using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
    public class NullValueImpl : AbstractValue
    {
        private NullValueImpl()
        {
        }

        private static NullValueImpl instance = new NullValueImpl();

        internal static NullValueImpl GetInstance()
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

        public override NullValueImpl AsNullValue()
        {
            return this;
        }

    }
}
