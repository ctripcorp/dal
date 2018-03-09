using Arch.Data.Common.constant;
using Arch.Data.Common.Enums;
using Arch.Data.Common.Logging;
using Arch.Data.Common.Util;
using Arch.Data.DbEngine.Connection;
using Arch.Data.DbEngine.ConnectionString;
using Arch.Data.DbEngine.MarkDown;
using Arch.Data.DbEngine.Providers;
using System;
using System.Configuration;
using System.Data;
using System.Data.Common;
using System.Data.SqlClient;
using System.Diagnostics;
using System.Globalization;
using System.Threading;
using System.Transactions;

namespace Arch.Data.DbEngine.DB
{
    /// <summary>
    /// 物理数据库对象
    /// </summary>
    public sealed class Database
    {
        /// <summary>
        /// 数据库链接
        /// </summary>
        private String m_ConnectionString;

        private readonly ReaderWriterLockSlim connectionStringLock = new ReaderWriterLockSlim();

        /// <summary>
        /// 数据库提供者对象
        /// </summary>
        private readonly IDatabaseProvider m_DatabaseProvider;

        #region properties

        /// <summary>
        /// 数据库链接
        /// </summary>
        public String ConnectionString
        {
            get
            {
                connectionStringLock.EnterReadLock();
                String result = m_ConnectionString;
                connectionStringLock.ExitReadLock();
                return result;
            }
        }

        /// <summary>
        /// 真正的数据库名
        /// </summary>
        public volatile String ActualDatabaseName;

        /// <summary>
        /// 对应的AllInOne中的Key
        /// </summary>
        public String AllInOneKey { get; private set; }

        /// <summary>
        /// DalConfig中配置的DatabaseName
        /// </summary>
        public String DatabaseName { get; private set; }

        /// <summary>
        /// 数据库的类型，读库/写库
        /// </summary>
        public DatabaseType DatabaseRWType { get; set; }

        public String DatabaseSetName { get; private set; }

        /// <summary>
        /// 当前数据库是否处于可用状态
        /// </summary>
        public Boolean Available
        {
            get { return !AutoMarkDown.DatabaseMarkedDown(AllInOneKey, DatabaseSetName); }
        }

        #endregion

        #region construction

        /// <summary>
        /// 构造方法
        /// </summary>
        /// <param name="databaseSetName"></param>
        /// <param name="databaseName"></param>
        /// <param name="connectionStringName">数据库链接名称</param>
        /// <param name="databaseProvider">数据库提供者</param>
        public Database(String databaseSetName, String databaseName, String connectionStringName, IDatabaseProvider databaseProvider)
        {
            if (String.IsNullOrEmpty(connectionStringName))
                throw new ArgumentNullException("connectionStringName");
            if (String.IsNullOrEmpty(databaseSetName))
                throw new ArgumentNullException("databaseSet");
            if (String.IsNullOrEmpty(databaseName))
                throw new ArgumentNullException("databaseName");
            if (databaseProvider == null)
                throw new ArgumentNullException("databaseProvider");

            DatabaseSetName = databaseSetName;
            DatabaseName = databaseName;
            AllInOneKey = connectionStringName;
            m_DatabaseProvider = databaseProvider;
            LoadActualConnectionString();
        }

        /// <summary>
        /// 重新读取All In One中的连接串
        /// </summary>
        private void LoadActualConnectionString()
        {
            var connectionStringSetting = ConnectionLocatorManager.Instance.GetConnectionString(AllInOneKey);
            connectionStringLock.EnterWriteLock();
            m_ConnectionString = connectionStringSetting == null ? String.Empty : connectionStringSetting.ConnectionString;
            connectionStringLock.ExitWriteLock();
        }

        #endregion

        #region helper methods

        /// <summary>
        /// 获取打开的数据库链接
        /// </summary>
        /// <param name="disposeInnerConnection">是否释放数据库链接对象</param>
        /// <returns></returns>
        private ConnectionWrapper GetOpenConnection(Boolean disposeInnerConnection)
        {
            var connection = TransactionConnectionManager.GetConnection(this);
            if (connection != null)
                return new ConnectionWrapper(connection, false);

            try
            {
                connection = CreateConnection();
                Interlocked.CompareExchange(ref ActualDatabaseName, connection.Database, null);
                connection.Open();
            }
            catch
            {
                if (connection != null)
                    connection.Close();
                throw;
            }

            return new ConnectionWrapper(connection, disposeInnerConnection);
        }

