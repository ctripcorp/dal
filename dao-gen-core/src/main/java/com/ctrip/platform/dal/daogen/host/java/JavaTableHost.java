package com.ctrip.platform.dal.daogen.host.java;

import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.util.*;

public class JavaTableHost {
    private DatabaseCategory databaseCategory;
    private String packageName;
    private String dbSetName;
    private String tableName;
    private String pojoClassName;
    private List<JavaParameterHost> fields;
    private List<JavaParameterHost> primaryKeys;
    private boolean hasIdentity;
    private String identityColumnName;
    private boolean Sp;
    private SpOperationHost SpInsert;
    private SpOperationHost SpDelete;
    private SpOperationHost SpUpdate;
    private List<JavaMethodHost> methods = new ArrayList<>();
    private String api_list;
    private boolean length;

    public boolean generateAPI(Integer... apiID) {
        if (api_list == null || api_list.isEmpty()) {
            return true;
        }
        String[] list = api_list.split(",");
        Arrays.sort(list);
        for (int i = 0; i < apiID.length; i++) {
            if (Arrays.binarySearch(list, "dal_api_" + apiID[i]) >= 0) {
                return true;
            }
        }
        return false;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSp() {
        return Sp;
    }

    public void setSp(boolean isSp) {
        this.Sp = isSp;
    }

    public boolean isSpa() {
        return null != this.SpInsert && this.SpInsert.isExist();
    }

    public String getDbSetName() {
        return dbSetName;
    }

    public void setDbSetName(String dbSetName) {
        this.dbSetName = dbSetName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPojoClassName() {
        return pojoClassName;
    }

    public void setPojoClassName(String pojoClassName) {
        this.pojoClassName = pojoClassName;
    }

    public List<JavaMethodHost> getMethods() {
        return methods;
    }

    public void setMethods(List<JavaMethodHost> methods) {
        this.methods = methods;
    }

    public boolean hasMethods() {
        return this.methods != null && !this.methods.isEmpty();
    }

    public List<JavaParameterHost> getFields() {
        return fields;
    }

    public void setFields(List<JavaParameterHost> fields) {
        this.fields = fields;
    }

    public List<JavaParameterHost> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<JavaParameterHost> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public boolean isHasIdentity() {
        return hasIdentity;
    }

    public void setHasIdentity(boolean hasIdentity) {
        this.hasIdentity = hasIdentity;
    }

    public String getCapitalizedIdentityColumnName() {
        for (JavaParameterHost field : fields) {
            if (field.getName().equals(identityColumnName))
                return field.getCapitalizedName();
        }
        return null;
    }

    public String getIdentityColumnName() {
        return identityColumnName;
    }

    public void setIdentityColumnName(String identityColumnName) {
        this.identityColumnName = identityColumnName;
    }

    public SpOperationHost getSpInsert() {
        return SpInsert;
    }

    public void setSpInsert(SpOperationHost SpInsert) {
        this.SpInsert = SpInsert;
    }

    public SpOperationHost getSpDelete() {
        return SpDelete;
    }

    public void setSpDelete(SpOperationHost SpDelete) {
        this.SpDelete = SpDelete;
    }

    public SpOperationHost getSpUpdate() {
        return SpUpdate;
    }

    public void setSpUpdate(SpOperationHost SpUpdate) {
        this.SpUpdate = SpUpdate;
    }

    public DatabaseCategory getDatabaseCategory() {
        return databaseCategory;
    }

    public void setDatabaseCategory(DatabaseCategory databaseCategory) {
        this.databaseCategory = databaseCategory;
    }

    public String getApi_list() {
        return api_list;
    }

    public void setApi_list(String api_list) {
        this.api_list = api_list;
    }

    public boolean isIntegerPk() {
        Class<?> clazz = primaryKeys.get(0).getJavaClass();
        return primaryKeys.size() == 1
                && (clazz.equals(Integer.class) || clazz.equals(Long.class) || clazz.equals(BigInteger.class));
    }

    public String pageBegain() {
        if (this.databaseCategory == DatabaseCategory.MySql) {
            return "(pageNo - 1) * pageSize";
        } else {
            return "(pageNo - 1) * pageSize + 1";
        }
    }

    public String pageEnd() {
        if (this.databaseCategory == DatabaseCategory.MySql) {
            return "pageSize";
        } else {
            return "pageSize * pageNo";
        }
    }

    public String getPkParameterDeclaration() {
        List<String> paramsDeclaration = new ArrayList<>();
        for (JavaParameterHost parameter : primaryKeys) {
            paramsDeclaration.add(
                    String.format("%s %s", parameter.getClassDisplayName(), parameter.getCamelCaseUncapitalizedName()));
        }
        paramsDeclaration.add(String.format("%s %s", "DalHints", "hints"));
        return StringUtils.join(paramsDeclaration, ", ");
    }

    public String getPkParameterDeclarationWithoutHints() {
        List<String> paramsDeclaration = new ArrayList<>();
        for (JavaParameterHost parameter : primaryKeys) {
            paramsDeclaration.add(
                    String.format("%s %s", parameter.getClassDisplayName(), parameter.getCamelCaseUncapitalizedName()));
        }
        return StringUtils.join(paramsDeclaration, ", ");
    }

    public String getPkParameters() {
        List<String> paramsDeclaration = new ArrayList<>();
        for (JavaParameterHost parameter : primaryKeys) {
            paramsDeclaration.add(parameter.getCamelCaseUncapitalizedName());
        }
        return StringUtils.join(paramsDeclaration, ", ");
    }

    public String getPkParametersList() {
        List<String> paramsList = new ArrayList<>();
        for (JavaParameterHost parameter : primaryKeys) {
            paramsList.add(parameter.getUncapitalizedName());
        }

        paramsList.add("new DalHints()");
        return StringUtils.join(paramsList, ",");
    }

    public boolean hasPk() {
        return null != primaryKeys && !primaryKeys.isEmpty();
    }

    public Set<String> getDaoImports() {
        Set<String> imports = new TreeSet<>();
        imports.add("com.ctrip.platform.dal.dao.*");
        imports.add("com.ctrip.platform.dal.dao.sqlbuilder.*");

        imports.add(java.sql.SQLException.class.getName());
        imports.add(java.sql.Types.class.getName());
        imports.add(List.class.getName());

        List<JavaParameterHost> allTypes = new ArrayList<>(fields);
        for (JavaMethodHost method : methods) {
            allTypes.addAll(method.getParameters());
        }

        if (SpInsert != null)
            allTypes.addAll(SpInsert.getParameters());
        if (SpDelete != null)
            allTypes.addAll(SpDelete.getParameters());
        if (SpUpdate != null)
            allTypes.addAll(SpUpdate.getParameters());

        for (JavaParameterHost field : allTypes) {
            try {
                Class<?> clazz = field.getJavaClass();
                if (byte[].class.equals(clazz))
                    continue;
                if (null == clazz)
                    continue;
                if (clazz.getPackage().getName().equals(String.class.getPackage().getName()))
                    continue;
                imports.add(clazz.getName());
            } catch (Throwable e) {
                throw e;
            }
        }

        return imports;
    }

    public Set<String> getTestImports() {
        Set<String> imports = new TreeSet<>();
        imports.add(List.class.getName());
        imports.addAll(this.getPojoImports());
        return imports;
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

    /**
     * Get the CTE order by columns to generate row-number
     *
     * @return
     */
    public String getOverColumns() {
        List<String> tokens = new ArrayList<>();
        for (JavaParameterHost p : this.fields) {
            if (p.isPrimary())
                tokens.add(p.getName());
        }
        if (tokens.size() > 0)
            return StringUtils.join(tokens, ",");
        else
            return this.fields.get(0).getName();
    }

    public String getPrimaryKeyName() throws Exception {
        if (this.getPrimaryKeys() == null && this.getPrimaryKeys().size() != 1)
            throw new Exception("The multiple primary key is not allowed here!");
        return this.getPrimaryKeys().get(0).getCapitalizedName();
    }

    public String getPrimaryKeyType() throws Exception {
        if (this.getPrimaryKeys() == null && this.getPrimaryKeys().size() != 1)
            throw new Exception("The multiple primary key is not allowed here!");
        return this.getPrimaryKeys().get(0).getClassDisplayName();
    }

    public String getScalarColumn() {
        return (null != this.fields && !this.fields.isEmpty()) ? this.fields.get(0).getClassDisplayName() : "";
    }

    public boolean isSampleType() {
        return null != this.fields && this.fields.size() == 1;
    }

    public boolean getLength() {
        return length;
    }

    public void setLength(boolean length) {
        this.length = length;
    }

}
