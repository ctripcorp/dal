using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Data;

namespace platform.dao.utils
{
    public class TypeConverter
    {

        private static readonly Hashtable typemap;
        private static readonly DateTime utcStartTime;

        static TypeConverter()
        {
            typemap = new Hashtable(17, 1f);
            typemap.Add(typeof(bool), DbType.Boolean);
            typemap.Add(typeof(byte), DbType.Byte);
            typemap.Add(typeof(sbyte), DbType.SByte);
            typemap.Add(typeof(short), DbType.Int16);
            typemap.Add(typeof(int), DbType.Int32);
            typemap.Add(typeof(long), DbType.Int64);
            typemap.Add(typeof(uint), DbType.UInt32);
            typemap.Add(typeof(ulong), DbType.UInt64);
            typemap.Add(typeof(ushort), DbType.UInt16);
            typemap.Add(typeof(float), DbType.Single);
            typemap.Add(typeof(double), DbType.Double);
            typemap.Add(typeof(decimal), DbType.Decimal);
            typemap.Add(typeof(char), DbType.StringFixedLength);
            typemap.Add(typeof(string), DbType.String);
            typemap.Add(typeof(Guid), DbType.Guid);
            typemap.Add(typeof(DateTime), DbType.DateTime);
            typemap.Add(typeof(byte[]), DbType.Binary);

            utcStartTime = new DateTime(1970, 1, 1, 0, 0, 0, 0);
        }

        /// <summary>
        /// 从字段类型获取对应的数据库类型
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public static DbType ResolveType(Type type)
        {
            if (type != null)
            {
                Type t = type;
                if (t.IsGenericType && t.IsValueType)
                {
                    Type[] genericTypes = t.GetGenericArguments();
                    if (genericTypes.Length > 0)
                        t = genericTypes[0];
                }
                if (t != null && typemap.ContainsKey(t))
                    return (DbType)typemap[t];
            }
            return DbType.String;
        }

        /// <summary>
        /// 从数据库类型获取对应的字段类型
        /// </summary>
        /// <param name="type"></param>
        /// <returns></returns>
        public static Type ResolveDbType(DbType type)
        {
            if (type != null)
            {
                foreach (Type t in typemap.Keys)
                {
                    if (type.Equals(typemap[t]))
                        return t;
                }
            }
            return typeof(string);
        }

        /// <summary>
        /// 将原始值转换为相应的类型
        /// </summary>
        /// <param name="type"></param>
        /// <param name="originalValue"></param>
        /// <returns></returns>
        public static object ConvertToUnderlyingType(Type type, object originalValue)
        {
            object convertedValue = null;
            //if (type == typeof(DateTime))
            //{
            //    ulong milliseconds = (ulong)originalValue;
            //    convertedValue = utcStartTime.AddMilliseconds(milliseconds);
            //}
            //else
            //{
                convertedValue = System.Convert.ChangeType(originalValue, type);
            //}

            return convertedValue;
        }


    }
}
