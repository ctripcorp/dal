using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    public abstract class IntegerValue : AbstractValue
    {

        public override ValueType GetType()
        {
            return ValueType.INTEGER;
        }

        public override bool IsIntegerValue()
        {
            return true;
        }

        public override IntegerValue AsIntegerValue()
        {
            return this;
        }

        public abstract byte GetByte();

        public abstract short GetShort();

        public abstract int GetInt();

        public abstract long GetLong();

    }
}
