using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace platform.dao.exception
{
    public class DAOConfigException : Exception
    {
         public DAOConfigException() : base("DataBase Error.") {}
		public DAOConfigException(string errorMessage) : base(errorMessage) {}
        public DAOConfigException(string msgFormat, params object[] os) : base(String.Format(msgFormat, os)) { }
		protected DAOConfigException(SerializationInfo info, StreamingContext context) : base(info, context) {}
        public DAOConfigException(string message, Exception innerException) : base(message, innerException) { }
    }
}
