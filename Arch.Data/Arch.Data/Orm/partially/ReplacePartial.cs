using System;
using System.Collections.Generic;
using System.Linq.Expressions;

namespace Arch.Data.Orm.partially
{
    public class ReplacePartial<T> : IReplacePartial<T>
    {
        private Queue<String> replaceFields = new Queue<String>();

        public IEnumerable<String> ReplaceFields
        {
            get { return replaceFields; }
        }

        /// <summary>
        /// Set replace field by lambda expression
        /// </summary>
        /// <typeparam name="TField"></typeparam>
        /// <param name="expression"></param>
        /// <returns></returns>
        public IReplacePartial<T> Replace<TField>(Expression<Func<T, TField>> expression)
        {
            if (expression != null)
            {
                String fieldName = ExpressionHelper<T>.GetFieldName(expression);
                if (!String.IsNullOrEmpty(fieldName)) replaceFields.Enqueue(fieldName.ToUpper());
            }

            return this;
        }

        /// <summary>
        /// Set replace field by field name
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        public IReplacePartial<T> Replace(String fieldName)
        {
            if (!String.IsNullOrEmpty(fieldName)) replaceFields.Enqueue(fieldName);
            return this;
        }

        /// <summary>
        /// Set replace fields by lambda expression
        /// </summary>
        /// <typeparam name="TField"></typeparam>
        /// <param name="expressions"></param>
        /// <returns></returns>
        public IReplacePartial<T> Replace<TField>(IEnumerable<Expression<Func<T, TField>>> expressions)
        {
            if (expressions != null)
            {
                foreach (var item in expressions)
                {
                    Replace(item);
                }
            }

            return this;
        }

        /// <summary>
        /// Set replace fields by fields name
        /// </summary>
        /// <param name="fieldsName"></param>
        /// <returns></returns>
        public IReplacePartial<T> Replace(IEnumerable<String> fieldsName)
        {
            if (fieldsName != null)
            {
                foreach (var item in fieldsName)
                {
                    Replace(item);
                }
            }

            return this;
        }

        public Boolean Validate()
        {
            if (replaceFields.Count == 0) throw new DalException("Replace fields can't be null.");
            return true;
        }

    }
}
