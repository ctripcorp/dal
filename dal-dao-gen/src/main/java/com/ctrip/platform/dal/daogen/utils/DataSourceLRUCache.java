package com.ctrip.platform.dal.daogen.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.ctrip.platform.dal.daogen.dao.DbServerDAO;
import com.ctrip.platform.dal.daogen.pojo.DbServer;

/**
 * 如果一个连接在一定时间内都没有访问，则将其关闭并移除, 触发时间：有其他连接被访问，或者其他连接加入Cache，对性能可能有一定影响，但是
 * 可以消除僵尸连接带来的隐患
 * 
 * @author gawu
 * 
 */
public class DataSourceLRUCache {

	private DataSourceLRUCache() {

	}

	private static DataSourceLRUCache cache = new DataSourceLRUCache();
	private static DbServerDAO dataSourceDao;

	static {
		dataSourceDao = BeanGetter.getDBServerDao();
	}

	public static DataSourceLRUCache newInstance() {
		return cache;
	}

	private ConcurrentHashMap<Integer, InnerDataSource> dataSources = new ConcurrentHashMap<Integer, DataSourceLRUCache.InnerDataSource>();

	public DataSource getDataSource(int id) {
		long current = System.currentTimeMillis();

		Iterator<Map.Entry<Integer, InnerDataSource>> iter = dataSources
				.entrySet().iterator();

		synchronized (dataSources) {
			while (iter.hasNext()) {
				Map.Entry<Integer, InnerDataSource> entry = iter.next();
				// 现在写死为30分钟
				if (!entry.getKey().equals(id)
						&& (current - entry.getValue().getLastAccessTime()) > 30 * 60 * 1000) {
					iter.remove();
				}
			}

			if (dataSources.containsKey(id)) {
				dataSources.get(id).setLastAccessTime(current);
				return dataSources.get(id).getDataSource();
			} else {
				DbServer dataSource = dataSourceDao.getDbServerByID(id);
				if (null != dataSource) {
					InnerDataSource innerDataSource = new InnerDataSource();
					innerDataSource.setLastAccessTime(current);

					BasicDataSource dbDataSource = new BasicDataSource();
					dbDataSource.setDriverClassName(dataSource.getDriver());
					dbDataSource.setUrl(dataSource.getUrl());
					dbDataSource.setUsername(dataSource.getUser());
					dbDataSource.setPassword(dataSource.getPassword());

					try {
						dbDataSource.getConnection();
					} catch (SQLException e) {
						e.printStackTrace();
						return null;
					}

					innerDataSource.setDataSource(dbDataSource);

					dataSources.put(id, innerDataSource);
					return dbDataSource;
				}
				return null;
			}
		}

	}

	/**
	 * 内部类，for map use
	 * 
	 * @author gawu
	 * 
	 */
	class InnerDataSource {
		private long lastAccessTime;
		private BasicDataSource dataSource;

		public long getLastAccessTime() {
			return lastAccessTime;
		}

		public void setLastAccessTime(long lastAccessTime) {
			this.lastAccessTime = lastAccessTime;
		}

		public BasicDataSource getDataSource() {
			return dataSource;
		}

		public void setDataSource(BasicDataSource dataSource) {
			this.dataSource = dataSource;
		}
	}

	public static void main(String[] args) throws SQLException {
		DataSource ds = DataSourceLRUCache.newInstance().getDataSource(6);
		Connection connection = ds.getConnection();

		// ResultSet rs = connection.getMetaData().getColumns("dao_test", null,
		// "Person", null);
		//
		// while(rs.next()){
		// System.out.println(rs.getString("COLUMN_NAME"));
		// }
		//
		// rs.close();

		ResultSet rs = connection.getMetaData().getIndexInfo("SysDalTest", null,
				"Person", false, false);

		while (rs.next()) {
			System.out.println(rs.getString("COLUMN_NAME"));
		}

		rs.close();
	}

}
