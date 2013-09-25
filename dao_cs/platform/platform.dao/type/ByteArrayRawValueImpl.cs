using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
    class ByteArrayRawValueImpl : AbstractRawValue
    {

         private byte[] bytes;

        internal ByteArrayRawValueImpl(byte[] bytes, bool gift)
        {
            if (gift)
            {
                this.bytes = bytes;
            }
            else
            {
                this.bytes = new byte[bytes.Length];
                Array.Copy(bytes, 0, this.bytes, 0, bytes.Length);
            }
        }

         internal ByteArrayRawValueImpl(byte[] bytes, int offset, int length)
         {
             this.bytes = new byte[length];
             Array.Copy(bytes, offset, this.bytes, 0, length);
         }

         public override byte[] GetByteArray()
        {
            return bytes;
        }

         public override string GetString()
        {
            return Encoding.Default.GetString(bytes);
        }

         public override bool IsBinary()
         {
             return true;
         }

    }
}
