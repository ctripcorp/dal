using Arch.Data.Orm.FastInvoker;
using System;
using System.Reflection;

namespace Arch.Data.Orm.sql
{
    class PropertyBridge : IDataBridge
    {
        /// <summary>
        /// 直接赋值
        /// </summary>
        private PropertyInfo propertyInfo;

        /// <summary>
        /// 动态方法+emit
        /// </summary>
        private IDynamicPropertyInfo dynamicPropertyInfo;

        public PropertyBridge(PropertyInfo property)
        {
            this.propertyInfo = property;
            dynamicPropertyInfo = ReflectionManager.CreateDynamicProperty(property);
        }

        public Boolean Readable
        {
            get { return propertyInfo.CanRead; }
        }

        public Boolean Writeable
        {
            get { return propertyInfo.CanWrite; }
        }

        public Type DataType
        {
            get { return propertyInfo.PropertyType; }
        }

        public Object Read(Object obj)
        {
            return dynamicPropertyInfo.GetValue(obj, null);
        }

        public void Write(Object obj, Object val)
        {
            try
            {
                dynamicPropertyInfo.SetValue(obj, val, null);
            }
            catch
            {
                throw;
            }
        }
    }
}
