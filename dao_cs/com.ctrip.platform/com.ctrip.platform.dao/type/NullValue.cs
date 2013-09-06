using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    public class NullValue : AbstractValue
    {
        private NullValue()
        {
        }

        private static NullValue instance = new NullValue();

        internal static NullValue GetInstance()
        {
            return instance;
        }

        public override ValueType GetType()
        {
            return ValueType.NULL;
        }

        public override bool IsNullValue()
        {
            return true;
        }

        public override NullValue AsNullValue()
        {
            return this;
        }

    }
}
