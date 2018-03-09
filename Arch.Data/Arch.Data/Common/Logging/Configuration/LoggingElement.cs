using System;
using System.Configuration;

namespace Arch.Data.Common.Logging.Configuration
{
    public sealed class LoggingElement : ConfigurationElement
    {
        /// <summary>
        /// 类型
        /// </summary>
        private const String type = "type";

        private const String logEntryType = "logEntryType";

        /// <summary>
        /// 类型
        /// </summary>
        [ConfigurationProperty(type)]
        public String TypeName
        {
            get { return (String)this[type]; }
            set { this[type] = value; }
        }

        [ConfigurationProperty(logEntryType)]
        public String LogEntryTypeName
        {
            get { return (String)this[logEntryType]; }
            set { this[logEntryType] = value; }
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

        public Type LogEntryType
        {
            get
            {
                try
                {
                    return Type.GetType(LogEntryTypeName);
                }
                catch
                {
                    return null;
                }
            }
            set { LogEntryTypeName = value.AssemblyQualifiedName; }
        }

    }
}
