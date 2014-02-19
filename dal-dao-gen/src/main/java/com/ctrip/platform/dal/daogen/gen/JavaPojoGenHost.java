package com.ctrip.platform.dal.daogen.gen;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.WordUtils;

import com.ctrip.platform.dal.daogen.pojo.FieldMeta;

public class JavaPojoGenHost {
	public static enum NamingStyle {
		ABC_DEF,
		abc_def,
		Abc_Def,
		AbcDef,
		abcDef,
	}
	private String daoNamespace;
	
	private String className;
	
	private List<FieldMeta> fields;
	
	private NamingStyle style = NamingStyle.abcDef;

	public String getDaoNamespace() {
		return daoNamespace;
	}

	public void setDaoNamespace(String daoNamespace) {
		this.daoNamespace = daoNamespace;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<FieldMeta> getFields() {
		return fields;
	}

	public void setFields(List<FieldMeta> fields) {
		this.fields = fields;
		buildImports();
	}
	
//	private static Map<Class, String> templates = new HashMap<Class, String>();
//	
//	static {
//		//pojo.set${field.getName()}(rs.get$WordUtils.capitalize($field.getType())("${field.getName()}"));
//		templates.put(Byte.class, "pojo.set%s((%s)rs.getObject(%s);");
//		templates.put(byte[].class, "pojo.set%s((%s)rs.getBytes(%s);");
//		
//		templates.put(java.sql.Date.class, "pojo.set%s((%s)rs.getDate(%s);");
//		templates.put(java.sql.Time.class, "pojo.set%s((%s)rs.getTime(%s);");
//		templates.put(java.sql.Timestamp.class, "pojo.set%s((%s)rs.getTimestamp(%s);");
//
//	}
	
	private Set<String> imports = new TreeSet<String>();
	private void buildImports() {
		
		for(FieldMeta field: fields) {
			Class clazz = field.getJavaClass();
			if(clazz.getPackage().getName().equals(String.class.getPackage().getName()))
				continue;
			if(byte[].class.equals(clazz))
				continue;
			imports.add(clazz.getName());
		}
	}
	
	public Set<String> getImports() {
		return imports;
	}
	
	public void setStyle(NamingStyle style) {
		this.style = style;
	}
	
	public String getFieldAccessorName(String fieldName) {
		switch (style) {
		case ABC_DEF:
		case abc_def:
		case Abc_Def:
			return WordUtils.capitalize(fieldName.replace("_", " ")).replace("_", "");
		case AbcDef:
			return fieldName;
		case abcDef:
			return WordUtils.capitalize(fieldName, new char[]{'_'});
		default:
			return WordUtils.capitalize(fieldName);
		}
	}
	
	public String getFieldName(String fieldName) {
		return WordUtils.uncapitalize(getFieldName(fieldName));
	}
	
	public String getClassDisplayName(Class clazz) {
		if(byte[].class.equals(clazz))
			return "byte[]";
		return clazz.getSimpleName();
	}

}
