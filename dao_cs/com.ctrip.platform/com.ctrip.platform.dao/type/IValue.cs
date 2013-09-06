using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace com.ctrip.platform.dao.type
{
    public interface IValue
    {
        ValueType GetType();

        bool IsNullValue();

        bool IsBoolValue();

        bool IsIntegerValue();

        bool IsFloatValue();

        bool IsArrayValue();

        bool IsMapValue();

        bool IsRawValue();

        NullValue AsNullValue();

        BoolValue AsBoolValue();

        IntegerValue AsIntegerValue();

        FloatValue AsFloatValue();

        IRawValue AsRawValue();

        ArrayValue AsArrayValue();

    }
}
