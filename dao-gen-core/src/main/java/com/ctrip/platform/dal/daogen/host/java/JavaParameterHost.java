package com.ctrip.platform.dal.daogen.host.java;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.enums.ConditionType;
import com.ctrip.platform.dal.daogen.enums.ParameterDirection;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import org.apache.commons.lang.WordUtils;

import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

public class JavaParameterHost extends AbstractParameterHost {

    private int index;

    private int sqlType;

    private int length;

    private Class<?> javaClass;

    /**
     * The default field name
     */
    private String name;

    /**
     * Used for re-name where condition parameters
     */
    private String alias;

    private boolean identity;

    private boolean primary;

    private boolean nullable;

    private ParameterDirection direction;

    private Object validationValue;

    private boolean conditional;

    private ConditionType conditionType;

    private boolean sensitive = false;// whether the param is sensitive

    private boolean operator = false; //whether is opearator and,or,not

    public JavaParameterHost() {
    }

    public JavaParameterHost(JavaParameterHost host) {
        this.index = host.getIndex();
        this.sqlType = host.getSqlType();
        this.length = host.getLength();
        this.javaClass = host.getJavaClass();
        this.name = host.getName();
        this.alias = host.getAlias();
        this.identity = host.isIdentity();
        this.primary = host.isPrimary();
        this.nullable = host.isNullable();
        this.direction = host.getDirection();
        this.conditionType = host.getConditionType();
        this.validationValue = host.getValidationValue();
        this.conditional = host.isConditional();
        this.sensitive = host.isSensitive();
        this.operator = host.isOperator();
    }

    public boolean isInParameter() {
        return ConditionType.In == this.conditionType;
    }

    public boolean isConditional() {
        return conditional;
    }

    public void setConditional(boolean conditional) {
        this.conditional = conditional;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    public ParameterDirection getDirection() {
        return direction;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setDirection(ParameterDirection direction) {
        this.direction = direction;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return null != this.alias && !this.alias.isEmpty() ? this.alias : this.name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isIdentity() {
        return identity;
    }

    public void setIdentity(boolean identity) {
        this.identity = identity;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isOperator() {
        return operator;
    }

    public void setOperator(boolean operator) {
        this.operator = operator;
    }

    public String getCapitalizedName() {
        String tempName = name.replace("@", "");
        // if (tempName.contains("_")) {
        // tempName = WordUtils.capitalizeFully(tempName.replace('_', ' '))
        // .replace(" ", "");
        // }
        return WordUtils.capitalize(tempName);
    }

    public String getUncapitalizedName() {
        String tempName = name.replace("@", "");
        // if (tempName.contains("_")) {
        // tempName = WordUtils.capitalizeFully(tempName.replace('_', ' '))
        // .replace(" ", "");
        // }
        return WordUtils.uncapitalize(tempName);
    }

    public String getClassDisplayName() {
        if (javaClass == null) {
            return "";
        }
        if (byte[].class.equals(javaClass))
            return "byte[]";
        return javaClass.getSimpleName();
    }

    public String getJavaTypeDisplay() {
        return Consts.jdbcSqlTypeDisplay.get(this.sqlType);
    }

    private static Set<Integer> stringTypes = new HashSet<Integer>();

    static {
        stringTypes.add(Types.CHAR);
        stringTypes.add(Types.VARCHAR);
        stringTypes.add(Types.NVARCHAR);
        stringTypes.add(Types.LONGVARCHAR);
    }

    public Object getValidationValue() {
        if (stringTypes.contains(sqlType))
            return "\"" + "\"";
        if (validationValue == null)
            return "null";
        return validationValue;
    }

    public void setValidationValue(Object validationValue) {
        this.validationValue = validationValue;
    }

}