using Arch.Data.Orm.FastInvoker;
using System;
using System.Reflection;

namespace Arch.Data.Orm.sql
{
    public class FieldBridge : IDataBridge
    {
        private FieldInfo fieldInfo;

        private IDynamicFieldInfo dynamicFieldInfo;

        public FieldBridge(FieldInfo fieldInfo)
        {
            this.fieldInfo = fieldInfo;
            dynamicFieldInfo = ReflectionManager.CreateDynamicFieldInfo(fieldInfo);
        }

        public Boolean Readable
        {
            get { return true; }
        }

        public Boolean Writeable
        {
            get { return true; }
        }

        public Type DataType
        {
            get { return fieldInfo.FieldType; }
        }

        public Object Read(Object obj)
        {
            return dynamicFieldInfo.GetValue(obj);
        }

        public void Write(Object obj, Object value)
        {
            dynamicFieldInfo.SetValue(obj, value);
        }
    }
}
