using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
    public interface IRawValue : IValue
    {
        byte[] GetByteArray();

        string GetString();

        bool IsBinary();

    }
}
