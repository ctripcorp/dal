using Arch.Data.Common.Enums;
using System;
using System.Collections.Generic;

namespace Arch.Data.Common.Util
{
    class TypeUtils
    {
        private static readonly IDictionary<Type, Func<Object, Object>> Dict = new Dictionary<Type, Func<Object, Object>>
        {
            { typeof (SByte), p => Convert.ToSByte(p) },
            { typeof (Byte), p => Convert.ToByte(p) },
            { typeof (Int16), p => Convert.ToInt16(p) },
            { typeof (UInt16), p => Convert.ToUInt16(p) },
            { typeof (Int32), p => Convert.ToInt32(p) },
            { typeof (UInt32), p => Convert.ToUInt32(p) },
            { typeof (Int64), p => Convert.ToInt64(p) },
            { typeof (UInt64), p => Convert.ToUInt64(p) },
            { typeof (Single), p => Convert.ToSingle(p) },
            { typeof (Double), p => Convert.ToDouble(p) },
            { typeof (Decimal), p => Convert.ToDecimal(p) }
        };

        /// <summary>
        /// 判断某数据是否是数字
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public static Boolean IsNumericType(Type type)
        {
            return Dict.ContainsKey(type);
        }

        /// <summary>
        /// 获取某数据是Int还是Long，或者是Double
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public static NumericType GetNumericTypeEnum(Type type)
        {
            switch (Type.GetTypeCode(type))
            {
                case TypeCode.Byte:
                case TypeCode.SByte:
                case TypeCode.UInt16:
                case TypeCode.UInt32:
                case TypeCode.Int16:
                case TypeCode.Int32:
                    return NumericType.Int;
                case TypeCode.UInt64:
                case TypeCode.Int64:
                    return NumericType.Long;
                case TypeCode.Decimal:
                case TypeCode.Double:
                case TypeCode.Single:
                    return NumericType.Double;
                default:
                    return NumericType.Int;
            }
        }

        public static T ChangeType<T>(Object obj)
        {
            if (obj is T)
                return (T)obj;

            try
            {
                return (T)Convert.ChangeType(obj, typeof(T));
            }
            catch (InvalidCastException)
            {
                return default(T);
            }
        }

        public static T GetNumericValue<T>(Type type, Object value)
        {
            Func<Object, Object> method;

            if (!Dict.TryGetValue(type, out method))
                return default(T);

            Object o = method.Invoke(value);
            return ChangeType<T>(o);
        }

    }
}
