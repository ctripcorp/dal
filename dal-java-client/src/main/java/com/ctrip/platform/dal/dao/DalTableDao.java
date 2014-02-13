package com.ctrip.platform.dal.dao;

import java.sql.SQLException;
import java.util.List;


/**
 * 
 * @author jhhe
 */
public final class DalTableDao<T> {
	public static final String TMPL_SQL_FIND_BY_PK = "SELECT %s FROM %s WHERE %s";
	public static final String TMPL_SQL_INSERT = "INSERT INTO %s(%s) VALUES(%s)";
	public static final String TMPL_SQL_DELETE = "DELETE FROM %s WHERE %s";
	public static final String TMPL_SQL_UPDATE = "UPDATE %s SET %s WHERE %s";
	
	private static final String COLUMN_SEPARATOR = ", ";
	private static final String PLACE_HOLDER = "?";
	
	private DalClient client;
	private DalQueryDao queryDao;
	private DalParser<T> parser;

	public DalTableDao(DalParser<T> parser) {
		this.client = DalClientFactory.getClient(parser.getDatabaseName());
		this.parser = parser;
		queryDao = new DalQueryDao(parser.getDatabaseName());
		initSql();
	}
	
	public T queryByPk(Number id, DalHints hints)
			throws SQLException {
//		queryDao.q
		return null;
	}

	public T queryByPk(T pk, DalHints hints)
			throws SQLException {
//		queryDao.q
		return null;
	}
	
	public List<T> queryLike(T pk, DalHints hints)
			throws SQLException {
		return null;
	}
	
	public List<T> query(String whereClause, StatementParameters parameters, DalHints hints)
			throws SQLException {
		return null;
	}
	
	public T queryFirst(String whereClause, StatementParameters parameters, DalHints hints)
			throws SQLException {
		return null;
	}

	public T queryTop(String whereClause, StatementParameters parameters, DalHints hints, int count)
			throws SQLException {
		return null;
	}
	
	public T queryFrom(String whereClause, StatementParameters parameters, DalHints hints, int start, int count)
			throws SQLException {
		return null;
	}
	
	/**
	 * TODO Support auto incremental id.
	 * The generated id if any will be set into the pojos
	 * @param pojo
	 */
	public void insert(T...daoPojos) throws SQLException {
		// Assumption: all pojo will try to insert all columns, even the auto gen and nullable column
	}

	public void delete(T...daoPojos) throws SQLException {
		
	}
	
	public void update(T...daoPojos) throws SQLException {
		
	}
	
	public void delete(String whereClause, StatementParameters parameters, DalHints hints) throws SQLException {
		
	}
	
	public void update(String sql, StatementParameters parameters, DalHints hints) throws SQLException {
		
	}

	
	private String SQL_FIND_BY_PK;
	private String SQL_INSERT;
	private String SQL_DELETE;
	private String SQL_UPDATE;
	
	private void initSql() {
		SQL_FIND_BY_PK = "";
		SQL_INSERT = initInsertSql();
		SQL_DELETE = initDeleteSql();
		SQL_UPDATE = initUpdateSql();
	}
	
	private String initInsertSql() {
		StringBuilder cloumnsSb = new StringBuilder();
		StringBuilder valuesSb = new StringBuilder();
		
		int i = 0;
		for(String column: parser.getColumnNames()) {
			cloumnsSb.append(column);
			valuesSb.append(PLACE_HOLDER);
			if(++i < parser.getColumnNames().length){
				cloumnsSb.append(COLUMN_SEPARATOR);
				valuesSb.append(COLUMN_SEPARATOR);
			}
		}
		
		return String.format(TMPL_SQL_INSERT, parser.getTableName(), cloumnsSb.toString(), valuesSb.toString());
	}
	
	private String initDeleteSql() {
		StringBuilder cloumnsSb = new StringBuilder();
		StringBuilder valuesSb = new StringBuilder();
		
		int i = 0;
		for(String column: parser.getColumnNames()) {
			cloumnsSb.append(column);
			valuesSb.append(PLACE_HOLDER);
			if(++i < parser.getColumnNames().length){
				cloumnsSb.append(COLUMN_SEPARATOR);
				valuesSb.append(COLUMN_SEPARATOR);
			}
		}
		
		return String.format(TMPL_SQL_DELETE, parser.getTableName(), cloumnsSb.toString(), valuesSb.toString());
	}

	private String initUpdateSql() {
		StringBuilder cloumnsSb = new StringBuilder();
		StringBuilder valuesSb = new StringBuilder();
		
		int i = 0;
		for(String column: parser.getColumnNames()) {
			cloumnsSb.append(column);
			valuesSb.append(PLACE_HOLDER);
			if(++i < parser.getColumnNames().length){
				cloumnsSb.append(COLUMN_SEPARATOR);
				valuesSb.append(COLUMN_SEPARATOR);
			}
		}
		
		return String.format(TMPL_SQL_DELETE, parser.getTableName(), cloumnsSb.toString(), valuesSb.toString());
	}
}
