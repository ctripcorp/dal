using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    public class BoolValue : AbstractValue
    {

        private bool boolValue;

        internal BoolValue(bool boolValue)
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

        public override BoolValue AsBoolValue()
        {
            return this;
        }

    }
}
