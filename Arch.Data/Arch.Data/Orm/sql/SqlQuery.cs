using Arch.Data.DbEngine;
using Arch.Data.Orm.Dialect;
using Arch.Data.Orm.FastInvoker;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Reflection;
using System.Text;

namespace Arch.Data.Orm.sql
{
    public class SqlQuery<T> : SqlQuery, IQuery<T>
    {
        internal SqlQuery(SqlTable queryTable) : base(queryTable) { }

        public IQuery<T> IsNull(String fieldName)
        {
            return (IQuery<T>)base.IsNull(fieldName);
        }

        public IQuery<T> IsNotNull(String fieldName)
        {
            return (IQuery<T>)base.IsNotNull(fieldName);
        }

        new public IQuery<T> Not()
        {
            return (IQuery<T>)base.Not();
        }

        new public IQuery<T> RightBracket()
        {
            return (IQuery<T>)base.RightBracket();
        }

        new public IQuery<T> LeftBracket()
        {
            return (IQuery<T>)base.LeftBracket();
        }

        /// <summary>
        /// 与操作
        /// </summary>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> And()
        {
            return (IQuery<T>)base.And();
        }

        /// <summary>
        /// 或操作
        /// </summary>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> Or()
        {
            return (IQuery<T>)base.Or();
        }

        new public IQuery<T> EqualNullable(String fieldName, Object val)
        {
            return (IQuery<T>)base.EqualNullable(fieldName, val);
        }

        new public IQuery<T> NotEqualNullable(String fieldName, Object val)
        {
            return (IQuery<T>)base.NotEqualNullable(fieldName, val);
        }

        new public IQuery<T> Equal(String fieldName, Object val)
        {
            return (IQuery<T>)base.Equal(fieldName, val);
        }

        new public IQuery<T> NotEqual(String fieldName, Object val)
        {
            return (IQuery<T>)base.NotEqual(fieldName, val);
        }

        new public IQuery<T> GreaterThanNullable(String fieldName, Object val)
        {
            return (IQuery<T>)base.GreaterThanNullable(fieldName, val);
        }

        new public IQuery<T> GreaterThan(String fieldName, Object val)
        {
            return (IQuery<T>)base.GreaterThan(fieldName, val);
        }

        new public IQuery<T> GreaterThanEqualsNullable(String fieldName, Object val)
        {
            return (IQuery<T>)base.GreaterThanEqualsNullable(fieldName, val);
        }

        new public IQuery<T> GreaterThanEquals(String fieldName, Object val)
        {
            return (IQuery<T>)base.GreaterThanEquals(fieldName, val);
        }

        new public IQuery<T> LessThanNullable(String fieldName, Object val)
        {
            return (IQuery<T>)base.LessThanNullable(fieldName, val);
        }

        new public IQuery<T> LessThan(String fieldName, Object val)
        {
            return (IQuery<T>)base.LessThan(fieldName, val);
        }

        new public IQuery<T> LessThanEqualsNullable(String fieldName, Object val)
        {
            return (IQuery<T>)base.LessThanEqualsNullable(fieldName, val);
        }

        new public IQuery<T> LessThanEquals(String fieldName, Object val)
        {
            return (IQuery<T>)base.LessThanEquals(fieldName, val);
        }

        new public IQuery<T> BetweenNullable(String fieldName, Object val, Object val2)
        {
            return (IQuery<T>)base.BetweenNullable(fieldName, val, val2);
        }

        new public IQuery<T> InNullable(String fieldName, IList paramValues)
        {
            return (IQuery<T>)base.InNullable(fieldName, paramValues);
        }

        new public IQuery<T> LikeNullable(String fieldName, String val)
        {
            return (IQuery<T>)base.LikeNullable(fieldName, val);
        }

        /// <summary>
        /// 包含操作
        /// </summary>
        /// <param name="column">列名</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> Constrain(String column)
        {
            return (IQuery<T>)base.Constrain(column);
        }

        /// <summary>
        /// 包含操作
        /// </summary>
        /// <param name="query">查询对象</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> Constrain(IQuery query)
        {
            return (IQuery<T>)base.Constrain(query);
        }

        /// <summary>
        /// 等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> Equal(Object val)
        {
            return (IQuery<T>)base.Equal(val);
        }

        /// <summary>
        /// 不等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> NotEqual(Object val)
        {
            return (IQuery<T>)base.NotEqual(val);
        }

