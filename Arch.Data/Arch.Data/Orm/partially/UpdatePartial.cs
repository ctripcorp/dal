using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;

namespace Arch.Data.Orm.partially
{
    /// <summary>
    /// 部分字段更新
    /// </summary>
    /// <typeparam name="TTable"></typeparam>
    class UpdatePartial<TTable> : IUpdatePartial<TTable>
    {
        public UpdatePartial()
        {
            WhereConditions = new Queue<String>();
            SetFields = new Queue<String>();
        }

        public Queue<String> SetFields { get; private set; }

        public Queue<String> WhereConditions { get; private set; }

        /// <summary>
        /// 使用Lambda方式指定需要更新的字段，如 p => p.ID
        /// </summary>
        /// <typeparam name="TValue"></typeparam>
        /// <param name="setValue"></param>
        /// <returns></returns>
        public IUpdatePartial<TTable> Set<TValue>(Expression<Func<TTable, TValue>> setValue)
        {
            String fieldName = ExpressionHelper<TTable>.GetFieldName(setValue);
            return Set(fieldName);
        }

        /// <summary>
        /// 使用字段名指定需要更新的字段，性能稍微好些，如 “ID”
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        public IUpdatePartial<TTable> Set(String fieldName)
        {
            if (!String.IsNullOrEmpty(fieldName)) SetFields.Enqueue(fieldName.ToLower());
            return this;
        }

        /// <summary>
        /// Update时的条件，使用字段名指定需要更新的字段，性能稍微好些，
        /// 建议只用主键或者乐观锁字段，且目前只支持等于操作
        /// </summary>
        /// <typeparam name="TValue"></typeparam>
        /// <param name="setValue"></param>
        /// <returns></returns>
        public IUpdatePartial<TTable> Where<TValue>(Expression<Func<TTable, TValue>> setValue)
        {
            String fieldName = ExpressionHelper<TTable>.GetFieldName(setValue);
            return Where(fieldName);
        }

        /// <summary>
        /// Update时的条件，使用字段名指定需要更新的字段，性能稍微好些，
        /// 建议只用主键或者乐观锁字段，且目前只支持等于操作
        /// </summary>
        /// <param name="conditionField"></param>
        /// <returns></returns>
        public IUpdatePartial<TTable> Where(String conditionField)
        {
            if (!String.IsNullOrEmpty(conditionField)) WhereConditions.Enqueue(conditionField.ToLower());
            return this;
        }

        public Boolean Validate()
        {
            if (SetFields.Count <= 0 || WhereConditions.Count <= 0)
                throw new DalException("At least one field and one conditon is required.");

            return true;
        }


        public IEnumerable<String> SetColumns
        {
            get { return SetFields; }
        }

        public IEnumerable<String> WhereColumns
        {
            get { return WhereConditions.AsEnumerable(); }
        }

        public IUpdatePartial<TTable> Sets<TValue>(IEnumerable<Expression<Func<TTable, TValue>>> setValues)
        {
            foreach (var element in setValues)
            {
                Set(element);
            }
            return this;
        }

        public IUpdatePartial<TTable> Sets(IEnumerable<String> fieldNames)
        {
            foreach (var element in fieldNames)
            {
                Set(element);
            }
            return this;
        }


        public IUpdatePartial<TTable> Where(IEnumerable<String> conditionFields)
        {
            foreach (var element in conditionFields)
            {
                Where(element);
            }
            return this;
        }

        public IUpdatePartial<TTable> Where<TValue>(IEnumerable<Expression<Func<TTable, TValue>>> conditionFields)
        {
            foreach (var element in conditionFields)
            {
                Where(element);
            }
            return this;
        }
    }
}