        /// <summary>
        /// 创建数据库链接
        /// </summary>
        /// <returns></returns>
        public DbConnection CreateConnection()
        {
            if (String.IsNullOrEmpty(ConnectionString))
                throw new ConfigurationErrorsException(String.Format("ConnectionString:{0} can't be found!", AllInOneKey));

            var connection = m_DatabaseProvider.CreateConnection();
            connection.ConnectionString = ConnectionString;
            return connection;
        }

        /// <summary>
        /// 准备数据库指令
        /// </summary>
        /// <param name="statement">上层指令</param>
        /// <returns>数据库指令</returns>
        private DbCommand PrepareCommand(Statement statement)
        {
            DbCommand command = m_DatabaseProvider.CreateCommand();
            command.CommandText = statement.StatementText;
            command.CommandType = statement.StatementType == Arch.Data.Common.Enums.StatementType.Sql ? CommandType.Text : CommandType.StoredProcedure;
            command.CommandTimeout = statement.Timeout;
            String providerName = m_DatabaseProvider.GetType().Name;

            foreach (var p in statement.Parameters)
            {
                if (p.ExtendType == 1)
                {
                    var parameter = (SqlParameter)command.CreateParameter();
                    parameter.ParameterName = m_DatabaseProvider.CreateParameterName(p.Name);
                    parameter.SqlDbType = (SqlDbType)p.ExtendTypeValue;

                    if (!String.IsNullOrEmpty(p.TypeName))
                        parameter.TypeName = p.TypeName;
                    parameter.Size = p.Size;
                    parameter.Value = p.Value ?? DBNull.Value;
                    parameter.Direction = p.Direction;
                    parameter.IsNullable = p.IsNullable;
                    command.Parameters.Add(parameter);
                }
                else
                {
                    if (p.DbType == DbType.Time && providerName == "SqlDatabaseProvider")
                    {
                        var parameter = (SqlParameter)command.CreateParameter();
                        parameter.ParameterName = m_DatabaseProvider.CreateParameterName(p.Name);
                        parameter.SqlDbType = SqlDbType.Time;
                        parameter.Size = p.Size;
                        parameter.Value = p.Value ?? DBNull.Value;
                        parameter.Direction = p.Direction;
                        parameter.IsNullable = p.IsNullable;
                        command.Parameters.Add(parameter);
                    }
                    else
                    {
                        var parameter = command.CreateParameter();
                        parameter.ParameterName = m_DatabaseProvider.CreateParameterName(p.Name);
                        parameter.DbType = p.DbType;
                        parameter.Size = p.Size;
                        parameter.Value = p.Value ?? DBNull.Value;
                        parameter.Direction = p.Direction;
                        parameter.IsNullable = p.IsNullable;

                        if (providerName.Equals("MySqlDatabaseProvider"))
                        {
                            command.Parameters.Insert(-1, parameter);   //work around for legacy mysql driver versions
                        }
                        else
                        {
                            command.Parameters.Add(parameter);
                        }
                    }
                }
            }

            return command;
        }

        /// <summary>
        /// 更新执行后的参数
        /// </summary>
        /// <param name="statement">指令</param>
        /// <param name="command">数据库指令</param>
        private void UpdateStatementParamenters(Statement statement, DbCommand command)
        {
            foreach (var p in statement.Parameters)
            {
                if (p.Direction != ParameterDirection.Input)
                    p.Value = command.Parameters[m_DatabaseProvider.CreateParameterName(p.Name)].Value;
            }
        }

        /// <summary>
        /// 加载程序集
        /// </summary>
        /// <param name="statement">statement</param>
        /// <param name="command">指令</param>
        /// <param name="dataSet">程序集</param>
        /// <param name="tableNames">表名称</param>
        private void LoadDataSet(Statement statement, DbCommand command, DataSet dataSet, params String[] tableNames)
        {
            Boolean schemaRequired = false;
            Boolean disableConstraints = false;
            if (statement.Hints != null && statement.Hints.Contains(DALExtStatementConstant.RETRIEVE_SCHEMA))
            {
                schemaRequired = true;
                if (statement.Hints.Contains(DALExtStatementConstant.DISABLE_CONSTRAINTS))
                    disableConstraints = true;
            }

            if (tableNames == null || tableNames.Length == 0)
                tableNames = new[] { "Table" };

            for (Int32 i = 0; i < tableNames.Length; i++)
            {
                if (String.IsNullOrEmpty(tableNames[i]))
                    throw new ArgumentException(String.Concat("tableNames[", i, "]"));
            }

            using (var adapter = m_DatabaseProvider.CreateDataAdapter())
            {
                adapter.SelectCommand = command;

                for (Int32 i = 0; i < tableNames.Length; i++)
                {
                    String tableName = (i == 0) ? "Table" : "Table" + i;
                    adapter.TableMappings.Add(tableName, tableNames[i]);
                }

                if (schemaRequired)
                {
                    adapter.FillSchema(dataSet, SchemaType.Mapped);
                    if (disableConstraints)
                        dataSet.EnforceConstraints = false;
                }

                adapter.Fill(dataSet);
            }
        }

