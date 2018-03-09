using System;
using System.Configuration;

namespace Arch.Data.DbEngine.Configuration
{
    public sealed class ExecutorElement : ConfigurationElement
    {
        /// <summary>
        /// 类型
        /// </summary>
        private const String type = "type";

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
            get
            {
                try
                {
                    return Type.GetType(TypeName);
                }
                catch
                {
                    return null;
                }

            }
            set { TypeName = value.AssemblyQualifiedName; }
        }

    }
}
