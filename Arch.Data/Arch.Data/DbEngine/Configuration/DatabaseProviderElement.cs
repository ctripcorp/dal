using System;
using System.Configuration;

namespace Arch.Data.DbEngine.Configuration
{
    /// <summary>
    /// 数据库提供者
    /// </summary>
    public sealed class DatabaseProviderElement : ConfigurationElement
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

        #endregion

        #region public properties

        /// <summary>
        /// 名称
        /// </summary>
        [ConfigurationProperty(c_NameProperty, IsKey = true)]
        public String Name
        {
            get { return (String)this[c_NameProperty]; }
            set { this[c_NameProperty] = value; }
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
        /// 类型
        /// </summary>
        [ConfigurationProperty(c_TypeProperty, IsRequired = true)]
        public String TypeName
        {
            get { return (String)this[c_TypeProperty]; }
            set { this[c_TypeProperty] = value; }
        }

        #endregion
    }
}
