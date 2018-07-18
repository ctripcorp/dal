using System;
using System.Runtime.Serialization;

namespace Arch.Data
{
    [Serializable]
    public class DalMarkDownException : DalException
    {
        public DalMarkDownException() : base("MarkDown Error.") { }

        public DalMarkDownException(String errorMessage) : base(errorMessage) { }

        public DalMarkDownException(String msgFormat, params Object[] os) : base(String.Format(msgFormat, os)) { }

        protected DalMarkDownException(SerializationInfo info, StreamingContext context) : base(info, context) { }

        public DalMarkDownException(String message, Exception innerException) : base(message, innerException) { }
    }
}
