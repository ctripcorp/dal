package com.ctrip.platform.dal.daogen.host.java;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

public class JavaSpParamResultSetExtractor implements ResultSetExtractor<List<AbstractParameterHost>> {

	private static Logger log = Logger.getLogger(JavaSpParamResultSetExtractor.class);
	
	private String allInOneName;
	private String spName;
	
	public JavaSpParamResultSetExtractor(String allInOneName, String spName) {
		super();
		this.allInOneName = allInOneName;
		this.spName = spName;
	}

	@Override
	public List<AbstractParameterHost> extractData(ResultSet rs) throws SQLException {
		List<AbstractParameterHost> parameters = new ArrayList<AbstractParameterHost>();
		while (rs.next()) {
			int paramMode = rs.getShort("COLUMN_TYPE");
			// For My Sql, there is no ORDINAL_POSITION
			// int paramIndex = spParams.getInt("ORDINAL_POSITION");
			if (!DbUtils.validMode.contains(paramMode)) {
				continue;
			}

			JavaParameterHost host = new JavaParameterHost();
			// host.setIndex(paramIndex);
			host.setSqlType(rs.getInt("DATA_TYPE"));

			if (paramMode == DatabaseMetaData.procedureColumnIn) {
				host.setDirection(ParameterDirection.Input);
			} else if (paramMode == DatabaseMetaData.procedureColumnInOut) {
				host.setDirection(ParameterDirection.InputOutput);
			} else {
				host.setDirection(ParameterDirection.Output);
			}

			host.setName(rs.getString("COLUMN_NAME").replace("@",""));
			Class<?> javaClass = Consts.jdbcSqlTypeToJavaClass.get(host.getSqlType());
			if (null == javaClass) {
				if (-153 == host.getSqlType()) {
					log.error(String.format("The Table-Valued Parameters is not support for JDBC. [%s, %s]", allInOneName, spName));
				} else {
					log.fatal(String.format("The java type cant be mapped.[%s, %s, %s, %s, %s]", 
							host.getName(), allInOneName, spName, host.getSqlType(), javaClass));
				}
				return null;
			}
			host.setJavaClass(javaClass);
			parameters.add(host);
		}
		return parameters;
	}

}
