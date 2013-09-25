using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
    public class LongValueImpl : IntegerValue
    {

        private long longValue;
        private static readonly long BYTE_MAX = (long)byte.MaxValue;
        private static readonly long SHORT_MAX = (long)short.MaxValue;
        private static readonly long INT_MAX = (long)int.MaxValue;

        private static readonly long BYTE_MIN = (long)byte.MinValue;
        private static readonly long SHORT_MIN = (long)short.MinValue;
        private static readonly long INT_MIN = (long)int.MinValue;

        internal LongValueImpl(long longValue)
        {
            this.longValue = longValue;
        }

        public override byte GetByte()
        {
            if (longValue > BYTE_MAX || longValue < BYTE_MIN)
            {
                throw new InvalidCastException();
            }
            return (byte)longValue;
        }

        public override short GetShort()
        {
            if (longValue > SHORT_MAX || longValue < SHORT_MIN)
            {
                throw new InvalidCastException();
            }
            return (short)longValue;
        }

        public override int GetInt()
        {
            if (longValue > INT_MAX || longValue < INT_MIN)
            {
                throw new InvalidCastException();
            }
            return (int)longValue;
        }

        public override long GetLong()
        {
            return this.longValue;
        }

    }
}
