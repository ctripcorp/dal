using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
    class StringRawValueImpl : AbstractRawValue
    {

        private string stringValue;

        internal StringRawValueImpl(string stringValue)
        {
            this.stringValue = stringValue;
        }

        public override byte[] GetByteArray()
        {
            return Encoding.UTF8.GetBytes(this.stringValue);
        }

        public override string GetString()
        {
            return this.stringValue;
        }

        public override bool IsBinary()
        {
            return false;
        }

    }
}
