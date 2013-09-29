package com.ctrip.platform.bll;

import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.DAOFactory;
import com.ctrip.platform.dao.PersonDAO;
import com.ctrip.platform.dao.param.Parameter;
import com.ctrip.platform.dao.param.ParameterFactory;

public class BatchInsertBLL {

	public static void main(String[] args) throws Exception {
		
		List<Parameter> params = new ArrayList<Parameter>();

		for (int i = 0; i < 3; i++) {
			Parameter addrParam = ParameterFactory.createStringParameter(1,
					"gawu"+i);

			Parameter nameParam = ParameterFactory.createStringParameter(2,
					"shanghai"+i);
			
			params.add(ParameterFactory.createParameterList(addrParam, nameParam));
		}
		
		PersonDAO person = DAOFactory.createPersonDAO();
		
		
		person.setUseDBClient(false);
		
		Parameter[] resultParams = new Parameter[params.size()];
		
		int row = person.BatchInsertData(0, params.toArray(resultParams));
		
		System.out.println(row);
		
	}

}
