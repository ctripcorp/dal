using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
    public class FloatValueImpl : FloatValue
    {

        private float floatValue;

        internal FloatValueImpl(float floatValue)
        {
            this.floatValue = floatValue;
        }

        public override float GetFloat()
        {
            return this.floatValue;
        }

        public override double GetDouble()
        {
            return (double)this.floatValue;
        }

        public override decimal GetDecimal()
        {
            return (decimal)this.floatValue;
        }

    }
}
