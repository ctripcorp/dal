package com.dal.sqlserver.test.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.dal.sqlserver.test.People;
import com.dal.sqlserver.test.PeopleDao;
import com.xross.tools.xunit.Context;

public class WebContext implements Context {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PeopleDao dao;
	private Object responsValue;
	private boolean supported = true;
	
	public WebContext(HttpServletRequest request, HttpServletResponse response, PeopleDao dao)
	{
		this.request = request;
		this.response = response;
		this.dao = dao;
	}
	
	public boolean isSupported() {
		return supported;
	}
	public void setSupported(boolean supported) {
		this.supported = supported;
	}
	public void handle(Exception e) {
		throw new RuntimeException(e);
	}
	public PeopleDao getDao() {
		return dao;
	}
	public void setDao(PeopleDao dao) {
		this.dao = dao;
	}
	public Object getResponsValue() {
		return responsValue;
	}
	public void setResponsValue(Object responsValue) {
		this.responsValue = responsValue;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public String getAction() {
		return get("action");
	}
	
	public People readPeople() {
		People p = new People();
		
		p.setPeopleID(getLong("PeopleID"));
		p.setName(get("Name"));
		p.setCityID(getInt("CityID"));
		p.setProvinceID(getInt("ProvinceID"));
		p.setCountryID(getInt("CountryID"));
		
		return p;
	}

	public List<People> readPeopleList() {
		List<People> pList = new ArrayList<>();
		
		for(int i = 0; i < 3; i++) {
			People p = new People();
			
			p.setPeopleID(getLong("PeopleID_" + i));
			p.setName(get("Name_" + i));
			p.setCityID(getInt("CityID_" + i));
			p.setProvinceID(getInt("ProvinceID_" + i));
			p.setCountryID(getInt("CountryID_" + i));
			pList.add(p);
		}
		
		return pList;
	}
	
	public DalHints readHints() {
		DalHints hints = new DalHints();

		String a = request.getParameter("enableKeyHolder");
		if("on".equals(a))
			hints.setKeyHolder(new KeyHolder());

		String value = get("shardConstrain");
		if("inShard".equals(value)) {
			value = get("Shard");
			if(value != null)
				hints.inShard(value);
		} else if("inAllShards".equals(value)){
			hints.inAllShards();
		} else if("inShards".equals(value)){
			Set<String> shards = new HashSet<>();
			value = get("Shards");
			shards.addAll(Arrays.asList(value.split(",")));
			hints.inShards(shards);
		}
		
		value = get("invocationMode");
		if("asynchronours".equals(value))
			hints.asyncExecution();
		else if("callback".equals(value))
			hints.callbackWith(new DefaultResultCallback());
		
		return hints;
	}
	
	public String get(String name) {
		String value = request.getParameter(name);
		return value == null || value.length() == 0 ? null : value;
	}

	public Integer getInt(String name) {
		String value = request.getParameter(name);
		return value == null || value.length() == 0 ? null : Integer.parseInt(value);
	}

	public Long getLong(String name) {
		String value = request.getParameter(name);
		return value == null || value.length() == 0 ? null : Long.parseLong(value);
	}
}
