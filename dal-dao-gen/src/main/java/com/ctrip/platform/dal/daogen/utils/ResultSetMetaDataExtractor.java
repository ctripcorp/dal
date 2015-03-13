package com.ctrip.platform.dal.daogen.utils;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public interface ResultSetMetaDataExtractor<T> {
	T extract(ResultSetMetaData rsmd) throws SQLException;
}