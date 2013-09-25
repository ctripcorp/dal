using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.response
{
    public abstract class AbstractResponse : IResponse
    {
        protected int protocolVersion;

	    public int GetProtocolVersion() {
		    return protocolVersion;
	    }

        public virtual int GetPropertyCount()
        {
            throw new NotImplementedException();
        }

    }
}
