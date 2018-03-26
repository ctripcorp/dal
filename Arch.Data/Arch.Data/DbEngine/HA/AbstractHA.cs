using Arch.Data.Common.Util;
using Arch.Data.Common.Vi;
using Arch.Data.DbEngine.DB;
using Arch.Data.Properties;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Data.Common;
using System.Transactions;

namespace Arch.Data.DbEngine.HA
{
    abstract class AbstractHA : IHA
    {
        protected IHABean haBean
        {
            get { return BeanManager.GetHABean(); }
        }

        public abstract HashSet<Int32> RetryFailOverErrorCodes { get; }

        private Int32 RetryFailOverTimes
        {
            get
            {
                Int32 retryTimes = haBean.RetryTimes;
                if (retryTimes < 1)
                    retryTimes = 1;
                return retryTimes;
            }
        }

        private Boolean SatisfyRetryFailOverCondition(DbException ex)
        {
            //如果使用了事务，不进行重试或者Fail Over
            if (Transaction.Current != null || ex == null)
                return false;

            Int32 errorCode = ExceptionUtil.GetDbExceptionErrorCode(ex);
            if (errorCode != 0 && RetryFailOverErrorCodes != null && RetryFailOverErrorCodes.Count > 0)
                return RetryFailOverErrorCodes.Contains(errorCode);
            return ExceptionUtil.IsTimeoutException(ex);
        }

        private Database FallToNextDatabase(Database current, IList<Database> candidates, BitArray bitArray)
        {
            var result = current;
            if (candidates != null && candidates.Count >= 0)
            {
                Int32 length = bitArray.Length;

                for (Int32 i = 0; i < length; i++)
                {
                    if (!bitArray[i] && candidates[i].Available)
                    {
                        bitArray[i] = true;
                        result = candidates[i];
                        break;
                    }
                }
            }

            return result;
        }

        public T ExecuteWithHa<T>(Func<Database, T> func, OperationalDatabases databases)
        {
            T result = default(T);
            BitArray bitArray = (databases.OtherCandidates != null && databases.OtherCandidates.Count > 0) ? new BitArray(databases.OtherCandidates.Count) : null;
            var currentOperateDatabase = databases.FirstCandidate;
            Int32 retryTimes = RetryFailOverTimes;

            try
            {
                ExecutorManager.Executor.Daemon();

                //被Mark Down了，且当前Request没有放行
                while (haBean.EnableHA && retryTimes > 0 && !currentOperateDatabase.Available)
                {
                    var fallbackDatabase = FallToNextDatabase(currentOperateDatabase, databases.OtherCandidates, bitArray);
                    if (fallbackDatabase == currentOperateDatabase)
                        throw new DalMarkDownException(String.Format(Resources.DBMarkDownException, currentOperateDatabase.AllInOneKey));
                    currentOperateDatabase = fallbackDatabase;
                    retryTimes--;
                }

                if (!currentOperateDatabase.Available)
                    throw new DalMarkDownException(String.Format(Resources.DBMarkDownException, currentOperateDatabase.AllInOneKey));
                result = func(currentOperateDatabase);
            }
            catch (DalMarkDownException)
            {
                throw;
            }
            catch (DbException ex)
            {
                if (!haBean.EnableHA)
                    throw;

                var exception = ex;
                Boolean failoverSucceed = false;
                String databaseSet = currentOperateDatabase.DatabaseSetName;
                String allInOneKey = currentOperateDatabase.AllInOneKey;

                while (retryTimes > 0)
                {
                    retryTimes--;

                    try
                    {
                        Boolean failoverNecessary = SatisfyRetryFailOverCondition(exception);
                        if (!failoverNecessary)
                            throw;

                        var failoverDatabase = FallToNextDatabase(currentOperateDatabase, databases.OtherCandidates, bitArray);
                        if (failoverDatabase == null)
                            throw;

                        allInOneKey = failoverDatabase.AllInOneKey;
                        result = func(failoverDatabase);
                        failoverSucceed = true;
                    }
                    catch (DbException exp)
                    {
                        exception = exp;
                        failoverSucceed = false;
                    }

                    if (failoverSucceed)
                    {
                        LogManager.Logger.MetricsFailover(databaseSet, allInOneKey);
                        break;
                    }
                }

                if (!failoverSucceed)
                    throw;
            }

            return result;
        }
    }
}
