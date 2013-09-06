using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    public abstract class AbstractRawValue : AbstractValue, IRawValue
    {

        public override ValueType GetType()
        {
            return ValueType.RAW;
        }

        public override bool IsRawValue()
        {
            return true;
        }

        public override IRawValue AsRawValue()
        {
            return this;
        }

        public abstract string GetString();

        public abstract byte[] GetByteArray();


    }
}
