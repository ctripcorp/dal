package com.ctrip.platform.dal.daogen.host.csharp;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.daogen.AbstractParameterHost;

public class CSharpParameterHost extends AbstractParameterHost  implements Comparable<CSharpParameterHost> {
	
	private String name;
	
	private String comment;
	
	private String alias;
	
	//where条件是否是in,如 select * from Person where id in ?
	private boolean inParameter;
	
	private ParameterDirection direction;
	
	//C#的DbType
	private DbType dbType;
	
	//C#的数据类型
	private String type;
	
	private boolean identity;
	
	private boolean primary;
	
	private boolean nullable;
	
	private boolean valueType;
	
	public CSharpParameterHost(){ }
	
	public CSharpParameterHost(CSharpParameterHost host)
	{
		this.name = host.getName();
		this.alias = host.getAlias();
		this.inParameter = host.isInParameter();
		this.direction = host.getDirection();
		this.dbType = host.getDbType();
		this.type = host.getType();
		this.identity = host.isIdentity();
		this.primary = host.isPrimary();
		this.nullable = host.isNullable();
		this.valueType = host.isValueType();
		this.comment = host.getComment();
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isInParameter() {
		return inParameter;
	}

	public void setInParameter(boolean inParameter) {
		this.inParameter = inParameter;
	}

	public boolean isValueType() {
		return valueType;
	}

	public void setValueType(boolean valueType) {
		this.valueType = valueType;
	}

	private int length;
	
	private Object value;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isIdentity() {
		return identity;
	}

	public void setIdentity(boolean identity) {
		this.identity = identity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAlias() {
		return null != this.alias && ! this.alias.isEmpty() ? this.alias : this.name;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public ParameterDirection getDirection() {
		return direction;
	}

	public void setDirection(ParameterDirection direction) {
		this.direction = direction;
	}

	public DbType getDbType() {
		return dbType;
	}

	public void setDbType(DbType dbType) {
		this.dbType = dbType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return this.getName().toString();
	}

	@Override
	public int compareTo(CSharpParameterHost o) {
		return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
	}

}