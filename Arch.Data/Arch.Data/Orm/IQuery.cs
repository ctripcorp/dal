using System;
using System.Collections;
using System.Linq.Expressions;

namespace Arch.Data.Orm
{
    /// <summary>
    /// 
    /// </summary>
    /// <typeparam name="T"></typeparam>
    public interface IQuery<T> : IQuery
    {
        new IQuery<T> IsNull(String fieldName);

        new IQuery<T> IsNotNull(String fieldName);

        new IQuery<T> Not();

        new IQuery<T> RightBracket();

        new IQuery<T> LeftBracket();

        new IQuery<T> EqualNullable(String fieldName, Object val);

        IQuery<T> EqualNullable(Expression<Func<T, Object>> propertyExpression, Object val);

        new IQuery<T> NotEqualNullable(String fieldName, Object val);

        IQuery<T> NotEqualNullable(Expression<Func<T, Object>> propertyExpression, Object val);

        new IQuery<T> Equal(String fieldName, Object val);

        IQuery<T> Equal(Expression<Func<T, Object>> propertyExpression, Object val);

        new IQuery<T> NotEqual(String fieldName, Object val);

        IQuery<T> NotEqual(Expression<Func<T, Object>> propertyExpression, Object val);

        new IQuery<T> GreaterThanNullable(String fieldName, Object val);

        new IQuery<T> GreaterThanEqualsNullable(String fieldName, Object val);

        new IQuery<T> LessThanNullable(String fieldName, Object val);

        new IQuery<T> LessThanEqualsNullable(String fieldName, Object val);

        new IQuery<T> BetweenNullable(String fieldName, Object val, Object val2);

        IQuery<T> BetweenNullable(Expression<Func<T, Object>> propertyExpression, Object val, Object val2);

        new IQuery<T> InNullable(String fieldName, IList paramValues);

        IQuery<T> InNullable(Expression<Func<T, Object>> propertyExpression, IList paramValues);

        new IQuery<T> LikeNullable(String fieldName, String val);

        IQuery<T> LikeNullable(Expression<Func<T, Object>> propertyExpression, String val);

        new IQuery<T> GreaterThan(String fieldName, Object val);

        IQuery<T> GreaterThan(Expression<Func<T, Object>> propertyExpression, Object val);

        new IQuery<T> GreaterThanEquals(String fieldName, Object val);

        IQuery<T> GreaterThanEquals(Expression<Func<T, Object>> propertyExpression, Object val);

        new IQuery<T> LessThan(String fieldName, Object val);

        IQuery<T> LessThan(Expression<Func<T, Object>> propertyExpression, Object val);

        new IQuery<T> LessThanEquals(String fieldName, Object val);

        IQuery<T> LessThanEquals(Expression<Func<T, Object>> propertyExpression, Object val);

        /// <summary>
        /// 
        /// </summary>
        /// <returns></returns>
        new IQuery<T> And();

        /// <summary>
        /// 
        /// </summary>
        /// <param name="propertyExpression"></param>
        /// <returns></returns>
        IQuery<T> Constrain(Expression<Func<T, Object>> propertyExpression);

        /// <summary>
        /// 
        /// </summary>
        /// <param name="propertyExpression"></param>
        /// <param name="asc"></param>
        /// <returns></returns>
        IQuery<T> Order(Expression<Func<T, Object>> propertyExpression, Boolean asc);

        /// <summary>
        /// 排序
        /// </summary>
        /// <param name="column">列名</param>
        /// <param name="ascending">是否升序</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> Order(String column, Boolean ascending);

        /// <summary>
        /// 获得约束对象
        /// </summary>
        /// <param name="index">索引</param>
        /// <returns>约束接口</returns>
        new IConstraint Get(Int32 index);

        /// <summary>
        /// 或操作
        /// </summary>
        /// <returns>查询对象接口</returns>
        new IQuery<T> Or();

        /// <summary>
        /// 包含操作
        /// </summary>
        /// <param name="column">列名</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> Constrain(String column);

