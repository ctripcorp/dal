using System;
using System.Configuration;

namespace Arch.Data.Common.Logging.Configuration
{
    public sealed class ListenerElement : ConfigurationElement
    {
        #region private field

        /// <summary>
        /// 名称
        /// </summary>
        private const String c_NameProperty = "name";

        /// <summary>
        /// 类型
        /// </summary>
        private const String c_TypeProperty = "type";

        /// <summary>
        /// 日志级别
        /// </summary>
        private const String c_LevelProperty = "level";

        /// <summary>
        /// 初始化配置
        /// </summary>
        private const String c_SettingProperty = "setting";

        public ListenerElement() { }

        #endregion

        #region public properties

        /// <summary>
        /// 名称,关键字
        /// </summary>
        [ConfigurationProperty(c_NameProperty, IsRequired = true)]
        public String Name
        {
            get { return (String)this[c_NameProperty]; }
            set { this[c_NameProperty] = value; }
        }

        /// <summary>
        /// 级别
        /// </summary>
        [ConfigurationProperty(c_LevelProperty)]
        public LogLevel Level
        {
            get { return (LogLevel)this[c_LevelProperty]; }
            set { this[c_LevelProperty] = value; }
        }

        /// <summary>
        /// 类型名称
        /// </summary>
        [ConfigurationProperty(c_TypeProperty, DefaultValue = "")]
        public String TypeName
        {
            get { return (String)this[c_TypeProperty]; }
            set { this[c_TypeProperty] = value; }
        }

        /// <summary>
        /// 类型
        /// </summary>
        public Type Type
        {
            get { return Type.GetType(TypeName); }
            set { TypeName = value.AssemblyQualifiedName; }
        }

        /// <summary>
        /// 初始化配置
        /// </summary>
        [ConfigurationProperty(c_SettingProperty, DefaultValue = "")]
        public String Setting
        {
            get { return (String)this[c_SettingProperty]; }
            set { this[c_SettingProperty] = value; }
        }

        #endregion
    }
}