        #endregion

        /// <summary>
        /// 执行返回数据集指令
        /// </summary>
        /// <param name="statement">指令</param>
        /// <param name="tableNames">填充表名称</param>
        /// <returns>数据集</returns>
        public DataSet ExecuteDataSet(Statement statement, params String[] tableNames)
        {
            var watch = new Stopwatch();
            ILogEntry entry = null;

            try
            {
                DataSet dataSet = new DataSet { Locale = CultureInfo.InvariantCulture };
                statement.PreProcess(AllInOneKey, ActualDatabaseName, DatabaseRWType, m_DatabaseProvider, ConnectionString);
                LogManager.Logger.Init(entry, statement, DatabaseName, MethodType.ExecuteDataSet.ToString());
                LogManager.Logger.Start(entry, watch);

                using (DbCommand command = PrepareCommand(statement))
                {
                    using (var wrapper = GetOpenConnection(true))
                    {
                        command.Connection = wrapper.Connection;
                        LoadDataSet(statement, command, dataSet, tableNames);
                        UpdateStatementParamenters(statement, command);
                    }
                }

                LogManager.Logger.Success(entry, statement, watch,
                     () => (dataSet.Tables.Count == 0) ? 0 : dataSet.Tables[0].Rows.Count);
                return dataSet;
            }
            catch (DbException ex)
            {
                LogManager.Logger.Error(ex, entry, statement, watch);
                throw;
            }
            catch (Exception ex)
            {
                LogManager.Logger.Error(ex, entry, statement, watch);
                throw;
            }
            finally
            {
                LogManager.Logger.Complete(entry);
            }
        }

        /// <summary>
        /// 执行非查询指令
        /// </summary>
        /// <param name="statement">指令</param>
        /// <returns>影响行数</returns>
        public Int32 ExecuteNonQuery(Statement statement)
        {
            var watch = new Stopwatch();
            ILogEntry entry = null;

            try
            {
                Int32 result;
                statement.PreProcess(AllInOneKey, ActualDatabaseName, DatabaseRWType, m_DatabaseProvider, ConnectionString);
                LogManager.Logger.Init(entry, statement, DatabaseName, MethodType.ExecuteNonQuery.ToString());
                LogManager.Logger.Start(entry, watch);

                using (DbCommand command = PrepareCommand(statement))
                {
                    using (var wrapper = GetOpenConnection(true))
                    {
                        command.Connection = wrapper.Connection;
                        result = command.ExecuteNonQuery();
                        UpdateStatementParamenters(statement, command);
                    }
                }

                LogManager.Logger.Success(entry, statement, watch, () => result);
                return result;
            }
            catch (DbException ex)
            {
                LogManager.Logger.Error(ex, entry, statement, watch);
                throw;
            }
            catch (Exception ex)
            {
                LogManager.Logger.Error(ex, entry, statement, watch);
                throw;
            }
            finally
            {
                LogManager.Logger.Complete(entry);
            }
        }

        /// <summary>
        /// 执行返回单向只读数据集的指令
        /// </summary>
        /// <param name="statement">指令</param>
        /// <returns>单向只读DataReader对象</returns>
        public IDataReader ExecuteReader(Statement statement)
        {
            var watch = new Stopwatch();
            ILogEntry entry = null;

            try
            {
                IDataReader reader;
                statement.PreProcess(AllInOneKey, ActualDatabaseName, DatabaseRWType, m_DatabaseProvider, ConnectionString);
                LogManager.Logger.Init(entry, statement, DatabaseName, MethodType.ExecuteReader.ToString());
                LogManager.Logger.Start(entry, watch);

                using (DbCommand command = PrepareCommand(statement))
                {
                    using (var wrapper = GetOpenConnection(false))
                    {
                        command.Connection = wrapper.Connection;
                        reader = command.ExecuteReader(Transaction.Current != null ? CommandBehavior.Default : CommandBehavior.CloseConnection);
                        UpdateStatementParamenters(statement, command);
                    }
                }

                LogManager.Logger.Success(entry, statement, watch, null);
                return reader;
            }
            catch (DbException ex)
            {
                LogManager.Logger.Error(ex, entry, statement, watch);
                throw;
            }
            catch (Exception ex)
            {
                LogManager.Logger.Error(ex, entry, statement, watch);
                throw;
            }
            finally
            {
                LogManager.Logger.Complete(entry);
            }
        }

