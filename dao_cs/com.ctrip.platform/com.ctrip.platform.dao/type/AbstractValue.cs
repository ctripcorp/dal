using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    public abstract class AbstractValue : IValue
    {

        public virtual bool IsNullValue()
        {
            return false;
        }

        public virtual bool IsBoolValue()
        {
            return false;
        }

        public virtual bool IsIntegerValue()
        {
            return false;
        }

        public virtual bool IsFloatValue()
        {
            return false;
        }

        public virtual bool IsArrayValue()
        {
            return false;
        }

        public virtual bool IsMapValue()
        {
            return false;
        }

        public virtual bool IsRawValue()
        {
            return false;
        }



        public virtual ValueType GetType()
        {
            throw new NotImplementedException();
        }

        public virtual NullValue AsNullValue()
        {
            throw new NotImplementedException();
        }

        public virtual BoolValue AsBoolValue()
        {
            throw new NotImplementedException();
        }

        public virtual IntegerValue AsIntegerValue()
        {
            throw new NotImplementedException();
        }

        public virtual FloatValue AsFloatValue()
        {
            throw new NotImplementedException();
        }

        public virtual IRawValue AsRawValue()
        {
            throw new NotImplementedException();
        }

        public virtual ArrayValue AsArrayValue()
        {
            throw new NotImplementedException();
        }
    }
}
