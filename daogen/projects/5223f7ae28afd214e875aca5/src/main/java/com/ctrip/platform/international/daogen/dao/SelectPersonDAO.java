package com.ctrip.platform.international.daogen.dao;

import java.sql.ResultSet;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.international.daogen.dao.common.AbstractDAO;
import com.ctrip.platform.international.daogen.dao.exception.ParametersInvalidException;
import com.ctrip.platform.international.daogen.dao.param.Parameter;

public class SelectPersonDAO extends AbstractDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(SelectPersonDAO.class);

	
	//None									
	public ResultSet getByNameGender(Parameter... params)
			throws Exception {

		
		Arrays.sort(params);

		String[] placeHolder = new String[params.length];

		for (int i = 0; i < params.length; i++) {
			//First set the place holder to just one parameter
			placeHolder[i] = "?";
			
			//if and only if user pass in valid parameter, we
			//format the place holder for him
			if (params[i].getValue().isArrayValue()) {
				int batchSize = params[i].getValue().asArrayValue().size();

				if (batchSize > 0) {
					StringBuilder inClause = new StringBuilder();
					inClause.append('(');
					for (int j = 0; j < batchSize; j++) {
						inClause.append('?');
						if (j != batchSize - 1) {
							inClause.append(',');
						}
					}
					inClause.append(')');
					placeHolder[i] = inClause.toString();
				}
			}
			
		}
		final String sql = String
				.format("SELECT Address, Telephone FROM Person  WHERE Name = %s  AND Gender IN %s",
						(Object[]) placeHolder);
		

		return super.fetch(null, sql, 0, params);
	}
	

}
