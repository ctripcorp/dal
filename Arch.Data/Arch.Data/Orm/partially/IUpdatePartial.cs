using System;
using System.Collections.Generic;
using System.Linq.Expressions;

namespace Arch.Data.Orm.partially
{
    public interface IUpdatePartial<TTable>
    {
        /// <summary>
        /// 使用时请注意，IUpdatePartial在传入BaseDao执行后，SetColumns将是空集合
        /// </summary>
        IEnumerable<String> SetColumns { get; }

        /// <summary>
        /// 使用时，请注意，IUpdatePartial在传入BaseDao执行后，WhereColumns将是空集合
        /// </summary>
        IEnumerable<String> WhereColumns { get; }

        /// <summary>
        /// 使用Lambda方式指定需要更新的字段，如 p => p.ID
        /// </summary>
        /// <typeparam name="TValue"></typeparam>
        /// <param name="setValue"></param>
        /// <returns></returns>
        IUpdatePartial<TTable> Set<TValue>(Expression<Func<TTable, TValue>> setValue);

        /// <summary>
        /// 使用字段名指定需要更新的字段，性能稍微好些，如 “ID”
        /// </summary>
        /// <param name="fieldName"></param>
        /// <returns></returns>
        IUpdatePartial<TTable> Set(String fieldName);

        /// <summary>
        /// 使用Lambda方式指定需要更新的字段，如 p => p.ID
        /// </summary>
        /// <typeparam name="TValue"></typeparam>
        /// <param name="setValues"></param>
        /// <returns></returns>
        IUpdatePartial<TTable> Sets<TValue>(IEnumerable<Expression<Func<TTable, TValue>>> setValues);

        /// <summary>
        /// 使用字段名指定需要更新的字段，性能稍微好些，如 “ID”
        /// </summary>
        /// <param name="fieldNames"></param>
        /// <returns></returns>
        IUpdatePartial<TTable> Sets(IEnumerable<String> fieldNames);

        /// <summary>
        /// Update时的条件，使用字段名指定需要更新的字段，性能稍微好些，
        /// 建议只用主键或者乐观锁字段，且目前只支持等于操作
        /// </summary>
        /// <typeparam name="TValue"></typeparam>
        /// <param name="setValue"></param>
        /// <returns></returns>
        IUpdatePartial<TTable> Where<TValue>(Expression<Func<TTable, TValue>> setValue);

        /// <summary>
        /// Update时的条件，使用字段名指定需要更新的字段，性能稍微好些，
        /// 建议只用主键或者乐观锁字段，且目前只支持等于操作
        /// </summary>
        /// <param name="conditionField"></param>
        /// <returns></returns>
        IUpdatePartial<TTable> Where(String conditionField);

        /// <summary>
        /// Update时的条件，使用字段名指定需要更新的字段，性能稍微好些，
        /// 建议只用主键或者乐观锁字段，且目前只支持等于操作
        /// </summary>
        /// <param name="conditionFields"></param>
        /// <returns></returns>
        IUpdatePartial<TTable> Where(IEnumerable<String> conditionFields);

        /// <summary>
        /// Update时的条件，使用字段名指定需要更新的字段，性能稍微好些，
        /// 建议只用主键或者乐观锁字段，且目前只支持等于操作
        /// </summary>
        /// <typeparam name="TValue"></typeparam>
        /// <param name="conditionFields"></param>
        /// <returns></returns>
        IUpdatePartial<TTable> Where<TValue>(IEnumerable<Expression<Func<TTable, TValue>>> conditionFields);

        /// <summary>
        /// 检查当前UpdatePartial是否合法
        /// </summary>
        /// <returns></returns>
        Boolean Validate();

    }
}
