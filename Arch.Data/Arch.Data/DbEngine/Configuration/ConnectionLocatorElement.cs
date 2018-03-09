using System;
using System.Configuration;

namespace Arch.Data.DbEngine.Configuration
{
    public sealed class ConnectionLocatorElement : ConfigurationElement
    {
        /// <summary>
        /// 类型
        /// </summary>
        private const String type = "type";

        private const String path = "path";

        /// <summary>
        /// 类型
        /// </summary>
        [ConfigurationProperty(type)]
        public String TypeName
        {
            get { return (String)this[type]; }
            set { this[type] = value; }
        }

        /// <summary>
        /// 类型
        /// </summary>
        public Type Type
        {
            get { return Type.GetType(TypeName); }
            set { TypeName = value.AssemblyQualifiedName; }
        }

        [ConfigurationProperty(path)]
        public String Path
        {
            get { return (String)this[path]; }
            set { this[path] = value; }
        }

    }
}
