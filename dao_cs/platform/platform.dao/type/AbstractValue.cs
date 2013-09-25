using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
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

        public virtual NullValueImpl AsNullValue()
        {
            throw new NotImplementedException();
        }

        public virtual BoolValueImpl AsBoolValue()
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

        public virtual ArrayValueImpl AsArrayValue()
        {
            throw new NotImplementedException();
        }
    }
}
