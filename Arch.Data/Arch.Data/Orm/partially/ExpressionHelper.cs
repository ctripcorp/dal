using System;
using System.Collections.Concurrent;
using System.Linq.Expressions;

namespace Arch.Data.Orm.partially
{
    /// <summary>
    /// 
    /// </summary>
    /// <typeparam name="T">Indicates table entity</typeparam>
    class ExpressionHelper<T>
    {
        private static ConcurrentDictionary<Int32, ConcurrentDictionary<Int32, String>> Cache =
            new ConcurrentDictionary<Int32, ConcurrentDictionary<Int32, String>>();

        /// <summary>
        /// Get the field name in table according to the name property in ColumnAttibute
        /// </summary>
        /// <typeparam name="TField"></typeparam>
        /// <param name="setValue">lambda expression like t=>t.colname</param>
        /// <returns></returns>
        public static String GetFieldName<TField>(Expression<Func<T, TField>> setValue)
        {
            if (setValue == null || setValue.Parameters == null || setValue.Parameters.Count == 0)
                throw new DalException("Lambda expression is invalid.");

            var parameter = setValue.Parameters[0];
            if (parameter == null)
                throw new DalException("Lambda expression[{0}] is invalid.", setValue.ToString());

            var member = setValue.Body as MemberExpression;
            if (member == null)
                throw new DalException("Lambda expression[{0}] is invalid.", setValue.ToString());

            var info = parameter.Type.GetProperty(member.Member.Name);
            String fieldName = Cache.GetOrAdd(info.Module.GetHashCode(), moduleKey => new ConcurrentDictionary<Int32, String>())
                .GetOrAdd(info.MetadataToken, innnerKey =>
                {
                    if (info.IsDefined(typeof(ColumnAttribute), false))
                    {
                        var attr = (ColumnAttribute)info.GetCustomAttributes(typeof(ColumnAttribute), false)[0];
                        return attr.Name;
                    }
                    return null;
                });

            return fieldName;
        }
    }
}