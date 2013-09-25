using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
    public class IntValueImpl : IntegerValue
    {

        private int intValue;
        private static readonly int BYTE_MAX = (int)byte.MaxValue;
        private static readonly int SHORT_MAX = (int)short.MaxValue;

        private static readonly int BYTE_MIN = (int)byte.MinValue;
        private static readonly int SHORT_MIN = (int)short.MinValue;


        internal IntValueImpl(int intValue)
        {
            this.intValue = intValue;
        }

        public override byte GetByte()
        {
            if (intValue > BYTE_MAX || intValue < BYTE_MIN)
            {
                throw new InvalidCastException();
            }
            return (byte)intValue;
        }

        public override short GetShort()
        {
            if (intValue > SHORT_MAX || intValue < SHORT_MIN)
            {
                throw new InvalidCastException();
            }
            return (short)intValue;
        }

        public override int GetInt()
        {
            return this.intValue;
        }

        public override long GetLong()
        {
            return this.intValue;
        }

    }
}
