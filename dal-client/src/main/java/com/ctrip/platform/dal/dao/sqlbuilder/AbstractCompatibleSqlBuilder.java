package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by lilj on 2018/8/30.
 * just for compatibility with versions before 1.15.0
 */
public abstract class AbstractCompatibleSqlBuilder extends AbstractSqlBuilder {

    public AbstractCompatibleSqlBuilder(BuilderContext context){
        super(context);
    }

    /**
     * 追加AND连接
     * @return this builder
     */
    public abstract AbstractSqlBuilder and();

    /**
     * 追加OR连接
     * @return this builder
     */
    public abstract AbstractSqlBuilder or();
    
    /**
     * 等于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder equal(String field, Object paramValue, int sqlType) throws SQLException ;

    /**
     * 等于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder equal(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException ;

    /**
     * 等于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     */
    public abstract AbstractSqlBuilder equalNullable(String field, Object paramValue, int sqlType);

    /**
     * 等于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     */
    public abstract AbstractSqlBuilder equalNullable(String field, Object paramValue, int sqlType, boolean sensitive);

    /**
     * 不等于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder notEqual(String field, Object paramValue, int sqlType) throws SQLException;

    /**
     * 不等于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder notEqual(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException;

    /**
     * 不等于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     */
    public abstract AbstractSqlBuilder notEqualNullable(String field, Object paramValue, int sqlType);

    /**
     * 不等于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     */
    public abstract AbstractSqlBuilder notEqualNullable(String field, Object paramValue, int sqlType, boolean sensitive);

    /**
     * 大于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder greaterThan(String field, Object paramValue, int sqlType) throws SQLException;

    /**
     * 大于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder greaterThan(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException;

    /**
     * 大于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     */
    public abstract AbstractSqlBuilder greaterThanNullable(String field, Object paramValue, int sqlType);

    /**
     * 大于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     */
    public abstract AbstractSqlBuilder greaterThanNullable(String field, Object paramValue, int sqlType, boolean sensitive);

    /**
     * 大于等于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder greaterThanEquals(String field, Object paramValue, int sqlType) throws SQLException;

    /**
     * 大于等于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder greaterThanEquals(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException;

    /**
     * 大于等于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     */
    public abstract AbstractSqlBuilder greaterThanEqualsNullable(String field, Object paramValue, int sqlType);

    /**
     * 大于等于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     */
    public abstract AbstractSqlBuilder greaterThanEqualsNullable(String field, Object paramValue, int sqlType, boolean sensitive);

    /**
     * 小于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder lessThan(String field, Object paramValue, int sqlType) throws SQLException;

    /**
     * 小于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder lessThan(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException;

    /**
     * 小于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     */
    public abstract AbstractSqlBuilder lessThanNullable(String field, Object paramValue, int sqlType);

    /**
     * 小于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     */
    public abstract AbstractSqlBuilder lessThanNullable(String field, Object paramValue, int sqlType, boolean sensitive);

    /**
     * 小于等于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder lessThanEquals(String field, Object paramValue, int sqlType) throws SQLException;

    /**
     * 小于等于操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder lessThanEquals(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException;

    /**
     * 小于等于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     */
    public abstract AbstractSqlBuilder lessThanEqualsNullable(String field, Object paramValue, int sqlType);

    /**
     * 小于等于操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     */
    public abstract AbstractSqlBuilder lessThanEqualsNullable(String field, Object paramValue, int sqlType, boolean sensitive);

    /**
     * Between操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue1 字段值1
     * @param paramValue2 字段值2
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder between(String field, Object paramValue1, Object paramValue2, int sqlType) throws SQLException;

    /**
     * Between操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue1 字段值1
     * @param paramValue2 字段值2
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder between(String field, Object paramValue1, Object paramValue2, int sqlType, boolean sensitive) throws SQLException;

    /**
     * Between操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue1 字段值1
     * @param paramValue2 字段值2
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     */
    public abstract AbstractSqlBuilder betweenNullable(String field, Object paramValue1, Object paramValue2, int sqlType);

    /**
     * Between操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue1 字段值1
     * @param paramValue2 字段值2
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     */
    public abstract AbstractSqlBuilder betweenNullable(String field, Object paramValue1, Object paramValue2, int sqlType, boolean sensitive);

    /**
     * Like操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder like(String field, Object paramValue, int sqlType) throws SQLException;

    /**
     * Like操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder like(String field, Object paramValue, int sqlType, boolean sensitive) throws SQLException;

    /**
     * Like操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     */
    public abstract AbstractSqlBuilder likeNullable(String field, Object paramValue, int sqlType);

    /**
     * Like操作，若字段值为NULL，则此条件不会加入SQL中
     * @param field 字段名称
     * @param paramValue 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     */
    public abstract AbstractSqlBuilder likeNullable(String field, Object paramValue, int sqlType, boolean sensitive);

    /**
     * In操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValues 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder in(String field, List<?> paramValues, int sqlType) throws SQLException;

    /**
     * In操作，且字段值不能为NULL，否则会抛出SQLException
     * @param field 字段名称
     * @param paramValues 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     * @throws SQLException SQLException
     */
    public abstract AbstractSqlBuilder in(String field, List<?> paramValues, int sqlType, boolean sensitive) throws SQLException;

    /**
     * In操作，允许参数为NULL，或者字段值为NULL, 或者传入的字段值数量为0
     * @param field 字段名称
     * @param paramValues 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @return this builder
     */
    public abstract AbstractSqlBuilder inNullable(String field, List<?> paramValues, int sqlType) throws SQLException;

    /**
     * In操作，允许参数为NULL，或者字段值为NULL, 或者传入的字段值数量为0
     * @param field 字段名称
     * @param paramValues 字段值
     * @param sqlType JDBC字段类型 (参考java.sql.Types)
     * @param sensitive 参数是否敏感 (敏感参数日志输出时会隐去)
     * @return this builder
     */
    public abstract AbstractSqlBuilder inNullable(String field, List<?> paramValues, int sqlType, boolean sensitive) throws SQLException;

    /**
     * Is null操作
     * @param field 字段名称
     * @return this builder
     */
    public abstract AbstractSqlBuilder isNull(String field);

    /**
     * Is not null操作
     * @param field 字段名称
     * @return this builder
     */
    public abstract AbstractSqlBuilder isNotNull(String field);

    /**
     * Add "("
     */
    public abstract AbstractSqlBuilder leftBracket();

    /**
     * Add ")"
     */
    public abstract AbstractSqlBuilder rightBracket();

    /**
     * Add "NOT"
     */
    public abstract AbstractSqlBuilder not();

}
