using Arch.Data.DbEngine.DB;
using System;
using System.Collections.Concurrent;
using System.Data.Common;
using System.Threading;
using System.Transactions;

namespace Arch.Data.DbEngine.Connection
{
    /// <summary>
    /// 对于相同的数据库,在同一个事务中,只打开一个数据库链接
    /// </summary>
    class TransactionConnectionManager
    {
        #region private field

        private static readonly ConcurrentDictionary<Transaction, ConcurrentDictionary<String, DbConnection>> TransactionConnections =
            new ConcurrentDictionary<Transaction, ConcurrentDictionary<String, DbConnection>>();

        [ThreadStatic]
        private static String connectionString;

        #endregion

        /// <summary>
        /// 获取数据库链接
        /// 当在同一个事务中的时候获取到的相同数据库的链接应该是一个对象
        /// 避免分布式事务,仅支持TransactionScope
        /// </summary>
        /// <param name="db"></param>
        /// <returns></returns>
        internal static DbConnection GetConnection(Database db)
        {
            var currentTransaction = Transaction.Current;
            if (currentTransaction == null) return null;

            if (String.IsNullOrEmpty(connectionString))
            {
                connectionString = db.ConnectionString;
            }
            else
            {
                if (connectionString != db.ConnectionString)
                {
                    connectionString = null;
                    throw new DalException("Distributed transaction is not supported.");
                }
            }

            return TransactionConnections.GetOrAdd(currentTransaction, tran =>
            {
                tran.TransactionCompleted += OnTransactionCompleted;
                return new ConcurrentDictionary<String, DbConnection>();
            }).GetOrAdd(db.ConnectionString, key =>
            {
                DbConnection connection = null;

                try
                {
                    connection = db.CreateConnection();
                    Interlocked.CompareExchange(ref db.ActualDatabaseName, connection.Database, null);

                    connection.Open();
                }
                catch
                {
                    if (connection != null) connection.Close();
                    throw;
                }

                return connection;
            });
        }

        /// <summary>
        /// 事务完成事件
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private static void OnTransactionCompleted(Object sender, TransactionEventArgs e)
        {
            connectionString = null;

            ConcurrentDictionary<String, DbConnection> connections;
            if (TransactionConnections.TryRemove(e.Transaction, out connections))
            {
                foreach (var connection in connections.Values)
                {
                    connection.Dispose();
                }
            }
        }

    }
}
