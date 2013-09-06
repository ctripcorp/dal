using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    public class ArrayValue : AbstractValue
    {
        
        private IValue[] array;

        internal ArrayValue(IValue[] array, bool gift)
        {
            if (gift)
            {
                
                this.array = array;
            }
            else
            {
                this.array = new IValue[array.Length];
                Array.Copy(array, 0, this.array, 0, array.Length);
            }
        }

        public override bool IsArrayValue()
        {
            return true;
        }

        public override ArrayValue AsArrayValue()
        {
            return this;
        }

        public IValue[] GetElementArray()
        {
            return array;
        }

        public IValue Get(int index)
        {
            return array[index];
        }

    }
}
