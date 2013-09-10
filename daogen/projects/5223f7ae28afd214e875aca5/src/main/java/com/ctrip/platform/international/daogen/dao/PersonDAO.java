package com.ctrip.platform.international.daogen.dao;

import java.sql.ResultSet;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.international.daogen.dao.common.AbstractDAO;
import com.ctrip.platform.international.daogen.dao.exception.ParametersInvalidException;
import com.ctrip.platform.international.daogen.dao.param.Parameter;

public class PersonDAO extends AbstractDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(PersonDAO.class);

	
	//None									
	public  int  delete(Parameter... params)
			throws Exception {

		
		final String sql = "DELETE FROM Person    WHERE  ID = ? AND Address = ? AND Name > ? ";
		

		return super.execute(null, sql, 0, params);
	}
	
	//None									
	public  ResultSet  get(Parameter... params)
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
				.format("SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person  WHERE  Age In %s ",
						(Object[]) placeHolder);
		

		return super.fetch(null, sql, 0, params);
	}
	
	//None									
	public  ResultSet  getAllByIDAddress(Parameter... params)
			throws Exception {

		
		final String sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person   WHERE  ID = ? AND Address != ? ";
		

		return super.fetch(null, sql, 0, params);
	}
	

	
	//None									
	public  int  insert(Parameter... params)
			throws Exception {
		

		final String spName = "spa_Person_i";
	

		
			return super.executeSp(null, spName, 0, params);
		
	}
	
	//None									
	public  int  set(Parameter... params)
			throws Exception {
		

		final String spName = "spa_Person_u";
	

		
			return super.executeSp(null, spName, 0, params);
		
	}
	

}
