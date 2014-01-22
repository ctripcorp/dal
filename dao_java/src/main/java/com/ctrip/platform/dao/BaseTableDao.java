package com.ctrip.platform.dao;

import java.sql.ResultSet;
import java.util.List;

/**
 * TODO support batch
 * @author jhhe
 *
 */
public class BaseTableDao extends BaseQueryDao {
	public DaoPojo selectByPk(DaoPojo pojo) {
		return null;
	}
	
	/**
	 * Support auto incremental id.
	 * @param pojo
	 */
	public void insert(DaoPojo...daoPojos) {
		
	}

	public void delete(DaoPojo...daoPojos) {
		
	}
	
	public void update(DaoPojo...daoPojos) {
		
	}
	
	void test() {
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
