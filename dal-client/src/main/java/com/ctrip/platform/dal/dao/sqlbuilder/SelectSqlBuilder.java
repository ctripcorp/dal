package com.ctrip.platform.dal.dao.sqlbuilder;


import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.CustomizableMapper;
import com.ctrip.platform.dal.dao.helper.DalFirstResultMerger;
import com.ctrip.platform.dal.dao.helper.DalListMerger;
import com.ctrip.platform.dal.dao.helper.DalObjectRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRangedResultMerger;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalSingleResultExtractor;
import com.ctrip.platform.dal.dao.helper.DalSingleResultMerger;
import com.ctrip.platform.dal.dao.helper.EntityManager;

public class SelectSqlBuilder extends AbstractTableSqlBuilder implements SelectBuilder, TableSelectBuilder {
    private static final String ALL = "*";
    private static final String ALL_COLUMNS = "***";
    private static final String COUNT = "COUNT(1)";
    private static final String SPACE = " ";
    private static final String ORDER_BY = "ORDER BY ";
    private static final String ASC = " ASC";
    private static final String DESC = " DESC";
    private static final String ORDER_BY_SEPARATOR = ", ";
    private static final String QUERY_ALL_CRITERIA = "1=1";

    private String[] selectedColumns;
    private String customized;

    private StatementParameters parameters;
    private String whereClause;

    private Map<String, Boolean> orderBys = new LinkedHashMap<>();

    @SuppressWarnings("rawtypes")
    private DalRowMapper mapper;

    @SuppressWarnings("rawtypes")
    private ResultMerger merger;

    @SuppressWarnings("rawtypes")
    private DalResultSetExtractor extractor;

    private boolean requireFirst = false;
    private boolean requireSingle = false;
    private boolean requireTop = false;

    private boolean nullable = false;

    private int count;
    private int start;

    private boolean isPagination = false;

    /**
     * Important Note: In this case, the generated code with set page info into statement parameters.
     * You are recommended to re-generate code using the code generator. The new code will use the other two constructor instead
     *
     * @param tableName    表名
     * @param dbCategory   数据库类型
     * @param isPagination 是否分页. If it is true, it means the code is running with old generated code
     * @throws SQLException
     * @deprecated If you see this, please regenerate dal code with code gen
     */
    public SelectSqlBuilder(String tableName,
                            DatabaseCategory dbCategory, boolean isPagination)
            throws SQLException {
        this();
        from(tableName).setDatabaseCategory(dbCategory);
        this.isPagination = isPagination;
        setCompatible(true);
    }

    public SelectSqlBuilder() {
        selectAll();
        nullable();
    }

    /**
     * Which means user provide parameters and where clause
     *
     * @param parameters
     * @return
     */
    public SelectSqlBuilder with(StatementParameters parameters) {
        this.parameters = parameters;
        return this;
    }

    @Override
    public StatementParameters buildParameters() {
        return parameters == null ? super.buildParameters() : parameters;
    }

    public SelectSqlBuilder from(String tableName) throws SQLException {
        super.from(tableName);
        return this;
    }

    public SelectSqlBuilder setDatabaseCategory(DatabaseCategory dbCategory) throws SQLException {
        super.setDatabaseCategory(dbCategory);
        return this;
    }

    /**
     * 添加select字段
     *
     * @param fieldName
     * @return
     */
    public SelectSqlBuilder select(String... fieldName) {
        selectedColumns = fieldName;
        customized = null;
        return this;
    }

    public SelectSqlBuilder selectAll() {
        this.customized = ALL;
        selectedColumns = null;
        return this;
    }

    public SelectSqlBuilder selectAllColumns() {
        this.customized = ALL_COLUMNS;
        selectedColumns = null;
        return this;
    }

    public SelectSqlBuilder selectCount() {
        this.customized = COUNT;
        selectedColumns = null;
        mergerWith(new ResultMerger.LongNumberSummary());
        requireSingle();
        simpleType();

        return this;
    }

