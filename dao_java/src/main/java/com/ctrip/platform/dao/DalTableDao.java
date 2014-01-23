package com.ctrip.platform.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * TODO support batch
 * @author jhhe
 *
 */
public class DalTableDao extends DalQueryDao {
	private static final String SQL_FIND_BY_PK = "SELECT * FROM %s WHERE %s";

	private DalTableParser pojoParser;

	public DalTableDao(DalClientFactory factory, DalTableParser pojoParser) {
		super(factory, pojoParser);
		this.pojoParser = pojoParser;
	}
	
	public DaoPojo selectByPk(DaoPojo pk, Map<DaoHintEnum, Object> hints)
			throws SQLException {
		return selectFisrt(SQL_FIND_BY_PK, pojoParser.getPk(pk),
				hints);
	}
	
	/**
	 * Support auto incremental id.
	 * @param pojo
	 */
	public void insert(DaoPojo...daoPojos) throws SQLException {
		
	}

	public void delete(DaoPojo...daoPojos) throws SQLException {
		
	}
	
	public void update(DaoPojo...daoPojos) throws SQLException {
		
	}
	
	public List<DaoCommand> createInsertCommand(DaoPojo...daoPojos) {
		return null;
	}
	
	public List<DaoCommand> createDeleteCommand(DaoPojo...daoPojos) {
		return null;
	}

	public List<DaoCommand> createUpdateCommand(DaoPojo...daoPojos) {
		return null;
	}
	
	public List<DaoCommand> createCommand(String sql, List<StatementParameter> parameters) {
		return null;
	}
	
	public void executeBatch(List<DaoCommand> commands, Map<DaoHintEnum, Object> hints) {
		// conn.setAutoCommit(false)
	}
	
	void test() throws SQLException {
		DaoPojo p = null;
		DaoPojo[] pl = null;
		
		insert();
		
		insert(p);
		
		insert(pl);
		
		insert(p, p, p);

		update(p);
		
		update(pl);
		
		update(p, p, p);

		delete(p);
		
		delete(pl);
		
		delete(p, p, p);
	}
	
}
