using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    class StringRawValue : AbstractRawValue
    {

        private string stringValue;

        internal StringRawValue(string stringValue)
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

    }
}