    /**
     * Set where clause directly
     *
     * @param whereClause
     * @return
     */
    public SelectSqlBuilder where(String whereClause) {
        whereClause = whereClause.trim();
        this.whereClause = whereClause.length() == 0 ? QUERY_ALL_CRITERIA : whereClause;
        return this;
    }

    /**
     * 追加order by字段
     *
     * @param fieldName 字段名
     * @param ascending 是否升序
     * @return
     */
    public SelectSqlBuilder orderBy(String fieldName, boolean ascending) {
        orderBys.put(fieldName, ascending);
        return this;
    }

    public SelectSqlBuilder top(int count) {
        this.count = count;
        if (requireTop == false)
            requireTop = true;
        return this;
    }

    /**
     * Construct with pagenation
     *
     * @param pageNo
     * @param pageSize
     * @throws SQLException
     */
    public SelectSqlBuilder atPage(int pageNo, int pageSize)
            throws SQLException {
        if (pageNo < 1 || pageSize < 1)
            throw new SQLException("Illigal pagesize or pageNo, please check");

        range((pageNo - 1) * pageSize, pageSize);

        return this;
    }

    @Override
    public SelectSqlBuilder range(int start, int count) {
        this.start = start;
        this.count = count;
        return this;
    }

    public SelectSqlBuilder requireFirst() {
        requireFirst = true;
        return this;
    }

    public SelectSqlBuilder requireSingle() {
        requireSingle = true;
        return this;
    }

    public SelectSqlBuilder nullable() {
        nullable = true;
        return this;
    }

    public boolean isRequireFirst() {
        return requireFirst;
    }

    public boolean isRequireSingle() {
        return requireSingle;
    }

    public boolean isNullable() {
        return nullable;
    }

    @Override
    public <T> SelectSqlBuilder mergerWith(ResultMerger<T> merger) {
        this.merger = merger;
        return this;
    }

