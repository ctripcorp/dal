using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    public abstract class FloatValue: AbstractValue
    {

        public override ValueType GetType()
        {
            return ValueType.FLOAT;
        }

        public override bool IsFloatValue()
        {
            return true;
        }

        public override FloatValue AsFloatValue()
        {
            return this;
        }

        public abstract float GetFloat();

        public abstract double GetDouble();

        public abstract decimal GetDecimal();

    }
}
