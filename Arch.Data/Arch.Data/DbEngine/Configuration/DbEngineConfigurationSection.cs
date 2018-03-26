using Arch.Data.Common.Logging.Configuration;
using Arch.Data.Common.Metrics.Configuration;
using Arch.Data.Common.Tracing.Configuration;
using System;
using System.Configuration;

namespace Arch.Data.DbEngine.Configuration
{
    /// <summary>
    /// 数据库引擎配置节
    /// </summary>
    public sealed class DbEngineConfigurationSection : ConfigurationSection
    {
        #region private fields

        private readonly ConfigurationProperty databaseSets;

        private readonly ConfigurationProperty databaseProviders;

        private readonly ConfigurationProperty logListeners;

        private readonly ConfigurationProperty metrics;

        private readonly ConfigurationProperty tracing;

        private readonly ConfigurationProperty log;

        private readonly ConfigurationProperty logEntryType;

        private readonly ConfigurationProperty connectionLocator;

        private readonly ConfigurationProperty vi;

        private readonly ConfigurationProperty rw;

        private readonly ConfigurationProperty executor;

        #endregion

        #region construction

        /// <summary>
        /// 构造方法
        /// </summary>
        public DbEngineConfigurationSection()
        {
            databaseSets = new ConfigurationProperty("databaseSets", typeof(DatabaseSetElementCollection), null, ConfigurationPropertyOptions.None);
            databaseProviders = new ConfigurationProperty("databaseProviders", typeof(DatabaseProviderElementCollection), null, ConfigurationPropertyOptions.None);
            logListeners = new ConfigurationProperty("logListeners", typeof(ListenerElementCollection), null, ConfigurationPropertyOptions.None);
            metrics = new ConfigurationProperty("metrics", typeof(MetricsElement), null, ConfigurationPropertyOptions.None);
            tracing = new ConfigurationProperty("tracing", typeof(TracingElement), null, ConfigurationPropertyOptions.None);
            log = new ConfigurationProperty("log", typeof(LoggingElement), null, ConfigurationPropertyOptions.None);
            connectionLocator = new ConfigurationProperty("connectionLocator", typeof(ConnectionLocatorElement), null, ConfigurationPropertyOptions.None);
            rw = new ConfigurationProperty("rw", typeof(RWSplittingElement), null, ConfigurationPropertyOptions.None);
            executor = new ConfigurationProperty("executor", typeof(ExecutorElement), null, ConfigurationPropertyOptions.None);
        }

        #endregion

        #region get configuration

        /// <summary>
        /// Db引擎配置节名称
        /// </summary>
        private const String SectionName = "dal";

        /// <summary>
        /// 获取Db引擎配置节配置节
        /// </summary>
        /// <returns></returns>
        public static DbEngineConfigurationSection GetConfig()
        {
            return ConfigurationManager.GetSection(SectionName) as DbEngineConfigurationSection;
        }

        /// <summary>
        /// 名称,关键字
        /// </summary>
        [ConfigurationProperty("name", DefaultValue = "DALFx")]
        public String Name
        {
            get { return (String)base["name"]; }
        }

        #endregion

        #region public properties

        /// <summary>
        /// 数据库集配置数组
        /// </summary>
        [ConfigurationProperty("databaseSets")]
        public DatabaseSetElementCollection DatabaseSets
        {
            get { return (DatabaseSetElementCollection)base[databaseSets]; }
        }

        /// <summary>
        /// 数据库提供者配置数组
        /// </summary>
        [ConfigurationProperty("databaseProviders")]
        public DatabaseProviderElementCollection DatabaseProviders
        {
            get { return (DatabaseProviderElementCollection)base[databaseProviders]; }
        }

        /// <summary>
        /// 数据库提供者配置数组
        /// </summary>
        [ConfigurationProperty("logListeners")]
        public ListenerElementCollection LogListeners
        {
            get { return (ListenerElementCollection)base[logListeners]; }
        }

        /// <summary>
        /// 数据库提供者配置数组
        /// </summary>
        [ConfigurationProperty("metrics")]
        public MetricsElement Metrics
        {
            get { return (MetricsElement)base[metrics]; }
        }

        /// <summary>
        /// 数据库提供者配置数组
        /// </summary>
        [ConfigurationProperty("tracing")]
        public TracingElement Tracing
        {
            get { return (TracingElement)base[tracing]; }
        }

        /// <summary>
        /// Connection String
        /// </summary>
        [ConfigurationProperty("connectionLocator")]
        public ConnectionLocatorElement ConnectionLocator
        {
            get { return (ConnectionLocatorElement)base[connectionLocator]; }
        }

        /// <summary>
        /// Log
        /// </summary>
        [ConfigurationProperty("log")]
        public LoggingElement Log
        {
            get { return (LoggingElement)base[log]; }
        }

        [ConfigurationProperty("rw")]
        public RWSplittingElement RWSplitting
        {
            get { return (RWSplittingElement)base[rw]; }
        }

        public ExecutorElement Executor
        {
            get { return (ExecutorElement)base[executor]; }
        }

        #endregion
    }
}
