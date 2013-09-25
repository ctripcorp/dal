using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
    /// <summary>
    /// 代替Object，存储SQL参数对应的数值
    /// </summary>
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

        NullValueImpl AsNullValue();

        BoolValueImpl AsBoolValue();

        IntegerValue AsIntegerValue();

        FloatValue AsFloatValue();

        IRawValue AsRawValue();

        ArrayValueImpl AsArrayValue();

    }
}
