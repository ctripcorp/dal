package com.ctrip.platform.dal.ext.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.ext.parser.DalDefaultJpaParser;

public class DalDefaultJpaMapper {
	
	private static final ConcurrentHashMap<String, DalRowMapper<?>> cache = new ConcurrentHashMap<String, DalRowMapper<?>>();

	@SuppressWarnings("unchecked")
	public static <T> DalRowMapper<T> create(Class<T> clazz, String databaseName) throws SQLException {
		if (!cache.contains(clazz.getName())) {
			synchronized (DalDefaultJpaMapper.class) {
				if (!cache.contains(clazz.getName())) {
					createAndCacheMapper(clazz, databaseName);
				}
			}
		}
		return (DalRowMapper<T>) cache.get(clazz.getName());
	}
	
	private static <T> void createAndCacheMapper(Class<T> clazz, String databaseName) throws SQLException {
		final DalParser<T> parser = DalDefaultJpaParser.create(clazz, databaseName);
		DalRowMapper<T> mapper = new DalRowMapper<T>() {
			@Override
			public T map(ResultSet rs, int rowNum) throws SQLException {
				return parser.map(rs, rowNum);
			}
		};
		cache.put(clazz.getName(), mapper);
	}
}
