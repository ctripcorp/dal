using System;
using System.Reflection.Emit;

namespace Arch.Data.Orm.FastInvoker
{
    class OpCodesFactory
    {
        /// <summary>
        /// 压入整数堆栈方法
        /// </summary>
        /// <param name="index"></param>
        /// <returns></returns>
        public static OpCode GetLdc_I4(Int32 index)
        {
            if (index == 0)
                return OpCodes.Ldc_I4_0;
            if (index == 1)
                return OpCodes.Ldc_I4_1;
            if (index == 2)
                return OpCodes.Ldc_I4_2;
            if (index == 3)
                return OpCodes.Ldc_I4_3;
            if (index == 4)
                return OpCodes.Ldc_I4_4;
            if (index == 5)
                return OpCodes.Ldc_I4_5;
            if (index == 6)
                return OpCodes.Ldc_I4_6;
            if (index == 7)
                return OpCodes.Ldc_I4_7;
            if (index == 8)
                return OpCodes.Ldc_I4_8;
            if (index > 8)
                return OpCodes.Ldc_I4_S;

            throw new Exception("unknown index to ldc_i4");
        }

        /// <summary>
        /// 根据传入的数据类型，获取数组元素的op
        /// </summary>
        /// <param name="reflectType"></param>
        /// <returns></returns>
        public static OpCode GetLdelem(Type reflectType)
        {
            if (!reflectType.IsValueType)
                return OpCodes.Ldelem_Ref;

            if (typeof(Byte).Equals(reflectType))
                return OpCodes.Ldelem_I1;

            if (typeof(Int16).Equals(reflectType))
                return OpCodes.Ldelem_I2;

            if (typeof(Int32).Equals(reflectType))
                return OpCodes.Ldelem_I4;

            if (typeof(Int64).Equals(reflectType))
                return OpCodes.Ldelem_I8;

            if (typeof(Single).Equals(reflectType))
                return OpCodes.Ldelem_R4;

            if (typeof(Double).Equals(reflectType))
                return OpCodes.Ldelem_R8;

            return OpCodes.Ldelem_Ref;
        }

        /// <summary>
        /// 判断是否需要装箱
        /// </summary>
        /// <param name="generator"></param>
        /// <param name="type"></param>
        public static void BoxIfNeeded(ILGenerator generator, Type type)
        {
            if (type.IsValueType) generator.Emit(OpCodes.Box, type);
        }

        /// <summary>
        /// 判断是否需要拆箱
        /// </summary>
        /// <param name="generator"></param>
        /// <param name="type"></param>
        public static void UnboxIfNeeded(ILGenerator generator, Type type)
        {
            if (type.IsValueType) generator.Emit(OpCodes.Unbox_Any, type);
        }
    }
}
