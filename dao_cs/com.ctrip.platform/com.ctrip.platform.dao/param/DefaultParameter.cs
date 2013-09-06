using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MsgPack;
using com.ctrip.platform.dao.type;

namespace com.ctrip.platform.dao.param
{
    internal class DefaultParameter
    {

        private int parameterIndex;

        private ParameterType parameterType;

        private IValue value;

        /**
	 * Initialize AvailableType with a bool value
	 * 
	 * @param paramIndex
	 * @param value
	 */
        public DefaultParameter(int parameterIndex, bool value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.BOOL;
            this.value = ValueFactory.createBooleanValue(value);
        }

        /**
         * Initialize AvailableType with a byte value
         * 
         * @param paramIndex
         * @param value
         */
        public DefaultParameter(int parameterIndex, byte value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.BYTE;
            this.value = ValueFactory.createIntegerValue(value);
        }

        /**
         * Initialize AvailableType with a short value
         * 
         * @param paramIndex
         * @param value
         */
        public DefaultParameter(int parameterIndex, short value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.SHORT;
            this.value = ValueFactory.createIntegerValue(value);
        }

        /**
         * Initialize AvailableType with an int value
         * 
         * @param paramIndex
         * @param value
         */
        public DefaultParameter(int parameterIndex, int value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.INT;
            this.value = ValueFactory.createIntegerValue(value);
        }

        public DefaultParameter(int parameterIndex, int[] values)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.INTARRAY;
            IValue[] resultValues = new IValue[values.Length];
            for (int i = 0; i < values.Length; i++)
            {
                resultValues[i] = ValueFactory.createIntegerValue(values[i]);
            }
            this.value = ValueFactory.createArrayValue(resultValues);
        }

        /**
         * Initialize DefaultParameter with a long value
         * 
         * @param paramIndex
         * @param value
         */
        public DefaultParameter(int parameterIndex, long value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.LONG;
            this.value = ValueFactory.createIntegerValue(value);
        }

        /**
         * Initialize AvailaDefaultParametera float value
         * 
         * @param paramIndex
         * @param value
         */
        public DefaultParameter(int parameterIndex, float value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.FLOAT;
            this.value = ValueFactory.createFloatValue(value);
        }

        /**
         * Initialize AvailableType with a double value
         * 
         * @param paramIndex
         * @param value
         */
        public DefaultParameter(int parameterIndex, double value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.DOUBLE;
            this.value = ValueFactory.createFloatValue(value);
        }

        /**
         * Initialize AvailableType with a BigDecimal value
         * 
         * @param paramIndex
         * @param value
         */
        public DefaultParameter(int parameterIndex, decimal value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.DECIMAL;
            this.value = ValueFactory.createFloatValue(value);
        }

        /**
         * Initialize DefaultParameter with a String value
         * 
         * @param paramIndex
         * @param value
         */
        public DefaultParameter(int parameterIndex, String value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.STRING;
            this.value = ValueFactory.createRawValue(value);
        }

        public DefaultParameter(int parameterIndex, String[] values)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.STRINGARRAY;
            IValue[] resultValues = new IValue[values.Length];
            for (int i = 0; i < values.Length; i++)
            {
                resultValues[i] = ValueFactory.createRawValue(values[i]);
            }
            this.value = ValueFactory.createArrayValue(resultValues);
        }

        /**
         * Initialize DefaultParameter with a Timestamp value
         * 
         * @param paramIndex
         * @param value
         */
        public DefaultParameter(int parameterIndex, DateTime value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.DATETIME;
            this.value = ValueFactory.createFloatValue((float)value.Ticks / 10000);
        }

        /**
         * Initialize DefaultParameter with a byte[] value
         * 
         * @param paramIndex
         * @param value
         */
        public DefaultParameter(int parameterIndex, byte[] value)
        {
            this.parameterIndex = parameterIndex;
            this.parameterType = ParameterType.BYTEARRAY;
            this.value = ValueFactory.createRawValue(value);
        }

        public void pack(Packer packer)
        {
            packer.PackArrayHeader(3);
            packer.Pack(parameterIndex);
            packer.Pack((int)parameterType);
            switch (parameterType)
            {
                case ParameterType.NULL:
                    packer.PackNull();
                    break;
                case ParameterType.BOOL:
                    packer.Pack(value.AsBoolValue().GetBoolValue());
                    break;
                case ParameterType.BYTE:
                    packer.Pack(value.AsIntegerValue().GetByte());
                    break;
                case ParameterType.SHORT:
                    packer.Pack(value.AsIntegerValue().GetShort());
                    break;
                case ParameterType.INT:
                    packer.Pack(value.AsIntegerValue().GetInt());
                    break;
                case ParameterType.LONG:
                    packer.Pack(value.AsIntegerValue().GetLong());
                    break;
                case ParameterType.FLOAT:
                    packer.Pack(value.AsFloatValue().GetFloat());
                    break;
                case ParameterType.DOUBLE:
                    packer.Pack(value.AsFloatValue().GetDouble());
                    break;
                case ParameterType.DECIMAL:
                    packer.Pack(value.AsFloatValue().GetDecimal());
                    break;
                case ParameterType.STRING:
                    packer.Pack(value.AsRawValue().GetString());
                    break;
                case ParameterType.DATETIME:
                    packer.Pack(value.AsFloatValue().GetFloat());
                    break;
                case ParameterType.BYTEARRAY:
                    packer.Pack(value.AsRawValue().GetByteArray());
                    break;
                case ParameterType.INTARRAY:
                    IValue[] arrayValue = value.AsArrayValue().GetElementArray();
                    packer.PackArrayHeader(arrayValue.Length);
                    foreach (IValue v in arrayValue)
                    {
                        packer.Pack(v.AsIntegerValue().GetInt());
                    }
                    break;
                case ParameterType.STRINGARRAY:
                    IValue[] stringArrayValue = value.AsArrayValue().GetElementArray();
                    packer.PackArrayHeader(stringArrayValue.Length);
                    foreach (IValue v in stringArrayValue)
                    {
                        packer.Pack(v.AsRawValue().GetString());
                    }
                    break;
            }
        }

    }
}
