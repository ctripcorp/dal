using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace platform.dao.type
{
   public sealed class ValueFactory {
    public static NullValueImpl createNilValue() {
        return NullValueImpl.GetInstance();
    }

    public static BoolValueImpl createBooleanValue(bool v) {
        return new BoolValueImpl(v);
    }

    public static IntegerValue createIntegerValue(byte v) {
        return new IntValueImpl((int) v);
    }

    public static IntegerValue createIntegerValue(short v)
    {
        return new IntValueImpl((int) v);
    }

    public static IntegerValue createIntegerValue(int v)
    {
        return new IntValueImpl(v);
    }

    public static IntegerValue createIntegerValue(long v)
    {
        return new LongValueImpl(v);
    }

    public static FloatValue createFloatValue(float v) {
        return new FloatValueImpl(v);
    }

    public static FloatValue createFloatValue(double v) {
        return new DoubleValueImpl(v);
    }

    public static FloatValue createFloatValue(decimal v)
    {
        return new DecimalValueImpl(v);
    }

    public static IRawValue createRawValue(byte[] b) {
        return createRawValue(b, false);
    }

    public static IRawValue createRawValue(byte[] b, bool gift)
    {
        return new ByteArrayRawValueImpl(b, gift);
    }

    public static IRawValue createRawValue(byte[] b, int off, int len)
    {
        return new ByteArrayRawValueImpl(b, off, len);
    }

    public static IRawValue createRawValue(String s)
    {
        return new StringRawValueImpl(s);
    }

    public static ArrayValueImpl createArrayValue(IValue[] array) {
        return createArrayValue(array, false);
    }

    public static ArrayValueImpl createArrayValue(IValue[] array, bool gift) {
        return new ArrayValueImpl(array, gift);
    }
    // TODO
    // public static Value get(Object obj) {
    // return new Unconverter().pack(obj).getResult();
    // }

    private ValueFactory() {
    }
}

}
