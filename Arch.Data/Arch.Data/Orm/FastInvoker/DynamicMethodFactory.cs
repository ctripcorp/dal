using System;
using System.Reflection;
using System.Reflection.Emit;

namespace Arch.Data.Orm.FastInvoker
{
    class DynamicMethodFactory
    {
        /// <summary>
        /// 创建构造函数
        /// </summary>
        /// <param name="type"></param>
        /// <param name="constructorInfo"></param>
        /// <returns></returns>
        public static DynamicConstructorInfoHandler CreateDynamicConstructorInfoHandler(Type type, ConstructorInfo constructorInfo)
        {
            Int32 argIndex = 0;
            var dynamicMethod = new DynamicMethod("DynamicConstructor",
                MethodAttributes.Static | MethodAttributes.Public, CallingConventions.Standard, typeof(Object), new[] { typeof(Object[]) }, type, true);
            var generator = dynamicMethod.GetILGenerator();

            foreach (var parameter in constructorInfo.GetParameters())
            {
                generator.Emit(OpCodes.Ldarg_0);
                if (argIndex > 8)
                    generator.Emit(OpCodesFactory.GetLdc_I4(argIndex), argIndex);
                else
                    generator.Emit(OpCodesFactory.GetLdc_I4(argIndex));
                generator.Emit(OpCodes.Ldelem_Ref);
                OpCodesFactory.UnboxIfNeeded(generator, parameter.ParameterType);
                argIndex++;
            }
            generator.Emit(OpCodes.Newobj, constructorInfo);
            generator.Emit(OpCodes.Ret);
            return (DynamicConstructorInfoHandler)dynamicMethod.CreateDelegate(typeof(DynamicConstructorInfoHandler));
        }

        /// <summary>
        /// 创建PropertyInfo的动态方法get
        /// </summary>
        /// <param name="type"></param>
        /// <param name="propertyInfo"></param>
        /// <returns></returns>
        public static DynamicPropertyGetHandler CreateGetHandler(Type type, PropertyInfo propertyInfo)
        {
            var getMethodInfo = propertyInfo.GetGetMethod(true);
            Int32 argIndex = 0;
            var dynamicGet = new DynamicMethod("DynamicGet", typeof(Object), new[] { typeof(Object), typeof(Object[]) }, type, true);
            var getGenerator = dynamicGet.GetILGenerator();

            getGenerator.Emit(OpCodes.Ldarg_0);
            foreach (var parameter in getMethodInfo.GetParameters())
            {
                getGenerator.Emit(OpCodes.Ldarg_1);
                if (argIndex > 8)
                    getGenerator.Emit(OpCodesFactory.GetLdc_I4(argIndex), argIndex);
                else
                    getGenerator.Emit(OpCodesFactory.GetLdc_I4(argIndex));
                getGenerator.Emit(OpCodes.Ldelem_Ref);
                OpCodesFactory.UnboxIfNeeded(getGenerator, parameter.ParameterType);
                argIndex++;
            }
            getGenerator.Emit(OpCodes.Callvirt, getMethodInfo);
            OpCodesFactory.BoxIfNeeded(getGenerator, getMethodInfo.ReturnType);
            getGenerator.Emit(OpCodes.Ret);

            return (DynamicPropertyGetHandler)dynamicGet.CreateDelegate(typeof(DynamicPropertyGetHandler));
        }

        /// <summary>
        /// 创建PropertyInfo的动态方法Set
        /// </summary>
        /// <param name="type"></param>
        /// <param name="propertyInfo"></param>
        /// <returns></returns>
        public static DynamicPropertySetHandler CreateSetHandler(Type type, PropertyInfo propertyInfo)
        {
            var setMethodInfo = propertyInfo.GetSetMethod(true);
            Int32 argCount = setMethodInfo.GetParameters().Length;
            Int32 argIndex = 0;

            var dynamicSet = new DynamicMethod("DynamicSet", typeof(void), new[] { typeof(Object), typeof(Object), typeof(Object[]) }, type, true);
            var setGenerator = dynamicSet.GetILGenerator();

            setGenerator.Emit(OpCodes.Ldarg_0);
            foreach (var parameter in setMethodInfo.GetParameters())
            {
                if (argIndex + 1 >= argCount)
                    break;

                setGenerator.Emit(OpCodes.Ldarg_2);
                if (argIndex > 8)
                    setGenerator.Emit(OpCodesFactory.GetLdc_I4(argIndex), argIndex);
                else
                    setGenerator.Emit(OpCodesFactory.GetLdc_I4(argIndex));
                setGenerator.Emit(OpCodes.Ldelem_Ref);
                OpCodesFactory.UnboxIfNeeded(setGenerator, parameter.ParameterType);
                argIndex++;
            }

            setGenerator.Emit(OpCodes.Ldarg_1);
            OpCodesFactory.UnboxIfNeeded(setGenerator, setMethodInfo.GetParameters()[argIndex].ParameterType);
            setGenerator.Emit(OpCodes.Call, setMethodInfo);
            setGenerator.Emit(OpCodes.Ret);

            return (DynamicPropertySetHandler)dynamicSet.CreateDelegate(typeof(DynamicPropertySetHandler));
        }

        /// <summary>
        /// 创建Field的动态方法get
        /// </summary>
        /// <param name="type"></param>
        /// <param name="fieldInfo"></param>
        /// <returns></returns>
        public static DynamicFieldGetHandler CreateGetHandler(Type type, FieldInfo fieldInfo)
        {
            var dynamicGet = CreateGetDynamicMethod(type);
            var getGenerator = dynamicGet.GetILGenerator();

            getGenerator.Emit(OpCodes.Ldarg_0);
            getGenerator.Emit(OpCodes.Ldfld, fieldInfo);
            OpCodesFactory.BoxIfNeeded(getGenerator, fieldInfo.FieldType);
            getGenerator.Emit(OpCodes.Ret);

            return (DynamicFieldGetHandler)dynamicGet.CreateDelegate(typeof(DynamicFieldGetHandler));
        }

        /// <summary>
        /// 创建Field的动态方法set
        /// </summary>
        /// <param name="type"></param>
        /// <param name="fieldInfo"></param>
        /// <returns></returns>
        public static DynamicFieldSetHandler CreateSetHandler(Type type, FieldInfo fieldInfo)
        {
            var dynamicSet = CreateSetDynamicMethod(type);
            var setGenerator = dynamicSet.GetILGenerator();

            setGenerator.Emit(OpCodes.Ldarg_0);
            setGenerator.Emit(OpCodes.Ldarg_1);
            OpCodesFactory.UnboxIfNeeded(setGenerator, fieldInfo.FieldType);
            setGenerator.Emit(OpCodes.Stfld, fieldInfo);
            setGenerator.Emit(OpCodes.Ret);

            return (DynamicFieldSetHandler)dynamicSet.CreateDelegate(typeof(DynamicFieldSetHandler));
        }

        private static DynamicMethod CreateGetDynamicMethod(Type type)
        {
            return new DynamicMethod("DynamicGet", typeof(Object), new[] { typeof(Object) }, type, true);
        }

        private static DynamicMethod CreateSetDynamicMethod(Type type)
        {
            return new DynamicMethod("DynamicSet", typeof(void), new[] { typeof(Object), typeof(Object) }, type, true);
        }
    }
}

