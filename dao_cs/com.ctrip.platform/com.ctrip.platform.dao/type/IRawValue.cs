using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    public interface IRawValue : IValue
    {
        byte[] GetByteArray();

        string GetString();

    }
}