        /// <summary>
        /// 大于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> Greater(Object val)
        {
            return (IQuery<T>)base.Greater(val);
        }

        /// <summary>
        /// 大于等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> GreaterEqual(Object val)
        {
            return (IQuery<T>)base.GreaterEqual(val);
        }

        /// <summary>
        /// 小于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> Less(Object val)
        {
            return (IQuery<T>)base.Less(val);
        }

        /// <summary>
        /// 小于等于操作
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> LessEqual(Object val)
        {
            return (IQuery<T>)base.LessEqual(val);
        }

        /// <summary>
        ///  like操作需要在dao方法内加通配符(%)
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> Like(String val)
        {
            return (IQuery<T>)base.Like(val);
        }

        new public IQuery<T> Like(String fieldName, Object val)
        {
            return (IQuery<T>)base.Like(fieldName, val);
        }

        /// <summary>
        /// 在范围内操作
        /// </summary>
        /// <param name="values">集合接口</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> In(IList values)
        {
            return (IQuery<T>)base.In(values);
        }

        new public IQuery<T> In(String fieldName, IList values)
        {
            return (IQuery<T>)base.In(fieldName, values);
        }

        /// <summary>
        /// 不在范围内操作
        /// </summary>
        /// <param name="values">集合接口</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> NotIn(IList values)
        {
            return (IQuery<T>)base.NotIn(values);
        }

        #region Implementation of IQuery<T>

        public IQuery<T> Constrain(Expression<Func<T, Object>> propertyExpression)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return Constrain(info.Name);
        }

