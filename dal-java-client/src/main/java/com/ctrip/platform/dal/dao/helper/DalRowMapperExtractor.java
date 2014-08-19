package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;

public class DalRowMapperExtractor <T> implements DalResultSetExtractor<List<T>> {
	private DalRowMapper<T> mapper;
	private int start;
	private int count;
	
	// Select all
	public DalRowMapperExtractor(DalRowMapper<T> mapper) {
		this(mapper, 0, 0);
	}
	
	// Select top
	public DalRowMapperExtractor(DalRowMapper<T> mapper, int count) {
		this(mapper, 0, count);
	}
	
	// Select from to
	public DalRowMapperExtractor(DalRowMapper<T> mapper, int start, int count) {
		this.mapper = mapper;
		this.start = start;
		this.count = count;
	}

	@Override
	public List<T> extract(ResultSet rs) throws SQLException {
		List<T> result = count == 0 ? new ArrayList<T>() : new ArrayList<T>(count);
		if(start != 0)
			rs.absolute(start);
		int i = 0;
		int rowNum = 0;
		while ((i++ < count || count == 0) && rs.next()) {
			result.add(mapper.map(rs, rowNum++));
		}
		return result;
	}
}