        /// <summary>
        /// 包含操作
        /// </summary>
        /// <param name="query">查询对象</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> Constrain(IQuery query);

        /// <summary>
        /// 等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> Equal(Object val);

        /// <summary>
        /// 不等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> NotEqual(Object val);

        /// <summary>
        /// 大于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> Greater(Object val);

        /// <summary>
        /// 大于等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> GreaterEqual(Object val);

        /// <summary>
        /// 小于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> Less(Object val);

        /// <summary>
        /// 小于等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> LessEqual(Object val);

        /// <summary>
        ///  like操作需要在dao方法内加通配符(%)
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> Like(String val);

        /// <summary>
        ///  like操作需要在dao方法内加通配符(%)
        /// </summary>
        /// <param name="fieldName">字段名</param>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> Like(String fieldName, Object val);

        /// <summary>
        /// 在范围内操作
        /// </summary>
        /// <param name="values">集合接口</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> In(IList values);

        /// <summary>
        /// 在范围内操作
        /// </summary>
        /// <param name="fieldName"></param>
        /// <param name="values">集合接口</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> In(String fieldName, IList values);

        /// <summary>
        /// 不在范围内操作
        /// </summary>
        /// <param name="values">集合接口</param>
        /// <returns>查询对象接口</returns>
        new IQuery<T> NotIn(IList values);

        /// <summary>
        /// Sql Server Top 或者 MySql Limit
        /// </summary>
        /// <param name="num1">如Top 10 或者 limit 10</param>
        /// <param name="num2">只针对MySql有效，如 limit 10, 20</param>
        /// <returns></returns>
        new IQuery<T> Limit(Int32 num1, Int32 num2 = 0);

        /// <summary>
        /// SQL的Between功能
        /// </summary>
        /// <param name="start">Between的起始点</param>
        /// <param name="end">Between的结束点</param>
        /// <returns></returns>
        new IQuery<T> Between(Object start, Object end);

        /// <summary>
        /// SQL的Between功能
        /// </summary>
        /// <param name="start">Between的起始点</param>
        /// <param name="end">Between的结束点</param>
        /// <returns></returns>
        new IQuery<T> Between(String fieldName, Object start, Object end);

        /// <summary>
        /// 分页功能
        /// </summary>
        /// <param name="pageNumber">当前第几页,从1开始</param>
        /// <param name="pageSize">每页显示多少数据</param>
        /// <param name="propertyExpression">排序的列，目前只支持一个列</param>
        /// <param name="isAscending">是否安装升序排序</param>
        /// <returns>当前IQuery对象</returns>
        IQuery<T> Paging(Int32 pageNumber, Int32 pageSize,
            Expression<Func<T, Object>> propertyExpression, Boolean isAscending);

        /// <summary>
        /// 分页功能
        /// </summary>
        /// <param name="pageNumber">当前第几页,从1开始</param>
        /// <param name="pageSize">每页显示多少数据</param>
        /// <param name="orderColumnName">排序的列，目前只支持一个列</param>
        /// <param name="isAscending">是否安装升序排序</param>
        /// <returns>当前IQuery对象</returns>
        new IQuery<T> Paging(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending);
    }

    /// <summary>
    /// 
    /// </summary>
    public interface IQuery
    {
        IQuery IsNull(String fieldName);

        IQuery IsNotNull(String fieldName);

        IQuery Not();

        IQuery RightBracket();

        IQuery LeftBracket();

        IQuery EqualNullable(String fieldName, Object val);

        IQuery NotEqualNullable(String fieldName, Object val);

        IQuery Equal(String fieldName, Object val);

        IQuery NotEqual(String fieldName, Object val);

        IQuery GreaterThanNullable(String fieldName, Object val);

        IQuery GreaterThanEqualsNullable(String fieldName, Object val);

        IQuery LessThanNullable(String fieldName, Object val);

        IQuery LessThanEqualsNullable(String fieldName, Object val);

        IQuery BetweenNullable(String fieldName, Object val, Object val2);

        IQuery InNullable(String fieldName, IList paramValues);