    @Override
    public <T> SelectSqlBuilder extractorWith(DalResultSetExtractor<T> extractor) {
        this.extractor = extractor;
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> ResultMerger<T> getResultMerger(DalHints hints) {
        if (hints.is(DalHintEnum.resultMerger))
            return (ResultMerger<T>) hints.get(DalHintEnum.resultMerger);

        if (merger != null)
            return merger;

        if (isRequireSingle() || isRequireFirst())
            return isRequireSingle() ? new DalSingleResultMerger() : new DalFirstResultMerger((Comparator) hints.getSorter());

        return count > 0 ? new DalRangedResultMerger((Comparator) hints.getSorter(), count) : new DalListMerger((Comparator) hints.getSorter());
    }

    @Override
    public <T> SelectSqlBuilder mapWith(DalRowMapper<T> mapper) {
        this.mapper = mapper;
        return this;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> SelectBuilder mapWith(Class<T> type) {
        return mapWith(EntityManager.getMapper(type));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public SelectSqlBuilder simpleType() {
        return mapWith(new DalObjectRowMapper());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> DalResultSetExtractor<T> getResultExtractor(DalHints hints) throws SQLException {
        if (extractor != null)
            return extractor;

        DalRowMapper<T> mapper = checkAllowPartial(hints);
        if (isRequireSingle() || isRequireFirst())
            return new DalSingleResultExtractor<>(mapper, isRequireSingle());

        return count > 0 ? new DalRowMapperExtractor(mapper, count) : new DalRowMapperExtractor(mapper);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> DalRowMapper<T> checkAllowPartial(DalHints hints) throws SQLException {
        if (!(mapper instanceof CustomizableMapper))
            return mapper;

        // If it is COUNT case, we do nothing here
        if (customized == COUNT)
            return mapper;

        if (customized == ALL) {
            if (hints.is(DalHintEnum.selectByNames))
                customized = ALL_COLUMNS;
            else
                return mapper;
        }

        if (customized == ALL_COLUMNS) {
            return mapper;
        }

        if (hints.is(DalHintEnum.partialQuery))
            return mapper;

        // Use what user selected and customize mapper
        return ((CustomizableMapper) mapper).mapWith(selectedColumns);
    }

    /**
     * This method has to be backward compatible. The old generator will generated like
     * String sql = builder.build();
     * <p>
     * For page:
     * StatementParameters parameters = builder.buildParameters();
     * int index =  builder.getStatementParameterIndex();
     * parameters.set(index++, Types.INTEGER, (pageNo - 1) * pageSize + 1);
     * parameters.set(index++, Types.INTEGER, pageSize * pageNo);
     * return queryDao.query(sql, parameters, hints, parser);
     * <p>
     * Or for first result
     * return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints, parser);
     * <p>
     * Or for single result
     * return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints, parser);
     *
     * @return
     */
    public String build() {
        preBuild();

        String sql = internalBuild(getTableName());
        String suffix = getDbCategory().getPageSuffixTpl();

        // If it is the old code gen case, we need to append page suffix
        return isPagination ? sql + suffix : sql;
    }

    /**
     * For backward compatible
     * 对于select first，会在语句中追加limit 0,1(MySQL)或者top 1(SQL Server)：
     *
     * @return
     */
    public String buildFirst() {
        requireFirst = true;
        return build();
    }

    /**
     * Only the newly generated code will use this method
     */
    public String build(String shardStr) {
        preBuild();
        return internalBuild(getTableName() + shardStr);
    }

    private void preBuild() {
        /**
         * If the template is already set
         */
        if (whereClause == null)
            where(getWhereExp());
    }

    private String internalBuild(String effectiveTableName) {
        effectiveTableName = wrapField(effectiveTableName);

        if (requireFirst)
            return buildFirst(effectiveTableName);

        if (requireTop && count > 0)
            return buildTop(effectiveTableName);

        if (start >= 0 && count > 0)
            return buildPage(effectiveTableName);

        return buildList(effectiveTableName);
    }

    private String getCompleteWhereExp() {
        return orderBys.size() == 0 ? whereClause : whereClause + SPACE + buildOrderbyExp();
    }

    private String buildOrderbyExp() {
        StringBuilder orderbyExp = new StringBuilder();

        orderbyExp.append(ORDER_BY);
        boolean first = true;
        for (String orderBy : orderBys.keySet()) {
            if (first)
                first = false;
            else
                orderbyExp.append(ORDER_BY_SEPARATOR);

            orderbyExp.append(wrapField(orderBy));
            orderbyExp.append(orderBys.get(orderBy) ? ASC : DESC);
        }

        return orderbyExp.toString();
    }

    /**
     * 对字段进行包裹，数据库是MySQL则用 `进行包裹，数据库是SqlServer则用[]进行包裹
     *
     * @param fieldName
     * @return
     */
    public String wrapField(String fieldName) {
        return AbstractTableSqlBuilder.wrapField(getDbCategory(), fieldName);
    }

    private String buildFirst(String effectiveTableName) {
        count = 1;
        return buildTop(effectiveTableName);
    }

    private String buildTop(String effectiveTableName) {
        return getDbCategory().buildTop(effectiveTableName, buildColumns(), getCompleteWhereExp(), count);
    }

    private String buildPage(String effectiveTableName) {
        return getDbCategory().buildPage(effectiveTableName, buildColumns(), getCompleteWhereExp(), start, count);
    }

    private String buildList(String effectiveTableName) {
        return getDbCategory().buildList(effectiveTableName, buildColumns(), getCompleteWhereExp());
    }

    @SuppressWarnings("rawtypes")
    private String buildColumns() {
        if (customized == ALL_COLUMNS) {
            selectedColumns = ((DalParser) mapper).getColumnNames();
            customized = null;
        }

        if (customized != null)
            return customized;

        if (selectedColumns != null) {
            StringBuilder fieldBuf = new StringBuilder();
            for (int i = 0, count = selectedColumns.length; i < count; i++) {
                fieldBuf.append(this.wrapField(selectedColumns[i]));
                if (i < count - 1) {
                    fieldBuf.append(", ");
                }
            }

            return fieldBuf.toString();
        }

        // This will be an exceptional case
        return SPACE;
    }
}