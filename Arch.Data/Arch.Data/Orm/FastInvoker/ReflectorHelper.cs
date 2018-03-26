using System;
using System.Linq.Expressions;
using System.Reflection;

namespace Arch.Data.Orm.FastInvoker
{
    /// <summary>
    /// 反射帮助类
    /// </summary>
    public class ReflectorHelper
    {
        /// <summary>
        /// Gets the property represented by the lambda expression.
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="expression"></param>
        /// <returns></returns>
        public static PropertyInfo GetProperty<T>(Expression<Func<T, Object>> expression)
        {
            MemberExpression memberExpression = null;

            if (expression.Body.NodeType == ExpressionType.Convert)
            {
                memberExpression = ((UnaryExpression)expression.Body).Operand as MemberExpression;
            }
            else if (expression.Body.NodeType == ExpressionType.MemberAccess)
            {
                memberExpression = expression.Body as MemberExpression;
            }

            if (memberExpression == null) throw new ArgumentException("Property not found.");
            return memberExpression.Member as PropertyInfo;
        }

        /// <summary>
        /// Gets the field represented by the lambda expression.
        /// </summary>
        /// <typeparam name="T"></typeparam>
        /// <param name="expression"></param>
        /// <returns></returns>
        public static FieldInfo GetField<T>(Expression<Func<T, Object>> expression)
        {
            MemberExpression memberExpression = null;

            if (expression.Body.NodeType == ExpressionType.Convert)
            {
                memberExpression = ((UnaryExpression)expression.Body).Operand as MemberExpression;
            }
            else if (expression.Body.NodeType == ExpressionType.MemberAccess)
            {
                memberExpression = expression.Body as MemberExpression;
            }

            if (memberExpression == null) throw new ArgumentException("Field not found.");
            return memberExpression.Member as FieldInfo;
        }

    }
}
