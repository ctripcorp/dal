using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
    class DecimalValueImpl : FloatValue
    {
        private decimal decimalValue;

        internal DecimalValueImpl(decimal decimalValue)
        {
            this.decimalValue = decimalValue;
        }

        public override float GetFloat()
        {
            return (float)this.decimalValue;
        }

        public override double GetDouble()
        {
            return (double)this.decimalValue;
        }

        public override decimal GetDecimal()
        {
            return this.decimalValue;
        }


    }
}
