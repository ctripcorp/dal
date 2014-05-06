package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.dao.DalParser;

/**
 * The T type must contain non-parameters constructor
 * TODO: The default primary key has only one
 * @author wcyuan
 * @param <T> The JPA Entity
 */
public class DalDefaultJpaParser<T> implements DalParser<T> {
	private static ConcurrentHashMap<String, DalParser<?>> cache = null;
	
	private String databaseName;
	private String tableName;
	private String[] fieldNames;
	private String[] columns;
	private boolean[] nullables;
	private int[] columnTypes;
	private String[] primaryKeys; 
	private Object[] defaultVals;
	private Class<T> clazz;
	private Field identity;
	private Field[] originFileds;
	private boolean autoIncrement;
	private Loader loader;
	
	static{
		cache = new ConcurrentHashMap<String, DalParser<?>>();
	}
	
	private DalDefaultJpaParser(Class<T> clazz, Loader loader, String databaseName){
		this.clazz = clazz;
		this.loader = loader;
		this.databaseName = databaseName;
		Entity entityAnnot = this.clazz.getAnnotation(Entity.class);
		if(null != entityAnnot){
			this.tableName = entityAnnot.name();
			Field[] fields = this.clazz.getDeclaredFields();
			if(null == fields)
				return;
			this.fieldNames = new String[fields.length];
			this.columns = new String[fields.length];
			this.columnTypes = new int[fields.length];
			this.originFileds = new Field[fields.length];
			this.defaultVals = new Object[fields.length];
			this.nullables = new boolean[fields.length];
			
			List<String> ids = new ArrayList<String>();
			for(int i = 0 ; i< fields.length; i++){
				Field field = fields[i];
				field.setAccessible(true);
				this.originFileds[i] = field;
				this.defaultVals[i] = null;
				
				GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
				if(null != generatedValue && 
						(generatedValue.strategy() == GenerationType.AUTO ||
						generatedValue.strategy() == GenerationType.IDENTITY)){
					this.autoIncrement = true;
				}
				
				Id id = field.getAnnotation(Id.class);
				if(null != id){
					this.identity = field;
					ids.add(field.getName());
				}
				
				Column column = field.getAnnotation(Column.class);
				Basic basic = field.getAnnotation(Basic.class);
				if(null == column && null != id){
					this.columns[i] = field.getName();
					this.columnTypes[i] = this.loader.getSqlType(field.getType());
					this.nullables[i] = false;
					this.fieldNames[i] = field.getName();
					continue;
				}
				if(null != column){
					columns[i] = column.name().isEmpty() ? field.getName() : column.name();
					//TODO: Resolve column type and default value from columnDefinition
					this.columnTypes[i] = this.loader.getSqlType(field.getType());
					this.nullables[i] = column.nullable();
					this.fieldNames[i] = field.getName();
				}else if(null != basic){
					columns[i] = field.getName();
					this.columnTypes[i] = this.loader.getSqlType(field.getType());
					this.nullables[i] = true;
					this.fieldNames[i] = field.getName();
				}
			}
			
			this.primaryKeys = new String[ids.size()];
			ids.toArray(this.primaryKeys);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> DalParser<T> create(Class<T> clazz,  Loader loader, String databaseName) {
		if(!cache.contains(clazz.getName())){
			cache.put(clazz.getName(), new DalDefaultJpaParser<T>(clazz, loader, databaseName));
		}
		return (DalParser<T>)cache.get(clazz.getName());
	}
	
	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		try {
			T instance  = this.clazz.newInstance();
			//Fill fields here
			for(int i = 0; i < fieldNames.length; i++){
				Field field = this.originFileds[i];
				field.set(instance, this.loader.load(field, 
						rs.getObject(this.columns[i])));
			}
			return instance;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	public String getDatabaseName() {
		return this.databaseName;
	}

	@Override
	public String getTableName() {
		return this.tableName;
	}

	@Override
	public String[] getColumnNames() {
		return this.columns;
	}

	@Override
	public String[] getPrimaryKeyNames() {
		return this.primaryKeys;
	}

	@Override
	public int[] getColumnTypes() {
		return this.columnTypes;
	}

	@Override
	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	@Override
	public Number getIdentityValue(T pojo) {
		if(pojo.getClass().equals(this.clazz)){
			try {
				Object val = this.identity.get(pojo);
				if(val instanceof Number)
					return (Number) val;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}	
		}
		return null;
	}

	@Override
	public Map<String, ?> getPrimaryKeys(T pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		Object id = this.getIdentityValue(pojo);
		if(id != null)
			map.put(primaryKeys[0], id);
		return map;
	}

	@Override
	public Map<String, ?> getFields(T pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for(int i = 0; i < this.originFileds.length; i++){
			try {
				if(this.autoIncrement && originFileds[i].equals(this.identity)){
					map.put(this.columns[i], null);
					continue;
				}
				Object val = this.loader.save(originFileds[i], pojo, nullables[i]);
				map.put(this.columns[i], val);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
	
	
	public static void main(String[] args){
		DalParser<Person> parser = 
				DalDefaultJpaParser.create(Person.class, new DefaultLoader(), "dao_test");
		Person person = new Person();
		person.setID(1);
		person.setName("cc");
		person.setAddress("ctrip");
		person.setAge(null);
		person.setGender(-1);
		person.setTelephone(null);
		person.setBirth(new Timestamp(System.currentTimeMillis()));
		
		System.out.println("databaseName: " + parser.getDatabaseName());
		System.out.println("tableName:" + parser.getTableName());
		System.out.println("identity: " + parser.getIdentityValue(person));
		
		System.out.println("columnNames: " + StringUtils.join(parser.getColumnNames(), ":"));
		for(int t : parser.getColumnTypes()){
			System.out.print(t + ":");
		}
		System.out.print("\r\n");
		
		System.out.println("isAutoIncrement" + parser.isAutoIncrement());
		System.out.println("primaryKeyNames" + StringUtils.join(parser.getPrimaryKeyNames(), ":"));
		
		System.out.print("fields: ");
		for(Map.Entry<String, ?> en : parser.getFields(person).entrySet()){
			System.out.print(String.format("[key=%s,value=%s],", en.getKey(), en.getValue()));
		}
		System.out.print("\r\nprimaryKeys: ");
		for(Map.Entry<String, ?> en : parser.getPrimaryKeys(person).entrySet()){
			System.out.print(String.format("[key=%s,value=%s],", en.getKey(), en.getValue()));
		}
	}
}
