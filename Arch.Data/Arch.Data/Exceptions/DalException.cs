using System;
using System.Runtime.Serialization;

namespace Arch.Data
{
    [Serializable]
    public class DalException : Exception
    {
        public DalException() : base("Database Error.") { }

        public DalException(String errorMessage) : base(errorMessage) { }

        public DalException(String msgFormat, params Object[] os) : base(String.Format(msgFormat, os)) { }

        protected DalException(SerializationInfo info, StreamingContext context) : base(info, context) { }

        public DalException(String message, Exception innerException) : base(message, innerException) { }
    }
}
