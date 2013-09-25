using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.request
{
    public abstract class AbstractRequest : IRequest
    {
        protected int protocolVersion;

	    public int GetProtocolVersion() {
		    return protocolVersion;
	    }

        public virtual int GetPropertyCount()
        {
            throw new NotImplementedException();
        }

        public virtual byte[] Pack2ByteArray()
        {
            throw new NotImplementedException();
        }

    }
}
