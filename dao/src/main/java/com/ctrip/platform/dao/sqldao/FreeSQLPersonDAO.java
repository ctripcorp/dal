package com.ctrip.platform.dao.sqldao;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dao.common.AbstractDAO;
import com.ctrip.platform.dao.msg.AvailableType;

public class FreeSQLPersonDAO extends AbstractDAO {

	private static final Logger logger = LoggerFactory
			.getLogger(FreeSQLPersonDAO.class);

	public FreeSQLPersonDAO() {
	}

	/**
	 * Query Address Telephone according to the name and gender
	 * 
	 * @return The DAO function object to validate the parameter
	 */
	public ResultSet getAddrAndTel(AvailableType... params) throws Exception {

		final int[] inClauseParamIndex = new int[] { 2 };
		final String[] inClauseParamPlaceHolder = new String[inClauseParamIndex.length];

		Arrays.sort(inClauseParamIndex);

		for (int i = 0; i < inClauseParamIndex.length; i++) {
			int paramIndex = inClauseParamIndex[i];

			for (AvailableType param : params) {
				if (param.paramIndex == paramIndex) {
			
					int batchSize = Array.getLength(param.object_arg);

					
					StringBuilder inClause = new StringBuilder();

					for (int j = 0; j < batchSize; j++) {
						inClause.append('?');
						if(j != batchSize -1){
							inClause.append(',');
						}
					}

					if (inClause.length() > 0) {
						inClauseParamPlaceHolder[i] = String.format("(%s)",
								inClause.toString());
					}

					break;
				}
			}

		}

		final String sql = String
				.format("SELECT Address, Telephone FROM Person WHERE Name = ? AND Gender IN %s",
						(Object[])inClauseParamPlaceHolder);
		
//		String sql = "SELECT Address, Telephone FROM Person WHERE Name = ? AND Gender IN ?";
		
		

		return super.fetch(null, sql, 0, params);
	}

	public static void main(String[] args) {

		Object[] origial = new Object[] { 1 };
		int batchSize = 2;
		int realPassedInParamsLength = 1;

		if (batchSize > realPassedInParamsLength) {
			Object[] newArray = new Object[batchSize];
			System.arraycopy(origial, 0, newArray, 0, realPassedInParamsLength);
			for (Object obj : newArray) {
				System.out.println(obj.getClass());
				System.out.println(obj);
			}
		}
	}

}
