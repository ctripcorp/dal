package com.ctrip.platform.dao.client;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dao.DasProto;
import com.ctrip.platform.dao.DasProto.CRUD;
import com.ctrip.platform.dao.DasProto.StatementType;
import com.ctrip.platform.dao.param.StatementParameter;

public class DasClient implements Client {

	private SocketPool pool;

	public void init() {
		pool = new SocketPool("192.168.83.132", 9000);
	}

	@Override
	public ResultSet fetch(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {

		boolean master = false;
		
		if(keywordParameters.size() > 0 && keywordParameters.containsKey("master")){
			master = Boolean.getBoolean(keywordParameters.get("master").toString());
		}
		
		DasProto.RequestMessage.Builder msgBuilder = DasProto.RequestMessage
				.newBuilder();

		msgBuilder.setStateType(StatementType.SQL).setCrud(CRUD.GET)
				.setFlags(1).setMaster(master).setName(sql);
		
		for(StatementParameter p : parameters){
			msgBuilder.addParameters(p.build2SqlParameters());
		}
		
		DasProto.Request.Builder requestBuilder = DasProto.Request.newBuilder();
		
		requestBuilder.setMsg(msgBuilder.build()).setCred("").setDb("").setId("");

		return null;
	}

	@Override
	public int execute(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ResultSet fetchBySp(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int executeSp(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		// TODO Auto-generated method stub
		return 0;
	}

}
