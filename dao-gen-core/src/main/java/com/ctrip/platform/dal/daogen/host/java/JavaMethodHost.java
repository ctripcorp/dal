package com.ctrip.platform.dal.daogen.host.java;

import com.ctrip.platform.dal.daogen.enums.ConditionType;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.utils.SqlBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.*;

public class JavaMethodHost {
    private String crud_type;
    private String name;
    private String sql;
    private String packageName;
    // DAO class name
    private String className;
    private String pojoClassName;
    private String comments;
    private List<JavaParameterHost> parameters;
    // Only for free sql query dao
    private List<JavaParameterHost> fields;

    private String scalarType;
    private String pojoType;

    private boolean paging;

    private List<String> inClauses = new ArrayList<>();

    // below is only for auto sql dao
    private String field;
    private String tableName;
    private String orderByExp = "";

    private List<JavaParameterHost> updateSetParameters;

    private String dbSetName;

    private String hints = null;
    private HashSet<String> hintsSet = null;
    private Boolean isAllShard = null;
    private Boolean isShards = null;
    private Boolean isAsync = null;
    private Boolean isCallback = null;
    private String allShard = "allShard";
    private String shards = "shards";
    private String async = "async";
    private String callback = "callback";
    private boolean length;

    public String getOrderByExp() {
        return orderByExp;
    }

    public void setOrderByExp(String orderByExp) {
        this.orderByExp = orderByExp;
    }

    public List<JavaParameterHost> getUpdateSetParameters() {
        return updateSetParameters;
    }

