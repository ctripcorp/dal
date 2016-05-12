package com.ctrip.platform.dal.daogen.host.csharp;

import com.ctrip.platform.dal.daogen.enums.ConditionType;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;
import com.ctrip.platform.dal.daogen.utils.SqlBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSharpMethodHost {

    private String crud_type;

    private String name;

    private String sql;

    private List<CSharpParameterHost> parameters;

    private CSharpFreeSqlPojoHost pojohost;

    private List<JavaParameterHost> fields;

    private String pojoName;

    private String scalarType;

    private String pojoType;

    private boolean paging;

    private String orderByExp = "";

    public String getOrderByExp() {
        return orderByExp;
    }

    public void setOrderByExp(String orderByExp) {
        this.orderByExp = orderByExp;
    }

    public String getParameterDeclaration() {
        List<String> paramsDeclaration = new ArrayList<>();
        for (CSharpParameterHost parameter : parameters) {
            ConditionType conditionType = parameter.getConditionType();
            if (conditionType == ConditionType.In || parameter.isInParameter()) {
                paramsDeclaration.add(String.format("List<%s> %s", parameter.getType(), WordUtils.uncapitalize(parameter.getAlias())));
            } else if (conditionType == ConditionType.IsNull || conditionType == ConditionType.IsNotNull
                    || conditionType == ConditionType.And || conditionType == ConditionType.Or
                    || conditionType == ConditionType.Not || conditionType == ConditionType.LeftBracket
                    || conditionType == ConditionType.RightBracket) {
                continue;// is null、is not null don't hava param
            } else {
                paramsDeclaration.add(String.format("%s %s", parameter.getType(), WordUtils.uncapitalize(parameter.getAlias())));
            }
        }
        if (this.paging && this.crud_type.equalsIgnoreCase("select")) {
            paramsDeclaration.add("int pageNo");
            paramsDeclaration.add("int pageSize");
        }
        return StringUtils.join(paramsDeclaration, ", ");
    }

    public boolean paramTypeIsNotNull(CSharpParameterHost parameter) {
        return parameter.getConditionType() != ConditionType.IsNull && parameter.getConditionType() != ConditionType.IsNotNull;
    }

    public String getCrud_type() {
        return crud_type;
    }

    public void setCrud_type(String crud_type) {
        this.crud_type = crud_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSql() {
        return sql.replaceAll("[\r\n\t]", " ").replaceAll(" {2,}", " ");
    }

    public String getBuildCudSql() {
        String temp = sql;
        String regex = "(?i)In *\\(@\\w+\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(sql);
        int index = 0;
        while (m.find()) {
            temp = temp.replaceFirst(regex, String.format("IN ({%s})", index++));
        }
        return temp;
    }

    public String getSql(DatabaseCategory databaseCategory) {
        if (databaseCategory == DatabaseCategory.MySql) {
            return sql + " limit 0,1";
        }
        if (databaseCategory == DatabaseCategory.SqlServer) {
            return sql.replaceFirst("(?i)select", "SELECT TOP 1 ");
        }
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<CSharpParameterHost> getParameters() {
        return parameters;
    }

    public void setParameters(List<CSharpParameterHost> parameters) {
        this.parameters = parameters;
    }

    public List<JavaParameterHost> getFields() {
        return fields;
    }

    public void setFields(List<JavaParameterHost> fields) {
        this.fields = fields;
    }

    public String getPojoName() {
        return pojoName;
    }

    public void setPojoName(String pojoName) {
        this.pojoName = pojoName;
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

    public CSharpFreeSqlPojoHost getPojohost() {
        return pojohost;
    }

    public boolean isPaging() {
        return paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public void setPojohost(CSharpFreeSqlPojoHost pojohost) {
        this.pojohost = pojohost;
    }

    public boolean isFirstOrSingle() {
        return (this.scalarType != null) && (!this.scalarType.isEmpty()) && (this.scalarType.equalsIgnoreCase("First") || this.scalarType.equalsIgnoreCase("Single"));
    }

    public boolean isSampleType() {
        return this.getPojoType().equalsIgnoreCase("SimpleType");
    }

    public CSharpParameterHost getSinglePojoFieldHost() {
        return this.pojohost.getColumns().get(0);
    }

    public String getPagingSql(DatabaseCategory dbType) throws Exception {
        return SqlBuilder.pagingQuerySql(sql, dbType, CurrentLanguage.CSharp);
    }

    public boolean hasParameters() {
        return null != this.parameters && !this.parameters.isEmpty();
    }
}