        public IQuery<T> Order(Expression<Func<T, Object>> propertyExpression, Boolean asc)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return Order(info.Name, asc);
        }

        /// <summary>
        /// 排序
        /// </summary>
        /// <param name="column">列名</param>
        /// <param name="ascending">是否升序</param>
        /// <returns>查询对象接口</returns>
        new public IQuery<T> Order(String column, Boolean ascending)
        {
            return (IQuery<T>)base.Order(column, ascending);
        }

        #endregion

        new public IQuery<T> Between(Object value1, Object value2)
        {
            return (IQuery<T>)base.Between(value1, value2);
        }

        public IQuery<T> Between(String fieldName, Object start, Object end)
        {
            return (IQuery<T>)base.Between(fieldName, start, end);
        }

        public new IQuery<T> Limit(Int32 from, Int32 to = 0)
        {
            return (IQuery<T>)base.Limit(from, to);
        }

        public IQuery<T> Paging(Int32 pageNumber, Int32 pageSize, Expression<Func<T, Object>> propertyExpression, Boolean isAscending)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return Paging(pageNumber, pageSize, info.Name, isAscending);
        }

        new public IQuery<T> Paging(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending)
        {
            return (IQuery<T>)base.Paging(pageNumber, pageSize, orderColumnName, isAscending);
        }


        public IQuery<T> GreaterThan(Expression<Func<T, Object>> propertyExpression, Object val)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return GreaterThan(info.Name, val);
        }

        public IQuery<T> GreaterThanEquals(Expression<Func<T, Object>> propertyExpression, Object val)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return GreaterThanEquals(info.Name, val);
        }

        public IQuery<T> LessThan(Expression<Func<T, Object>> propertyExpression, Object val)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return LessThan(info.Name, val);
        }

        public IQuery<T> LessThanEquals(Expression<Func<T, Object>> propertyExpression, Object val)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return LessThanEquals(info.Name, val);
        }


        public IQuery<T> EqualNullable(Expression<Func<T, Object>> propertyExpression, Object val)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return EqualNullable(info.Name, val);
        }

        public IQuery<T> NotEqualNullable(Expression<Func<T, Object>> propertyExpression, Object val)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return NotEqualNullable(info.Name, val);
        }

        public IQuery<T> Equal(Expression<Func<T, Object>> propertyExpression, Object val)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return Equal(info.Name, val);
        }

        public IQuery<T> NotEqual(Expression<Func<T, Object>> propertyExpression, Object val)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return NotEqual(info.Name, val);
        }

        public IQuery<T> BetweenNullable(Expression<Func<T, Object>> propertyExpression, Object val, Object val2)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return BetweenNullable(info.Name, val, val2);
        }

        public IQuery<T> InNullable(Expression<Func<T, Object>> propertyExpression, IList paramValues)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return InNullable(info.Name, paramValues);
        }

        public IQuery<T> LikeNullable(Expression<Func<T, Object>> propertyExpression, String val)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return LikeNullable(info.Name, val);
        }
    }

    public class SqlQuery : IQuery
    {
        private List<SqlConstraint> AllConstraints;
        private List<SqlConstraint> Constraints;
        private List<String> Operators;
        private List<SqlOrder> Orders;
        private String _pending;
        private Int32 length;
        private SqlTable table;
        private Int32 limitFrom;
        private Int32 limitTo;
        private Int32 number;
        private Int32 size;
        private String name;
        private Boolean ascending;

        public SqlQuery()
        {
            AllConstraints = new List<SqlConstraint>();
            Constraints = new List<SqlConstraint>(3);
            Operators = new List<String>(2);
            Orders = new List<SqlOrder>(1);
        }

        /// <summary>
        /// sql查询
        /// </summary>
        internal SqlQuery(SqlTable queryTable)
            : this()
        {
            table = queryTable;
        }

        public IQuery Constrain<T>(Expression<Func<T, Object>> propertyExpression)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return Constrain(info.Name);
        }

        /// <summary>
        /// 排序
        /// </summary>
        /// <typeparam name="T">dao的实体对象</typeparam>
        /// <param name="propertyExpression">属性的表达式</param>
        /// <param name="asc">是否升序 </param>
        /// <returns>查询对象接口</returns>
        public IQuery Order<T>(Expression<Func<T, Object>> propertyExpression, Boolean asc)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return Order(info.Name, asc);
        }

        public IConstraint Get(Int32 index)
        {
            if (index > -1 && index < Constraints.Count) return Constraints[index];
            return null;
        }

        private void ErrorIfPending()
        {
            if (_pending != null)
                throw new InvalidOperationException("Method Constrain duplicated.");
        }

        private void ErrorIfNotPending()
        {
            if (_pending == null)
                throw new InvalidOperationException("Method Constrain not found.");
        }

        private void ErrorIfNull(Object val)
        {
            if (val == null)
                throw new ArgumentNullException("Value can't be null.");
        }

        /// <summary>
        /// 包含
        /// </summary>
        /// <param name="query">查询对象</param>
        /// <returns>查询对象接口</returns>
        public IQuery Constrain(IQuery query)
        {
            ErrorIfPending();
            Constraints.Add(new SqlConstraint(query));
            return this;
        }

        public IQuery Constrain(String column)
        {
            ErrorIfPending();
            _pending = column;

            if (table != null)
            {
                var col = table.ByName(column);
                if (col != null) length = col.Length;
            }
            return this;
        }

        #region Add Operators

        public IQuery IsNull(String fieldName)
        {
            AddConstraintOneStep(new NullConstraint(fieldName, true));
            return this;
        }

        public IQuery IsNotNull(String fieldName)
        {
            AddConstraintOneStep(new NullConstraint(fieldName, false));
            return this;
        }

        public IQuery Not()
        {
            ErrorIfPending();
            Operators.Add("NOT");
            AllConstraints.Add(new NotConstraint());
            return this;
        }

        public IQuery RightBracket()
        {
            ErrorIfPending();
            Operators.Add(")");
            AllConstraints.Add(BracketConstraint.RightBracket());
            return this;
        }

        public IQuery LeftBracket()
        {
            ErrorIfPending();
            Operators.Add("(");
            AllConstraints.Add(BracketConstraint.LeftBracket());
            return this;
        }

        /// <summary>
        /// 与
        /// </summary>
        /// <returns></returns>
        public virtual IQuery And()
        {
            ErrorIfPending();
            Operators.Add("AND");
            AllConstraints.Add(OperatorConstraint.And());
            return this;
        }

        /// <summary>
        /// 或
        /// </summary>
        /// <returns></returns>
        public IQuery Or()
        {
            ErrorIfPending();
            Operators.Add("OR");
            AllConstraints.Add(OperatorConstraint.Or());
            return this;
        }

        public IQuery EqualNullable(String fieldName, Object val)
        {
            if (CheckNullValue(val)) return this;
            AddConstraintOneStep(fieldName, "=", val);
            return this;
        }

        public IQuery NotEqualNullable(String fieldName, Object val)
        {
            if (CheckNullValue(val)) return this;
            AddConstraintOneStep(fieldName, "!=", val);
            return this;
        }

        public IQuery Equal(String fieldName, Object val)
        {
            if (val == null || val == DBNull.Value)
                throw new DalException("Equal cannot use null value, please use IsNull instead.");
            AddConstraintOneStep(fieldName, "=", val);
            return this;
        }

        public IQuery NotEqual(String fieldName, Object val)
        {
            if (val == null || val == DBNull.Value)
                throw new DalException("Equal cannot use null value, please use IsNull instead.");
            AddConstraintOneStep(fieldName, "!=", val);
            return this;
        }

        public IQuery GreaterThanNullable(String fieldName, Object val)
        {
            if (CheckNullValue(val)) return this;
            AddConstraintOneStep(fieldName, ">", val);
            return this;
        }

        public IQuery GreaterThan(String fieldName, Object val)
        {
            ErrorIfNull(val);
            AddConstraintOneStep(fieldName, ">", val);
            return this;
        }

        public IQuery GreaterThanEqualsNullable(String fieldName, Object val)
        {
            if (CheckNullValue(val)) return this;
            AddConstraintOneStep(fieldName, ">=", val);
            return this;
        }

        public IQuery GreaterThanEquals(String fieldName, Object val)
        {
            ErrorIfNull(val);
            AddConstraintOneStep(fieldName, ">=", val);
            return this;
        }

        public IQuery LessThanNullable(String fieldName, Object val)
        {
            if (CheckNullValue(val)) return this;
            AddConstraintOneStep(fieldName, "<", val);
            return this;
        }

        public IQuery LessThan(String fieldName, Object val)
        {
            ErrorIfNull(val);
            AddConstraintOneStep(fieldName, "<", val);
            return this;
        }

        public IQuery LessThanEqualsNullable(String fieldName, Object val)
        {
            if (CheckNullValue(val)) return this;
            AddConstraintOneStep(fieldName, "<=", val);
            return this;
        }

        public IQuery LessThanEquals(String fieldName, Object val)
        {
            ErrorIfNull(val);
            AddConstraintOneStep(fieldName, "<=", val);
            return this;
        }

        public IQuery BetweenNullable(String fieldName, Object val, Object val2)
        {
            if (CheckNullValue(val) || CheckNullValue(val2)) return this;
            ErrorIfPending();
            var constraint = new SqlConstraint(fieldName, "BETWEEN", val, val2);
            Constraints.Add(constraint);
            AllConstraints.Add(constraint);
            return this;
        }

        public IQuery InNullable(String fieldName, IList paramValues)
        {
            if (paramValues == null || paramValues.Count == 0)
            {
                AllConstraints.Add(new NullValueConstraint());
                return this;
            }

            for (Int32 i = paramValues.Count - 1; i > -1; i--)
            {
                if (paramValues[i] == null)
                    paramValues.RemoveAt(i);
            }
            if (paramValues.Count == 0)
            {
                AllConstraints.Add(new NullValueConstraint());
                return this;
            }

            SqlConstraint c = new InConstraint(fieldName, "IN", paramValues);
            AddConstraintOneStep(c);
            return this;
        }

        public IQuery LikeNullable(String fieldName, String val)
        {
            if (String.IsNullOrEmpty(val))
            {
                AllConstraints.Add(new NullValueConstraint());
                return this;
            }

            AddConstraintOneStep(fieldName, " LIKE ", val);
            return this;
        }

        private Boolean CheckNullValue(Object val)
        {
            if (val == null || val == DBNull.Value)
            {
                AllConstraints.Add(new NullValueConstraint());
                return true;
            }
            return false;
        }

        #endregion

        public IQuery In(IList values)
        {
            SqlConstraint c = new InConstraint(_pending, "IN", values);
            AddConstraint(c);
            return this;
        }

        public IQuery In(String fieldName, IList values)
        {
            SqlConstraint c = new InConstraint(fieldName, "IN", values);
            AddConstraintOneStep(c);
            return this;
        }

        public IQuery NotIn(IList values)
        {
            SqlConstraint c = new InConstraint(_pending, "NOT IN", values);
            AddConstraint(c);
            return this;
        }

        protected void AddConstraint(String op, Object val)
        {
            SqlConstraint c = new SqlConstraint(_pending, op, val);
            AddConstraint(c);
        }

        protected void AddConstraint(String op, Object val, Object val2)
        {
            SqlConstraint c = new SqlConstraint(_pending, op, val, val2);
            AddConstraint(c);
        }

        internal void AddConstraint(SqlConstraint c)
        {
            ErrorIfNotPending();
            Constraints.Add(c);
            AllConstraints.Add(c);
            _pending = null;
        }

        internal void AddConstraintOneStep(SqlConstraint c)
        {
            ErrorIfPending();
            Constraints.Add(c);
            AllConstraints.Add(c);
        }

        protected void AddConstraintOneStep(String column, String op, Object val)
        {
            ErrorIfPending();
            SqlConstraint constraint = new SqlConstraint(column, op, val);
            Constraints.Add(constraint);
            AllConstraints.Add(constraint);
        }

        /// <summary>
        /// 等于
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        public IQuery Equal(Object val)
        {
            if (val == null || val == DBNull.Value)
            {
                AddConstraint(new NullConstraint(_pending, true));
            }
            else
            {
                AddConstraint("=", val);
            }
            return this;
        }

        /// <summary>
        /// 不等于
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        public IQuery NotEqual(Object val)
        {
            if (val == null || val == DBNull.Value)
            {
                AddConstraint(new NullConstraint(_pending, false));
            }
            else
            {
                AddConstraint("!=", val);
            }
            return this;
        }

        /// <summary>
        /// 大于
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        public IQuery Greater(Object val)
        {
            ErrorIfNull(val);
            AddConstraint(">", val);
            return this;
        }

        /// <summary>
        /// 大于等于
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        public IQuery GreaterEqual(Object val)
        {
            ErrorIfNull(val);
            AddConstraint(">=", val);
            return this;
        }

        /// <summary>
        /// 小于
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        public IQuery Less(Object val)
        {
            ErrorIfNull(val);
            AddConstraint("<", val);
            return this;
        }

        /// <summary>
        /// 小于等于
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        public IQuery LessEqual(Object val)
        {
            ErrorIfNull(val);
            AddConstraint("<=", val);
            return this;
        }

        /// <summary>
        /// like操作需要在dao方法内加通配符(%)
        /// </summary>
        /// <param name="val">值</param>
        /// <returns>查询对象接口</returns>
        public IQuery Like(String val)
        {
            ErrorIfNull(val);
            AddConstraint(" LIKE ", val);
            return this;
        }

        public IQuery Like(String fieldName, Object val)
        {
            ErrorIfNull(val);
            AddConstraintOneStep(fieldName, " LIKE ", val);
            return this;
        }

        /// <summary>
        /// 排序
        /// </summary>
        /// <param name="column">列名</param>
        /// <param name="asc">是否升序</param>
        /// <returns>查询对象接口</returns>
        public IQuery Order(String column, Boolean asc)
        {
            SqlOrder o = new SqlOrder(column, asc);
            Orders.Add(o);
            return this;
        }

        public IQuery Between(Object value1, Object value2)
        {
            ErrorIfNull(value1);
            ErrorIfNull(value2);
            AddConstraint("BETWEEN", value1, value2);
            return this;
        }

        public IQuery Between(String fieldName, Object start, Object end)
        {
            ErrorIfNull(start);
            ErrorIfNull(end);
            AddConstraintOneStep(new BetweenConstraint(fieldName, start, end));
            return this;
        }

        protected Boolean IsComplete
        {
            get { return _pending == null; }
        }

        public virtual IQuery Limit(Int32 from, Int32 to = 0)
        {
            limitFrom = from;
            limitTo = to;
            return this;
        }

        public virtual String PrepareLimitPrefix(IDbDialect dbDialect)
        {
            return dbDialect.LimitPrefix(limitFrom, limitTo);
        }

        public virtual String PrepareLimitSuffix(IDbDialect dbDialect)
        {
            return dbDialect.LimitSuffix(limitFrom, limitTo);
        }

        public IQuery Paging<T>(Int32 pageNumber, Int32 pageSize, Expression<Func<T, Object>> propertyExpression, Boolean isAscending)
        {
            PropertyInfo info = ReflectorHelper.GetProperty(propertyExpression);
            return Paging(pageNumber, pageSize, info.Name, isAscending);
        }

        public IQuery Paging(Int32 pageNumber, Int32 pageSize, String orderColumnName, Boolean isAscending)
        {
            number = pageNumber;
            size = pageSize;
            name = orderColumnName;
            ascending = isAscending;
            return this;
        }

        public virtual String PagingPrefix(IDbDialect dbDialect)
        {
            return dbDialect.PagingPrefix(number, size, name, ascending);
        }

        public virtual String PagingSuffix(IDbDialect dbDialect, List<SqlColumn> sqlColumns)
        {
            return dbDialect.PagingSuffix(number, size, name, ascending, sqlColumns);
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="table"></param>
        /// <param name="openQuote"></param>
        /// <param name="closeQuote"></param>
        /// <param name="offset"></param>
        /// <returns></returns>
        internal virtual String GetSql(SqlTable table, Char openQuote, Char closeQuote, ref Int32 offset)
        {
            String sql = GetQuerySql(table, openQuote, closeQuote, ref offset);
            if (Constraints.Count > 0) sql = String.Concat("WHERE", sql);
            return sql;
        }

        internal virtual String GetQuerySql(SqlTable table, Char openQuote, Char closeQuote, ref Int32 offset)
        {
            return ToQuerySql(table, openQuote, closeQuote, ref offset);
        }

        internal String ToQuerySql(SqlTable table, Char openQuote, Char closeQuote, ref Int32 offset)
        {
            if (!IsComplete) throw new InvalidOperationException("Invalid query.");

            var filtered = new List<SqlConstraint>();
            foreach (var item in AllConstraints)
            {
                if (item.IsClause() && item.IsNull())
                {
                    MeltDownNullValue(filtered);
                    continue;
                }
                if (item.IsBracket() && !((BracketConstraint)item).IsLeft)
                {
                    if (MeltDownRightBracket(filtered)) continue;
                }
                if (item.IsOperator() && !item.IsClause())
                {
                    if (MeltDownAndOrOperator(filtered)) continue;
                }

                filtered.Add(item);
            }

            StringBuilder sb = new StringBuilder();
            Int32 sz = filtered.Count;
            for (Int32 i = 0; i < sz; i++)
            {
                var constraint = filtered[i];
                String sql = constraint.GetSql(table, openQuote, closeQuote, ref offset);
                sb.Append(" ").Append(sql);
            }

            sz = Orders.Count;

            if (sz > 0)
            {
                sb.Append(" ORDER BY ");

                for (Int32 i = 0; i < sz; i++)
                {
                    if (i > 0) sb.Append(",");
                    var order = Orders[i];
                    String sql = order.GetSql(table, openQuote, closeQuote);
                    sb.Append(sql);
                }
            }

            return sb.ToString();
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="parameters"></param>
        internal virtual void SetParameters(SqlTable table, StatementParameterCollection parameters)
        {
            foreach (var constraint in Constraints)
            {
                if (constraint.Operator == "IN" || constraint.Operator == "NOT IN")
                {
                    constraint.SetInParameters(table, parameters);
                }
                else
                {
                    constraint.SetParameters(table, parameters);
                }
            }
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="table"></param>
        /// <param name="openQuote"></param>
        /// <param name="closeQuote"></param>
        /// <returns></returns>
        internal virtual String GetSql(SqlTable table, Char openQuote, Char closeQuote)
        {
            Int32 offset = 1;
            return GetSql(table, openQuote, closeQuote, ref offset);
        }

        private void MeltDownNullValue(List<SqlConstraint> filtered)
        {
            if (filtered.Count == 0) return;

            while (filtered.Count > 0)
            {
                var item = filtered[filtered.Count - 1];
                if (item.IsOperator())
                {
                    filtered.RemoveAt(filtered.Count - 1);
                    continue;
                }
                break;
            }
        }

        private Boolean MeltDownAndOrOperator(List<SqlConstraint> filtered)
        {
            if (filtered.Count == 0) return true;
            var item = filtered[filtered.Count - 1];
            if (item.IsBracket() && ((BracketConstraint)item).IsLeft) return true;
            if (item.IsOperator()) return true;
            return false;
        }

        private Boolean MeltDownRightBracket(List<SqlConstraint> filtered)
        {
            Int32 bracketCount = 1;
            while (filtered.Count > 0)
            {
                SqlConstraint item = filtered[filtered.Count - 1];
                if (item.IsBracket() && ((BracketConstraint)item).IsLeft && bracketCount == 1)
                {
                    filtered.RemoveAt(filtered.Count - 1);
                    bracketCount--;
                    continue;
                }

                if (item.IsOperator())
                {
                    filtered.RemoveAt(filtered.Count - 1);
                    continue;
                }

                break;
            }

            return bracketCount == 0;
        }

    }
}
