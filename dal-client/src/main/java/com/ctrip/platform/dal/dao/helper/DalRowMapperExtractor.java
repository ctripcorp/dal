package com.ctrip.platform.dal.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;

public class DalRowMapperExtractor <T> implements DalResultSetExtractor<List<T>>, HintsAwareExtractor<List<T>> {
	private static final int COUNT_LIMIT = 5000000;

	private DalRowMapper<T> mapper;
	private int start;
	private int count;
	private DalHints hints;
	
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
		List<T> result = count == 0 ? new ArrayList<T>() : new ArrayList<T>(Math.min(count, COUNT_LIMIT));
		if(start != 0)
			rs.absolute(start);
		int i = 0;
		int rowNum = 0;
		
		checkHints(rs);
		
		while ((i++ < count || count == 0) && rs.next()) {
			result.add(mapper.map(rs, rowNum++));
		}
		return result;
	}
	
	private void checkHints(ResultSet rs) throws SQLException {
	    if(hints != null && mapper instanceof CustomizableMapper) {
	        mapper = ((CustomizableMapper)mapper).mapWith(rs, hints);
	    }
	}

    @Override
    public DalResultSetExtractor<List<T>> extractWith(DalHints hints) throws SQLException {
        DalRowMapperExtractor<T> customized = new DalRowMapperExtractor<>(this.mapper, this.start, this.count);
        customized.hints = hints;
        return customized;
    }
}