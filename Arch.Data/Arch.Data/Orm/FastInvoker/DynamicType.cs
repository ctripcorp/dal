using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;

namespace Arch.Data.Orm.FastInvoker
{
    class DynamicType : IDynamicType
    {
        Type type;

        public DynamicType(Type type)
        {
            this.type = type;
        }

        public IDynamicConstructorInfo GetConstructor(Type[] types)
        {
            var info = type.GetConstructor(types);
            return info == null ? null : new DynamicConstructorInfo(type, info);
        }

        public IDynamicConstructorInfo GetConstructor(BindingFlags bindingAttr, Binder binder, Type[] types, ParameterModifier[] modifiers)
        {
            var info = type.GetConstructor(bindingAttr, binder, types, modifiers);
            return info == null ? null : new DynamicConstructorInfo(type, info);
        }

        public IDynamicConstructorInfo GetConstructor(BindingFlags bindingAttr, Binder binder, CallingConventions callConvention, Type[] types, ParameterModifier[] modifiers)
        {
            var info = type.GetConstructor(bindingAttr, binder, callConvention, types, modifiers);
            return info == null ? null : new DynamicConstructorInfo(type, info);
        }

        public IDynamicConstructorInfo[] GetConstructors()
        {
            IList<IDynamicConstructorInfo> list = new List<IDynamicConstructorInfo>();

            foreach (ConstructorInfo info in type.GetConstructors())
            {
                list.Add(new DynamicConstructorInfo(type, info));
            }

            return list.ToArray();
        }

        public IDynamicConstructorInfo[] GetConstructors(BindingFlags bindingAttr)
        {
            IList<IDynamicConstructorInfo> list = new List<IDynamicConstructorInfo>();

            foreach (ConstructorInfo info in type.GetConstructors(bindingAttr))
            {
                list.Add(new DynamicConstructorInfo(type, info));
            }

            return list.ToArray();
        }

        public IDynamicFieldInfo GetField(String name)
        {
            var info = type.GetField(name);
            return info == null ? null : new DynamicFieldInfo(type, info);
        }

        public IDynamicFieldInfo GetField(String name, BindingFlags bindingAttr)
        {
            var info = type.GetField(name, bindingAttr);
            return info == null ? null : new DynamicFieldInfo(type, info);
        }

        public IDynamicFieldInfo[] GetFields()
        {
            IList<IDynamicFieldInfo> list = new List<IDynamicFieldInfo>();

            foreach (FieldInfo info in type.GetFields())
            {
                list.Add(new DynamicFieldInfo(type, info));
            }

            return list.ToArray();
        }

        public IDynamicFieldInfo[] GetFields(BindingFlags bindingAttr)
        {
            IList<IDynamicFieldInfo> list = new List<IDynamicFieldInfo>();

            foreach (FieldInfo info in type.GetFields(bindingAttr))
            {
                list.Add(new DynamicFieldInfo(type, info));
            }

            return list.ToArray();
        }

        public IDynamicPropertyInfo GetProperty(String name)
        {
            var info = type.GetProperty(name);
            return info == null ? null : new DynamicPropertyInfo(type, info);
        }

        public IDynamicPropertyInfo GetProperty(String name, BindingFlags bindingAttr)
        {
            var info = type.GetProperty(name, bindingAttr);
            return info == null ? null : new DynamicPropertyInfo(type, info);
        }

        public IDynamicPropertyInfo GetProperty(String name, Type returnType)
        {
            var info = type.GetProperty(name, returnType);
            return info == null ? null : new DynamicPropertyInfo(type, info);
        }

        public IDynamicPropertyInfo GetProperty(String name, Type[] types)
        {
            var info = type.GetProperty(name, types);
            return info == null ? null : new DynamicPropertyInfo(type, info);
        }

        public IDynamicPropertyInfo GetProperty(String name, Type returnType, Type[] types)
        {
            var info = type.GetProperty(name, returnType, types);
            return info == null ? null : new DynamicPropertyInfo(type, info);
        }

        public IDynamicPropertyInfo GetProperty(String name, Type returnType, Type[] types, ParameterModifier[] modifiers)
        {
            var info = type.GetProperty(name, returnType, types, modifiers);
            return info == null ? null : new DynamicPropertyInfo(type, info);
        }

        public IDynamicPropertyInfo GetProperty(String name, BindingFlags bindingAttr, Binder binder, Type returnType, Type[] types, ParameterModifier[] modifiers)
        {
            var info = type.GetProperty(name, bindingAttr, binder, returnType, types, modifiers);
            return info == null ? null : new DynamicPropertyInfo(type, info);
        }

        public IDynamicPropertyInfo[] GetProperties()
        {
            IList<IDynamicPropertyInfo> list = new List<IDynamicPropertyInfo>();

            foreach (PropertyInfo info in type.GetProperties())
            {
                list.Add(new DynamicPropertyInfo(type, info));
            }

            return list.ToArray();
        }

        public IDynamicPropertyInfo[] GetProperties(BindingFlags bindingAttr)
        {
            IList<IDynamicPropertyInfo> list = new List<IDynamicPropertyInfo>();

            foreach (PropertyInfo info in type.GetProperties(bindingAttr))
            {
                list.Add(new DynamicPropertyInfo(type, info));
            }

            return list.ToArray();
        }
    }
}
