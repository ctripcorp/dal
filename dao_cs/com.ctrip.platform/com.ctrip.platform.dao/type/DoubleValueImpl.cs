using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    class DoubleValueImpl : FloatValue
    {
        private double doubleValue;

        internal DoubleValueImpl(double doubleValue)
        {
            this.doubleValue = doubleValue;
        }

        public override float GetFloat()
        {
            return (float)this.doubleValue;
        }

        public override double GetDouble()
        {
            return this.doubleValue;
        }

        public override decimal GetDecimal()
        {
            return (decimal)this.doubleValue;
        }

    }
}