        /// <summary>
        /// 执行单返回值聚集查询指令
        /// </summary>
        /// <param name="statement">指令</param>
        /// <returns>聚集结果</returns>
        public Object ExecuteScalar(Statement statement)
        {
            var watch = new Stopwatch();
            ILogEntry entry = null;

            try
            {
                Object result;
                statement.PreProcess(AllInOneKey, ActualDatabaseName, DatabaseRWType, m_DatabaseProvider, ConnectionString);
                LogManager.Logger.Init(entry, statement, DatabaseName, MethodType.ExecuteScalar.ToString());
                LogManager.Logger.Start(entry, watch);

                using (DbCommand command = PrepareCommand(statement))
                {
                    using (var wrapper = GetOpenConnection(true))
                    {
                        command.Connection = wrapper.Connection;
                        result = command.ExecuteScalar();
                        UpdateStatementParamenters(statement, command);
                    }
                }

                LogManager.Logger.Success(entry, statement, watch, () => result == null ? 0 : 1);
                return result;
            }
            catch (DbException ex)
            {
                LogManager.Logger.Error(ex, entry, statement, watch);
                throw;
            }
            catch (Exception ex)
            {
                LogManager.Logger.Error(ex, entry, statement, watch);
                throw;
            }
            finally
            {
                LogManager.Logger.Complete(entry);
            }
        }

        /// <summary>
        /// 执行返回单向只读数据集的指令
        /// </summary>
        /// <param name="statement">指令</param>
        /// <returns>单向只读DataReader对象</returns>
        public IDataReader InnnerExecuteReader(Statement statement)
        {
            var watch = new Stopwatch();

            try
            {
                IDataReader reader;
                statement.SQLHash = CommonUtil.GetHashCodeOfSQL(statement.StatementText);
                if (statement.StatementType == Arch.Data.Common.Enums.StatementType.Sql)
                    statement.StatementText = CommonUtil.GetTaggedAppIDSql(statement.StatementText);
                watch.Start();

                using (DbCommand command = PrepareCommand(statement))
                {
                    using (var wrapper = GetOpenConnection(false))
                    {
                        command.Connection = wrapper.Connection;
                        reader = command.ExecuteReader(Transaction.Current != null ? CommandBehavior.Default : CommandBehavior.CloseConnection);
                        UpdateStatementParamenters(statement, command);
                    }
                }

                watch.Stop();
                statement.Duration = TimeSpan.FromMilliseconds(watch.ElapsedMilliseconds);
                statement.ExecStatus = DALState.Success;
                LogManager.Logger.MetricsRW(statement.DatabaseSet, DatabaseName, true);
                return reader;
            }
            catch
            {
                statement.ExecStatus = DALState.Fail;
                watch.Stop();
                statement.Duration = TimeSpan.FromMilliseconds(watch.ElapsedMilliseconds);
                LogManager.Logger.MetricsRW(statement.DatabaseSet, DatabaseName, false);
                return null;
            }
        }

        /// <summary>
        /// 执行单返回值聚集查询指令
        /// </summary>
        /// <param name="statement">指令</param>
        /// <returns>聚集结果</returns>
        public Object InnnerExecuteScalar(Statement statement)
        {
            var watch = new Stopwatch();

            try
            {
                statement.SQLHash = CommonUtil.GetHashCodeOfSQL(statement.StatementText);
                if (statement.StatementType == Arch.Data.Common.Enums.StatementType.Sql)
                    statement.StatementText = CommonUtil.GetTaggedAppIDSql(statement.StatementText);

                watch.Start();
                Object result;

                using (var command = PrepareCommand(statement))
                {
                    using (var wrapper = GetOpenConnection(true))
                    {
                        command.Connection = wrapper.Connection;
                        result = command.ExecuteScalar();
                        UpdateStatementParamenters(statement, command);
                    }
                }

                watch.Stop();
                statement.Duration = TimeSpan.FromMilliseconds(watch.ElapsedMilliseconds);
                statement.ExecStatus = DALState.Success;
                statement.RecordCount = result == null ? 0 : 1;
                LogManager.Logger.MetricsRW(statement.DatabaseSet, DatabaseName, true);
                return result;
            }
            catch
            {
                statement.ExecStatus = DALState.Fail;
                watch.Stop();
                statement.Duration = TimeSpan.FromMilliseconds(watch.ElapsedMilliseconds);
                LogManager.Logger.MetricsRW(statement.DatabaseSet, DatabaseName, false);
                return null;
            }
        }

    }
}
