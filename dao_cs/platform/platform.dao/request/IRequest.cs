using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.request
{
    public interface IRequest
    {
        int GetProtocolVersion();

        byte[] Pack2ByteArray();

        int GetPropertyCount();
    }
}
