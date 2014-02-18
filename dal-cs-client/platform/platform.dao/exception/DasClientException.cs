using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace platform.dao.exception
{
    public class DasClientException : Exception
    {
        public DasClientException() : base("DataBase Error.") {}
		public DasClientException(string errorMessage) : base(errorMessage) {}
        public DasClientException(string msgFormat, params object[] os) : base(String.Format(msgFormat, os)) { }
		protected DasClientException(SerializationInfo info, StreamingContext context) : base(info, context) {}
        public DasClientException(string message, Exception innerException) : base(message, innerException) { }
    }
}
