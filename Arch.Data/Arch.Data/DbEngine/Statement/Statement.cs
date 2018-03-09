using Arch.Data.Common.constant;
using Arch.Data.Common.Enums;
using Arch.Data.Common.Util;
using Arch.Data.DbEngine.Providers;
using System;
using System.Collections;
using System.Text.RegularExpressions;
using System.Transactions;

namespace Arch.Data.DbEngine
{
    /// <summary>
    /// 指令
    /// </summary>
    public sealed class Statement
    {
        #region fields

        /// <summary>
        /// 参数集合
        /// </summary>
        private StatementParameterCollection m_Parameters;

        #endregion

        #region properties

        /// <summary>
        /// 指令内容
        /// </summary>
        public String StatementText { get; set; }

        /// <summary>
        /// 指令内容的Hash值（MD5 - base64)
        /// </summary>
        public String SQLHash { get; set; }

        private Int32 cacheKey = 0;

        /// <summary>
        /// 指令内容的缓存key
        /// </summary>
        public Int32 SQLCacheKey
        {
            get
            {
                if (StatementText == null)
                    return 0;
                if (cacheKey != 0)
                    return cacheKey;
                String sqltext = StatementText;

                if (StatementType == StatementType.Sql)
                {
                    String[] temp = Regex.Split(StatementText, "where", RegexOptions.IgnoreCase);
                    if (temp.Length > 0)
                        sqltext = temp[0];
                }

                cacheKey = sqltext.GetHashCode();
                return cacheKey;
            }
        }

        /// <summary>
        /// 持续时间
        /// </summary>
        public TimeSpan Duration { get; set; }

        /// <summary>
        /// 执行是否成功
        /// </summary>
        public DALState ExecStatus { get; set; }

        /// <summary>
        /// 调用者信息 - 类名
        /// </summary>
        public String Invoker { get; set; }

        /// <summary>
        /// 调用者信息 - 方法签名
        /// </summary>
        public String InvokeMethod { get; set; }

        /// <summary>
        /// 指令类型
        /// Sql-Sql语句
        /// StoreProcedure-存贮过程
        /// </summary>
        public StatementType StatementType { get; set; }

        /// <summary>
        /// 逻辑数据库
        /// </summary>
        public String DatabaseSet { get; set; }

        /// <summary>
        /// All in one的Key名称
        /// </summary>
        public String AllInOneKey { get; set; }

        public String ConnectionString { get; set; }

        public DatabaseProviderType ProviderType
        {
            get
            {
                if (String.IsNullOrEmpty(DatabaseSet))
                    throw new InvalidOperationException("You must set DatabaseSet first before you can get the database provider type!");

                return DALBootstrap.GetProviderType(DatabaseSet);
            }
        }

        public IDatabaseProvider DatabaseProvider { get; set; }

        /// <summary>
        /// 分区标识
        /// </summary>
        public String ShardID { get; set; }

        /// <summary>
        /// 指令操作类型
        /// Default-未指定,默认在master库执行
        /// Read-读操作,在slave库执行
        /// Write-写操作,在master库执行
        /// </summary>
        public OperationType OperationType { get; set; }

        private Int32 _timeout = 30;

        /// <summary>
        /// 指令超时时长
        /// </summary>
        public Int32 Timeout
        {
            get
            {
                if (Hints != null && Hints.Count > 0 && Hints.Contains(DALExtStatementConstant.TIMEOUT))
                    Int32.TryParse(Hints[DALExtStatementConstant.TIMEOUT].ToString(), out _timeout);

                return _timeout;
            }
            set
            {
                _timeout = value;
            }
        }

        private Boolean isSensitive = false;

        /// <summary>
        /// 是否包含敏感信息
        /// 如果包含敏感信息则忽略日志
        /// </summary>
        public Boolean IsSensitive
        {
            get
            {
                if (Hints != null && Hints.Count > 0 && Hints.Contains(DALExtStatementConstant.SENSITIVE) && Hints[DALExtStatementConstant.SENSITIVE].Equals("yes"))
                {
                    return true;
                }
                else
                {
                    return isSensitive;
                }
            }
            set
            {
                isSensitive = value;
            }
        }

        /// <summary>
        /// 扩展属性
        /// </summary>
        public IDictionary Hints { get; set; }

        /// <summary>
        /// 指令参数集
        /// </summary>
        public StatementParameterCollection Parameters
        {
            get
            {
                if (m_Parameters == null)
                    m_Parameters = new StatementParameterCollection();

                return m_Parameters;
            }
            set
            {
                m_Parameters = value;
            }
        }

        /// <summary>
        /// 该Statement影响的行数
        /// </summary>
        public Int32 RecordCount { get; set; }

        public String ActualDatabaseName { get; set; }

        public Boolean InTransaction { get; set; }

        public DatabaseType DatabaseRWType { get; set; }

        public String TableName { get; set; }

        public SqlStatementType SqlOperationType { get; set; }

        #endregion

        #region constructor

        /// <summary>
        /// 构造方法
        /// </summary>
        public Statement()
        {
            OperationType = OperationType.Default;
            StatementType = StatementType.Sql;
            Timeout = 30;
            ShardID = String.Empty;
            IsSensitive = false;
        }

        #endregion

        #region Helper

        public void PreProcess(String allInOneDatabaseName, String actualDatabaseName, DatabaseType databaseRWType, IDatabaseProvider databaseProvider, String connectionString)
        {
            AllInOneKey = allInOneDatabaseName;
            ActualDatabaseName = actualDatabaseName;
            DatabaseRWType = databaseRWType;
            DatabaseProvider = databaseProvider;
            ConnectionString = connectionString;
            InTransaction = Transaction.Current != null;
            SQLHash = CommonUtil.GetHashCodeOfSQL(StatementText);

            if (StatementType == StatementType.Sql)
                StatementText = CommonUtil.GetTaggedAppIDSql(StatementText);
        }

        /// <summary>
        /// 验证指令
        /// </summary>
        public void Validate()
        {
            if (String.IsNullOrEmpty(DatabaseSet))
                throw new InvalidOperationException("Please provide database set name.");
            if (String.IsNullOrEmpty(StatementText))
                throw new InvalidOperationException("Please provide statement text.");
        }

        #endregion

    }
}