        IQuery LikeNullable(String fieldName, String val);

        IQuery GreaterThan(String fieldName, Object val);

        IQuery GreaterThanEquals(String fieldName, Object val);

        IQuery LessThan(String fieldName, Object val);

        IQuery LessThanEquals(String fieldName, Object val);

        /// <summary>
        /// 和操作
        /// </summary>
        /// <returns>查询对象接口</returns>
        IQuery And();

        /// <summary>
        /// 或操作
        /// </summary>
        /// <returns>查询对象接口</returns>
        IQuery Or();

        /// <summary>
        /// 包含操作
        /// </summary>
        /// <param name="column">列名</param>
        /// <returns>查询对象接口</returns>
        IQuery Constrain(String column);

        /// <summary>
        /// 包含操作
        /// </summary>
        /// <param name="query">查询对象</param>
        /// <returns>查询对象接口</returns>
        IQuery Constrain(IQuery query);

        /// <summary>
        /// 等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        IQuery Equal(Object val);

        /// <summary>
        /// 不等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        IQuery NotEqual(Object val);

        /// <summary>
        /// 大于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        IQuery Greater(Object val);

        /// <summary>
        /// 大于等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        IQuery GreaterEqual(Object val);

        /// <summary>
        /// 小于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        IQuery Less(Object val);

        /// <summary>
        /// 小于等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        IQuery LessEqual(Object val);

        /// <summary>
        ///  like操作需要在dao方法内加通配符(%)
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        IQuery Like(String val);

        /// <summary>
        ///  like操作需要在dao方法内加通配符(%)
        /// </summary>
        /// <param name="fieldName">字段名</param>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        IQuery Like(String fieldName, Object val);

        /// <summary>
        /// 在范围内操作
        /// </summary>
        /// <param name="values">集合接口</param>
        /// <returns>查询对象接口</returns>
        IQuery In(IList values);

        /// <summary>
        /// 在范围内操作
        /// </summary>
        /// <param name="fieldName"></param>
        /// <param name="values">集合接口</param>
        /// <returns>查询对象接口</returns>
        IQuery In(String fieldName, IList values);

        /// <summary>
        /// 不在范围内操作
        /// </summary>
        /// <param name="values">集合接口</param>
        /// <returns>查询对象接口</returns>
        IQuery NotIn(IList values);

        /// <summary>
        /// 排序
        /// </summary>
        /// <param name="column">列名</param>
        /// <param name="ascending">是否升序</param>
        /// <returns>查询对象接口</returns>
        IQuery Order(String column, Boolean ascending);

        /// <summary>
        /// 获得约束对象
        /// </summary>
        /// <param name="index">索引</param>
        /// <returns>约束接口</returns>
        IConstraint Get(Int32 index);

        /// <summary>
        /// Sql Server Top 或者 MySql Limit
        /// </summary>
        /// <param name="from">如Top 10 或者 limit 10</param>
        /// <param name="to">只针对MySql有效，如 limit 10, 20</param>
        /// <returns></returns>
        IQuery Limit(Int32 from, Int32 to = 0);

        /// <summary>
        /// SQL的Between功能
        /// </summary>
        /// <param name="start">Between的起始点</param>
        /// <param name="end">Between的结束点</param>
        /// <returns></returns>
        IQuery Between(Object start, Object end);

        /// <summary>
        /// SQL的Between功能
        /// </summary>
        /// <param name="fieldName"></param>
        /// <param name="start">Between的起始点</param>
        /// <param name="end">Between的结束点</param>
        /// <returns></returns>
        IQuery Between(String fieldName, Object start, Object end);

        /// <summary>
        /// 分页功能
        /// </summary>
        /// <param name="pageNumber">当前第几页</param>
        /// <param name="pageSize">每页显示多少数据</param>
        /// <param name="orderColumnName">排序的列，目前只支持一个列</param>
        /// <param name="isAscending">是否安装升序排序</param>
        /// <returns>当前IQuery对象</returns>
        IQuery Paging(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending);

    }

}
