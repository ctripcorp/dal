package com.ctrip.platform.dal.daogen.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.pojo.DbServer;

/**
 * 如果一个连接在一定时间内都没有访问，则将其关闭并移除, 移除的时机：有其他连接被访问时，或者其他连接加入Cache时，
 * 对性能可能有一定影响，但是可以消除僵尸连接带来的隐患
 * 
 * @author gawu
 * 
 */
public class DataSourceLRUCache {

	private DataSourceLRUCache() {

	}

	private static DataSourceLRUCache cache = new DataSourceLRUCache();
	private static DaoOfDbServer dbServerDao;

	static {
		dbServerDao = SpringBeanGetter.getDBServerDao();
	}

	// 默认30分钟
	private long timeout = 30 * 60 * 1000;

	public static DataSourceLRUCache newInstance() {
		return cache;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long _timeout) {
		synchronized (dataSources) {
			timeout = _timeout;
		}
	}

	private ConcurrentHashMap<Integer, InnerDataSource> dataSources = new ConcurrentHashMap<Integer, DataSourceLRUCache.InnerDataSource>();

	/**
	 * 根据server的id获取连接池，调用者需要判断是否为null的情况
	 * 
	 * @param id
	 * @return 获得的连接池，如果不存在，返回null
	 */
	public DataSource getDataSource(int id) {
		// 用synchronized，此处的性能堪忧，但是是线程安全的
		synchronized (dataSources) {
			long current = System.currentTimeMillis();

			Iterator<Map.Entry<Integer, InnerDataSource>> iter = dataSources
					.entrySet().iterator();

			while (iter.hasNext()) {
				Map.Entry<Integer, InnerDataSource> entry = iter.next();
				// 现在是在返回连接池之前回收，后续可以考虑放在后台线程中进行释放，类似于GC
				if (!entry.getKey().equals(id)
						&& (current - entry.getValue().getLastAccessTime()) > timeout) {
					try {
						// 关闭所有的Idle连接，也可以考虑直接关闭active的连接，暂不实现
						entry.getValue().dataSource.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					iter.remove();
				}
			}

			if (dataSources.containsKey(id)) {
				dataSources.get(id).setLastAccessTime(current);
				return dataSources.get(id).getDataSource();
			}

			return null;
		}

	}

	/**
	 * 根据数据库信息新建一个连接池，如果已存在，则直接返回，如果不存在，且新建失败，返回null
	 * 
	 * @param id
	 * @return
	 */
	public DataSource putDataSource(int id) {
		DbServer dbServer = dbServerDao.getDbServerByID(id);

		return putDataSource(dbServer);
	}

	/**
	 * 根据数据库信息新建一个连接池，如果已存在，则直接返回，如果不存在，且新建失败，返回null
	 * 
	 * @param server
	 * @return
	 */
	public DataSource putDataSource(DbServer server) {

		// get和put会不会出现死锁？
		synchronized (dataSources) {

			if (dataSources.containsKey(server.getId())) {
				dataSources.get(server.getId()).setLastAccessTime(
						System.currentTimeMillis());
				return dataSources.get(server.getId()).getDataSource();
			}

			BasicDataSource dbDataSource = new BasicDataSource();
			dbDataSource.setDriverClassName(server.getDriver());
			// dbDataSource.setUrl(server.getUrl());
			String url = "";
			if (server.getDb_type().equalsIgnoreCase("mysql")) {
				url = String.format("jdbc:mysql://%s:%d", server.getServer(),
						server.getPort());
			} else {
				if (null == server.getDomain() || server.getDomain().isEmpty())
					url = String.format("jdbc:jtds:sqlserver://%s:%d",
							server.getServer(), server.getPort());
				else
					url = String.format(
							"jdbc:jtds:sqlserver://%s:%d;domain=%s;",
							server.getServer(), server.getPort(),
							server.getDomain());
			}
			dbDataSource.setUrl(url);
			dbDataSource.setUsername(server.getUser());
			dbDataSource.setPassword(server.getPassword());

			try {
				dbDataSource.getConnection();

				InnerDataSource innerDataSource = new InnerDataSource();
				innerDataSource.setLastAccessTime(System.currentTimeMillis());
				innerDataSource.setDataSource(dbDataSource);

				dataSources.put(server.getId(), innerDataSource);

			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}

			return dbDataSource;
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

	public static void main(String[] args) {
		NamedParameterJdbcTemplate tmpl = new NamedParameterJdbcTemplate(
				DataSourceLRUCache.newInstance().putDataSource(5));

		String sql = "select id, driver, url, user,password, db_type from daogen.data_source WHERE id = :id";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("id", 5);

		List<DbServer> servers = tmpl.query(sql, parameters,
				new RowMapper<DbServer>() {

					@Override
					public DbServer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return DbServer.visitRow(rs);
					}

				});

		for (DbServer server : servers) {
			System.out.println(server.getDb_type());
		}

	}

}
