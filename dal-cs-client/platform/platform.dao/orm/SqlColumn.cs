using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Reflection;
using platform.dao.utils;

namespace platform.dao.orm
{
    public class SqlColumn
    {

        /// <summary>
        /// 字段名
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// 字段别名
        /// </summary>
        public string Alias { get; set; }

        /// <summary>
        /// 字段在sql语句中的位置
        /// </summary>
        public int Index { get; set; }

        /// <summary>
        /// 是否是主键
        /// </summary>
        public bool IsPrimaryKey { get; set; }

        /// <summary>
        /// 该数据库字段对应的PropertyInfo
        /// </summary>
        public PropertyInfo PropertyInfo { get; set; }

        /// <summary>
        /// 为obj设置某属性的值
        /// </summary>
        /// <param name="obj"></param>
        /// <param name="value"></param>
        public void SetValue(object obj, object value)
        {
            Type type = Nullable.GetUnderlyingType(PropertyInfo.PropertyType);
            if (type != null)
            {
                PropertyInfo.SetValue(obj, TypeConverter.ConvertToUnderlyingType(type, value), null);
            }
            else
            {
                PropertyInfo.SetValue(obj, value, null);
            }
        }

        /// <summary>
        /// 获取obj该属性的值
        /// </summary>
        /// <param name="obj"></param>
        /// <returns></returns>
        public object GetValue(object obj)
        {
            return PropertyInfo.GetValue(obj, null);
        }

    }
}
