using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace platform.dao.exception
{
    public class DAOException : Exception
    {
        public DAOException() : base("DataBase Error.") {}
		public DAOException(string errorMessage) : base(errorMessage) {}
        public DAOException(string msgFormat, params object[] os) : base(String.Format(msgFormat, os)) { }
		protected DAOException(SerializationInfo info, StreamingContext context) : base(info, context) {}
        public DAOException(string message, Exception innerException) : base(message, innerException) { }
    }
}
