package com.ctrip.platform.dal.daogen.java;

import org.apache.commons.lang.WordUtils;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.Consts;

public class JavaParameterHost extends AbstractParameterHost {
	
	private int index;
	
	private int sqlType;
	
	private int length;

	private Class<?> javaClass;
	
	private String name;
	
	private boolean identity;
	
	private boolean primary;
	
	private boolean nullable;
	
	private ParameterDirection direction;
	
	private Object validationValue;

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
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
	
	public String getCapitalizedName() {
		String tempName = name.replace("@", "");
		if(tempName.contains("_")) {
			tempName = WordUtils.capitalizeFully(name.replace('_', ' ')).replace(" ", "");
		}
		return WordUtils.capitalize(tempName);
	}
	
	public String getUncapitalizedName() {
		String tempName = name.replace("@", "");
		if(tempName.contains("_")) {
			tempName = WordUtils.capitalizeFully(name.replace('_', ' ')).replace(" ", "");
		}
		return WordUtils.uncapitalize(tempName);
	}
	
	public String getClassDisplayName() {
		if(byte[].class.equals(javaClass))
			return "byte[]";
		return javaClass.getSimpleName();
	}
	
	public String getJavaTypeDisplay() {
		return Consts.jdbcSqlTypeDisplay.get(sqlType);
	}

	public Object getValidationValue() {
		return validationValue;
	}

	public void setValidationValue(Object validationValue) {
		this.validationValue = validationValue;
	}
}
