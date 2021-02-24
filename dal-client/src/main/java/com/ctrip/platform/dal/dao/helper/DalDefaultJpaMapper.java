package com.ctrip.platform.dal.dao.helper;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.UpdatableEntity;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalDefaultJpaMapper<T> implements DalRowMapper<T>, CustomizableMapper<T> {

    public Class<T> clazz = null;
    private String[] columnNames = null;
    private Map<String, Field> fieldsMap = null;
    private boolean ignorMissingFields = false;

    public DalDefaultJpaMapper(Class<T> clazz) throws SQLException {
        this.clazz = clazz;
        EntityManager manager = EntityManager.getEntityManager(clazz);
        this.columnNames = manager.getColumnNames();
        this.fieldsMap = manager.getFieldMap();
    }

    @Override
    public T map(ResultSet rs, int rowNum) throws SQLException {
        try {
            T instance = this.clazz.newInstance();
            for (int i = 0; i < columnNames.length; i++) {
                Field field = fieldsMap.get(columnNames[i]);
                if (field == null)
                    if (ignorMissingFields)
                        continue;
                    else
                        throw new DalException(ErrorCode.FieldNotExists, clazz.getName(), columnNames[i]);
                setValue(field, instance, rs, i);
            }

            if (instance instanceof UpdatableEntity)
                ((UpdatableEntity) instance).reset();

            return instance;
        } catch (Throwable e) {
            throw DalException.wrap(ErrorCode.ResultMappingError, e);
        }
    }

    private void setValue(Field field, Object entity, ResultSet rs, int index)
            throws ReflectiveOperationException, SQLException {
        Object val = rs.getObject(columnNames[index]);

        if (val == null) {
            field.set(entity, val);
            return;
        }

        Class<?> clazz = field.getType();

        // The following order is optimized for most cases
        if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            field.set(entity, ((Number) val).longValue());
            return;
        }
        if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            field.set(entity, ((Number) val).intValue());
            return;
        }
        if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            field.set(entity, ((Number) val).doubleValue());
            return;
        }
        if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            field.set(entity, ((Number) val).floatValue());
            return;
        }
        if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            field.set(entity, ((Number) val).byteValue());
            return;
        }
        if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            field.set(entity, ((Number) val).shortValue());
            return;
        }
        if (clazz.equals(BigInteger.class)) {
            BigInteger bigIntegerValue = BigInteger.valueOf(((Number) val).longValue());
            field.set(entity, bigIntegerValue);
            return;
        }
        /**
         * This is because oracle returns its own Timestamp type instead of standard java.sql.Timestamp
         */
        if (field.getType().equals(Timestamp.class) && !(val instanceof Timestamp)) {
            field.set(entity, rs.getTimestamp(columnNames[index]));
            return;
        }
        field.set(entity, val);
    }

    @Override
    public DalRowMapper<T> mapWith(String[] columns) throws SQLException {
        return new DalDefaultJpaMapper<T>(this, columns);
    }

    @Override
    public DalRowMapper<T> mapWith(ResultSet rs, DalHints hints) throws SQLException {
        return new DalDefaultJpaMapper<T>(this, rs, hints);
    }

    /**
     * For map partial result set with given column names. Copy fields from rawMapper
     * 
     * @param rawMapper
     * @param columns
     * @throws SQLException
     */
    private DalDefaultJpaMapper(DalDefaultJpaMapper<T> rawMapper, String[] columns) throws SQLException {
        this.clazz = rawMapper.clazz;
        this.fieldsMap = rawMapper.fieldsMap;
        this.ignorMissingFields = rawMapper.ignorMissingFields;
        this.columnNames = columns;
    }

    /**
     * For map partial result set with given column names. Copy fields from rawMapper
     * 
     * @param rawMapper
     * @param rs
     * @throws SQLException
     */
    private DalDefaultJpaMapper(DalDefaultJpaMapper<T> rawMapper, ResultSet rs, DalHints hints) throws SQLException {
        this.clazz = rawMapper.clazz;
        this.fieldsMap = rawMapper.fieldsMap;
        this.ignorMissingFields = hints.is(DalHintEnum.ignoreMissingFields);

        // User user defined columns if it is partial query case
        this.columnNames = hints.is(DalHintEnum.partialQuery) ? hints.getPartialQueryColumns() : rawMapper.columnNames;

        if (hints.is(DalHintEnum.allowPartial) == false)
            return;

        Set<String> preDefinedColumns = toSet(columnNames);

        Set<String> resetSetColumns = new HashSet<>();
        // Delay the retrieval of ResultSetMetaData as much as possible,
        // because different driver implements this at different cost.
        // Some may require an additional round-trip of network
        ResultSetMetaData rsMeta = rs.getMetaData();
        int colCount = rsMeta.getColumnCount();
        for (int i = 0; i < colCount; i++) {
            resetSetColumns.add(rsMeta.getColumnLabel(i + 1));
        }

        // If what user specifies is a subset of actual result set columns set
        if (resetSetColumns.containsAll(preDefinedColumns))
            return;

        // Get the common set of both
        preDefinedColumns.retainAll(resetSetColumns);
        columnNames = preDefinedColumns.toArray(new String[preDefinedColumns.size()]);
    }

    private Set<String> toSet(String[] values) {
        Set<String> s = new HashSet<>();
        for (String v : values)
            s.add(v);
        return s;
    }

    public Class getClazz() {
        return this.clazz;
    }
}
