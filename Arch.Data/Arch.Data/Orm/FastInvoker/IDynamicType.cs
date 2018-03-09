using System;
using System.Reflection;

namespace Arch.Data.Orm.FastInvoker
{
    public interface IDynamicType
    {
        IDynamicConstructorInfo GetConstructor(Type[] types);

        IDynamicConstructorInfo GetConstructor(BindingFlags bindingAttr, Binder binder, Type[] types, ParameterModifier[] modifiers);

        IDynamicConstructorInfo GetConstructor(BindingFlags bindingAttr, Binder binder, CallingConventions callConvention, Type[] types, ParameterModifier[] modifiers);

        IDynamicConstructorInfo[] GetConstructors();

        IDynamicConstructorInfo[] GetConstructors(BindingFlags bindingAttr);

        IDynamicFieldInfo GetField(String name);

        IDynamicFieldInfo GetField(String name, BindingFlags bindingAttr);

        IDynamicFieldInfo[] GetFields();

        IDynamicFieldInfo[] GetFields(BindingFlags bindingAttr);

        IDynamicPropertyInfo GetProperty(String name);

        IDynamicPropertyInfo GetProperty(String name, BindingFlags bindingAttr);

        IDynamicPropertyInfo GetProperty(String name, Type returnType);

        IDynamicPropertyInfo GetProperty(String name, Type[] types);

        IDynamicPropertyInfo GetProperty(String name, Type returnType, Type[] types);

        IDynamicPropertyInfo GetProperty(String name, Type returnType, Type[] types, ParameterModifier[] modifiers);

        IDynamicPropertyInfo GetProperty(String name, BindingFlags bindingAttr, Binder binder, Type returnType, Type[] types, ParameterModifier[] modifiers);

        IDynamicPropertyInfo[] GetProperties();

        IDynamicPropertyInfo[] GetProperties(BindingFlags bindingAttr);
    }
}
