using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
    public class BoolValueImpl : AbstractValue
    {

        private bool boolValue;

        internal BoolValueImpl(bool boolValue)
        {
            this.boolValue = boolValue;
        }

        public override ValueType GetType()
        {
            return ValueType.BOOL;
        }

        public override bool IsBoolValue()
        {
            return true;
        }

        public bool GetBoolValue()
        {
            return this.boolValue;
        }

        public override BoolValueImpl AsBoolValue()
        {
            return this;
        }

    }
}