    public void setUpdateSetParameters(List<JavaParameterHost> updateSetParameters) {
        this.updateSetParameters = updateSetParameters;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPojoClassName() {
        if (this.isSampleType()) {
            if (null != this.fields && !this.fields.isEmpty()) {
                return this.fields.get(0).getClassDisplayName();
            }
        }
        return pojoClassName;
    }

    public void setPojoClassName(String pojoClassName) {
        this.pojoClassName = pojoClassName;
    }

    public List<JavaParameterHost> getFields() {
        return fields;
    }

    public void setFields(List<JavaParameterHost> fields) {
        this.fields = fields;
    }

    public String getCrud_type() {
        return crud_type;
    }

    public void setCrud_type(String crud_type) {
        this.crud_type = crud_type;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScalarType() {
        return scalarType;
    }

    public void setScalarType(String scalarType) {
        this.scalarType = scalarType;
    }

    public String getPojoType() {
        if (null != this.pojoType)
            return this.pojoType;
        if (null != this.fields && this.fields.size() == 1) {
            return "SimpleType";
        } else {
            return "";
        }
    }

    public void setPojoType(String pojoType) {
        this.pojoType = pojoType;
    }

    public String getSql() {
        return sql.replaceAll("[\r\n\t]", " ").replaceAll(" {2,}", " ");
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<JavaParameterHost> getParameters() {
        return parameters;
    }

    public void setParameters(List<JavaParameterHost> parameters) {
        this.parameters = parameters;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getVariableName() {
        return WordUtils.uncapitalize(pojoClassName);
    }

    public String getInClauses() {
        return StringUtils.join(this.inClauses, ",");
    }

    public boolean isInClauses() {
        return this.inClauses != null && !this.inClauses.isEmpty();
    }

    public boolean isPaging() {
        return paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public String getParameterNames() {
        String[] params = new String[parameters.size()];
        int i = 0;
        for (JavaParameterHost parameter : parameters) {
            ConditionType conditionType = parameter.getConditionType();
            if (isExcludedParameter(conditionType)) {
                continue;
            }
            params[i++] = parameter.getAlias();
        }

        return StringUtils.join(params, ", ");
    }

    public String getParameterNames(String suffix) {
        List<String> params = new ArrayList<>();
        for (JavaParameterHost parameter : parameters) {
            ConditionType conditionType = parameter.getConditionType();
            if (isExcludedParameter(conditionType)) {
                continue;
            }
            params.add(parameter.getAlias() + (null != suffix ? suffix : ""));
        }
        if (this.isPaging() && this.isQuery()) {
            params.add("1");
            params.add("10");
        }
        params.add("new DalHints()");
        return StringUtils.join(params, ", ");
    }

    public String getParameterDeclaration() {
        List<String> paramsDeclaration = new ArrayList<>();
        for (JavaParameterHost parameter : parameters) {
            ConditionType conditionType = parameter.getConditionType();
            if (ConditionType.In == conditionType) {
                paramsDeclaration
                        .add(String.format("List<%s> %s", parameter.getClassDisplayName(), parameter.getAlias()));
                this.inClauses.add(parameter.getAlias());
            } else if (isExcludedParameter(conditionType)) {
                continue;
            } else {
                paramsDeclaration.add(String.format("%s %s", parameter.getClassDisplayName(), parameter.getAlias()));
            }
        }
        if (this.paging && this.crud_type.equalsIgnoreCase("select")) {
            paramsDeclaration.add("int pageNo");
            paramsDeclaration.add("int pageSize");
        }

        paramsDeclaration.add("DalHints hints");

        if (isShards()) {
            paramsDeclaration.add("Set<String> shards");
        }
        if (isCallback()) {
            paramsDeclaration.add("DalResultCallback callback");
        }
        return StringUtils.join(paramsDeclaration, ", ");
    }

    public String getParameterDeclarationWithoutHints() {
        List<String> paramsDeclaration = new ArrayList<>();
        for (JavaParameterHost parameter : parameters) {
            ConditionType conditionType = parameter.getConditionType();
            if (ConditionType.In == conditionType) {
                paramsDeclaration
                        .add(String.format("List<%s> %s", parameter.getClassDisplayName(), parameter.getAlias()));
                this.inClauses.add(parameter.getAlias());
            } else if (isExcludedParameter(conditionType)) {
                continue;
            } else {
                paramsDeclaration.add(String.format("%s %s", parameter.getClassDisplayName(), parameter.getAlias()));
            }
        }
        if (this.paging && this.crud_type.equalsIgnoreCase("select")) {
            paramsDeclaration.add("int pageNo");
            paramsDeclaration.add("int pageSize");
        }

        return StringUtils.join(paramsDeclaration, ", ");
    }

    public String getActualParameter() {
        List<String> paramsDeclaration = new ArrayList<>();
        for (JavaParameterHost parameter : parameters) {
            ConditionType conditionType = parameter.getConditionType();
            if (ConditionType.In == conditionType) {
                paramsDeclaration.add(parameter.getAlias());
                this.inClauses.add(parameter.getAlias());
            } else if (isExcludedParameter(conditionType)) {
                continue;
            } else {
                paramsDeclaration.add(parameter.getAlias());
            }
        }
        if (this.paging && this.crud_type.equalsIgnoreCase("select")) {
            paramsDeclaration.add("pageNo");
            paramsDeclaration.add("pageSize");
        }

        paramsDeclaration.add("null");

        if (isShards()) {
            paramsDeclaration.add("null");
        }
        if (isCallback()) {
            paramsDeclaration.add("null");
        }
        return StringUtils.join(paramsDeclaration, ", ");
    }

    public String getUpdateParameterNames(String suffix) {
        List<String> params = new ArrayList<>();
        for (JavaParameterHost parameter : this.updateSetParameters) {
            params.add(parameter.getAlias() + (null != suffix ? suffix : ""));
        }
        for (JavaParameterHost parameter : this.parameters) {
            ConditionType conditionType = parameter.getConditionType();
            if (isExcludedParameter(conditionType)) {
                continue;
            }
            params.add(parameter.getAlias() + (null != suffix ? suffix : ""));
        }
        params.add("new DalHints()");
        return StringUtils.join(params, ", ");
    }

    public String getUpdateParameterDeclaration() {
        List<String> paramsDeclaration = new ArrayList<>();
        for (JavaParameterHost parameter : updateSetParameters) {
            if (ConditionType.In == parameter.getConditionType()) {
                paramsDeclaration
                        .add(String.format("List<%s> %s", parameter.getClassDisplayName(), parameter.getAlias()));
                this.inClauses.add(parameter.getAlias());
            } else {
                paramsDeclaration.add(String.format("%s %s", parameter.getClassDisplayName(), parameter.getAlias()));
            }
        }
        for (JavaParameterHost parameter : parameters) {
            ConditionType conditionType = parameter.getConditionType();
            if (conditionType == ConditionType.In) {
                paramsDeclaration
                        .add(String.format("List<%s> %s", parameter.getClassDisplayName(), parameter.getAlias()));
                this.inClauses.add(parameter.getAlias());
            } else if (isExcludedParameter(conditionType)) {
                continue;
            } else {
                paramsDeclaration.add(String.format("%s %s", parameter.getClassDisplayName(), parameter.getAlias()));
            }
        }

        paramsDeclaration.add("DalHints hints");
        if (isShards()) {
            paramsDeclaration.add("Set<String> shards");
        }
        if (isCallback()) {
            paramsDeclaration.add("DalResultCallback callback");
        }
        return StringUtils.join(paramsDeclaration, ", ");
    }

    public String getUpdateParameterDeclarationWithoutHints() {
        List<String> paramsDeclaration = new ArrayList<>();
        for (JavaParameterHost parameter : updateSetParameters) {
            if (ConditionType.In == parameter.getConditionType()) {
                paramsDeclaration
                        .add(String.format("List<%s> %s", parameter.getClassDisplayName(), parameter.getAlias()));
                this.inClauses.add(parameter.getAlias());
            } else {
                paramsDeclaration.add(String.format("%s %s", parameter.getClassDisplayName(), parameter.getAlias()));
            }
        }
        for (JavaParameterHost parameter : parameters) {
            ConditionType conditionType = parameter.getConditionType();
            if (conditionType == ConditionType.In) {
                paramsDeclaration
                        .add(String.format("List<%s> %s", parameter.getClassDisplayName(), parameter.getAlias()));
                this.inClauses.add(parameter.getAlias());
            } else if (isExcludedParameter(conditionType)) {
                continue;
            } else {
                paramsDeclaration.add(String.format("%s %s", parameter.getClassDisplayName(), parameter.getAlias()));
            }
        }

        return StringUtils.join(paramsDeclaration, ", ");
    }

    public String getUpdateActualParameter() {
        List<String> paramsDeclaration = new ArrayList<>();
        for (JavaParameterHost parameter : updateSetParameters) {
            if (ConditionType.In == parameter.getConditionType()) {
                paramsDeclaration.add(parameter.getAlias());
                this.inClauses.add(parameter.getAlias());
            } else {
                paramsDeclaration.add(parameter.getAlias());
            }
        }
        for (JavaParameterHost parameter : parameters) {
            ConditionType conditionType = parameter.getConditionType();
            if (conditionType == ConditionType.In) {
                paramsDeclaration.add(parameter.getAlias());
                this.inClauses.add(parameter.getAlias());
            } else if (isExcludedParameter(conditionType)) {
                continue;
            } else {
                paramsDeclaration.add(parameter.getAlias());
            }
        }

        paramsDeclaration.add("null");
        if (isShards()) {
            paramsDeclaration.add("null");
        }
        if (isCallback()) {
            paramsDeclaration.add("null");
        }
        return StringUtils.join(paramsDeclaration, ", ");
    }

    public Set<String> getPojoImports() {
        Set<String> imports = new TreeSet<>();
        List<JavaParameterHost> allTypes = new ArrayList<>(fields);
        for (JavaParameterHost field : allTypes) {
            Class<?> clazz = field.getJavaClass();
            if (byte[].class.equals(clazz))
                continue;
            if (clazz.getPackage().getName().equals(String.class.getPackage().getName()))
                continue;
            imports.add(clazz.getName());
        }
        return imports;
    }

    public List<String> getParamComments() {
        List<String> params = new ArrayList<>();
        for (JavaParameterHost parameter : parameters) {
            if (!parameter.isConditional())
                params.add(parameter.getAlias() + ": set clause");
        }
        return params;
    }

    public List<String> getConditionComments() {
        List<String> params = new ArrayList<>();
        for (JavaParameterHost parameter : parameters) {
            if (parameter.isConditional())
                params.add(parameter.getAlias() + ": where clause");
        }
        return params;
    }

    public String getPagingSql(DatabaseCategory dbType) throws Exception {
        return SqlBuilder.pagingQuerySql(sql, dbType, CurrentLanguage.Java).replaceAll("%s", "?");
    }

    public boolean hasParameters() {
        return null != this.parameters && !this.parameters.isEmpty();
    }

    public boolean isEmptyFields() {
        return null == this.fields || this.fields.isEmpty();
    }

    public boolean isSampleType() {
        return this.getPojoType().equalsIgnoreCase("SimpleType");
    }

    public boolean isReturnList() {
        return this.scalarType == null || this.scalarType.equalsIgnoreCase("List");
    }

    public boolean isReturnSingle() {
        return scalarType != null && this.scalarType.equalsIgnoreCase("Single");
    }

    public boolean isReturnFirst() {
        return this.scalarType != null && this.scalarType.equalsIgnoreCase("First");
    }

    public boolean isQuery() {
        return this.crud_type == null || this.crud_type.isEmpty() || this.crud_type.equalsIgnoreCase("select");
    }

    public boolean isUpdate() {
        return this.crud_type != null && this.crud_type.equalsIgnoreCase("update");
    }

    public String getDbSetName() {
        return dbSetName;
    }

    public void setDbSetName(String dbSetName) {
        this.dbSetName = dbSetName;
    }

    public String getHints() {
        return hints;
    }

    public void setHints(String hints) {
        this.hints = hints;
    }

    private HashSet<String> getHintsSet() {
        if (this.hintsSet == null) {
            this.hintsSet = new HashSet<>();
            if (this.hints == null || this.hints.length() == 0) {
                return this.hintsSet;
            }
            String[] array = this.hints.split(";");
            if (array != null && array.length > 0) {
                for (String string : array) {
                    this.hintsSet.add(string);
                }
            }
        }
        return this.hintsSet;
    }

    public boolean isAllShard() {
        if (isAllShard == null) {
            HashSet<String> hashSet = getHintsSet();
            isAllShard = hashSet.contains(allShard);
        }
        return isAllShard.booleanValue();
    }

    public boolean isShards() {
        if (isShards == null) {
            HashSet<String> hashSet = getHintsSet();
            isShards = hashSet.contains(shards);
        }
        return isShards.booleanValue();
    }

    public boolean isAsync() {
        if (isAsync == null) {
            HashSet<String> hashSet = getHintsSet();
            isAsync = hashSet.contains(async);
        }
        return isAsync.booleanValue();
    }

    public boolean isCallback() {
        if (isCallback == null) {
            HashSet<String> hashSet = getHintsSet();
            isCallback = hashSet.contains(callback);
        }
        return isCallback.booleanValue();
    }

    private boolean isExcludedParameter(ConditionType conditionType) {
        boolean result = false;
        if (conditionType == ConditionType.IsNull || conditionType == ConditionType.IsNotNull
                || conditionType == ConditionType.And || conditionType == ConditionType.Or
                || conditionType == ConditionType.Not || conditionType == ConditionType.LeftBracket
                || conditionType == ConditionType.RightBracket) {
            result = true;
        }
        return result;
    }

    public boolean getLength() {
        return length;
    }

    public void setLength(boolean length) {
        this.length = length;
    }

}
