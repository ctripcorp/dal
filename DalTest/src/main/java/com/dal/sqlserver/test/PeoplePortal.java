package com.dal.sqlserver.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalColumnMapRowMapper;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.ctrip.platform.dal.sql.logging.CommonUtil;

/**
 * Servlet implementation class PeoplePortal
 * This the old traditional way to build a servlet based application. Just keep it here for your reference and comparing.
 * Please refer to XunitPeoplePortal for build app with xunit
 */
@WebServlet("/PeoplePortal")
public class PeoplePortal extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PeopleDao dao;
			
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PeoplePortal() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		try {
			dao = new PeopleDao();
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = get(request, "action");
		
		if(action == null) return;
		
//		testSelectSP(response);
		response.setContentType("text/html;charset=UTF-8");
		try {
			if("insertSingle".equals(action))
				insertSingle(request, response);
			else if("insertMultiple".equals(action))
				insertMultiple(request, response);
			else if("batchInsert".equals(action))
				batchInsert(request, response);
			else if("deleteSingle".equals(action))
				deleteSingle(request, response);
			else if("deleteMultiple".equals(action))
				deleteMultiple(request, response);
			else if("batchDelete".equals(action))
				batchDelete(request, response);
			else if("updateSingle".equals(action))
				updateSingle(request, response);
			else if("updateMultiple".equals(action))
				updateMultiple(request, response);
			else if("batchUpdate".equals(action))
				batchUpdate(request, response);
			else if("getAll".equals(action))
				getAll(request, response);
			else if("deleteAll".equals(action))
				deleteAll(request, response);
			else if("queryByPk".equals(action))
				queryByPk(request, response);
			else if("queryByPage".equals(action))
				queryByPage(request, response);
			else if("count".equals(action))
				count(request, response);
			else if("findPeople".equals(action))
				findPeople(request, response);
			else if("insertPeople".equals(action))
				insertPeople(request, response);
			else if("deletePeople".equals(action))
				deletePeople(request, response);
			else if("updatePeople".equals(action))
				updatePeople(request, response);
			else if("timeout".equals(action))
				timeout(request, response);
			else if("nontimeout".equals(action))
				nontimeout(request, response);
			else if("decrypt".equals(action))
				decrypt(request, response);
			else if("checkConnection".equals(action))
				checkConnection(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		response.flushBuffer();
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private void insertSingle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int num = dao.insert(hints, readPeople(request));
		buildResponse(num, hints, response);
	}
	
	private void insertMultiple(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int[] num = dao.insert(hints, readPeopleList(request));
		buildResponse(num, hints, response);
	}
	
	private void batchInsert(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int[] num = dao.batchInsert(hints, readPeopleList(request));
		buildResponse(num, hints, response);
	}
	
	private void deleteSingle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int num = dao.delete(hints , readPeople(request));
		buildResponse(num, hints, response);
	}
	
	private void deleteMultiple	(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int[] num = dao.delete(hints, readPeopleList(request));
		buildResponse(num, hints, response);
	}
	
	private void batchDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int[] num = dao.batchDelete(hints, readPeopleList(request));
		buildResponse(num, hints, response);
	}
	
	private void updateSingle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int num = dao.update(hints , readPeople(request));
		buildResponse(num, hints, response);
	}

	private void updateMultiple(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int[] num = dao.update(hints, readPeopleList(request));
		buildResponse(num, hints, response);
	}

	private void batchUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int[] num = dao.batchUpdate(hints, readPeopleList(request));
		buildResponse(num, hints, response);
	}

	private void queryByPk(HttpServletRequest request, HttpServletResponse response) throws Exception {
		People p = readPeople(request);
		DalHints hints = readHints(request);
		p = dao.queryByPk(p, hints);
		buildResponse(p, hints, response);
	}

	private void getAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		List<People> pList = dao.getAll(hints);
		buildResponse(pList, hints, response);
	}
	
	private void deleteAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		List<People> pList = dao.getAll(hints);
		int[] num = dao.delete(hints, pList);
		buildResponse(num, hints, response);
	}
	
	private void queryByPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int pageSize = getInt(request, "pageSize"); 
		int pageNo = getInt(request, "pageNo");
		List<People> pList = dao.queryByPage(pageSize, pageNo, hints);
		buildResponse(pList, hints, response);
	}

	private void count(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int num = dao.count(hints);
		buildResponse(num, hints, response);
	}

	private void findPeople(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request);
		int pageSize = getInt(request, "pageSize"); 
		int pageNo = getInt(request, "pageNo");
		String name = get(request, "name");
		List<People> pList = dao.findPeople(name, pageNo, pageSize, hints);
		buildResponse(pList, hints, response);
	}

	private void insertPeople(HttpServletRequest request, HttpServletResponse response) throws Exception {
		buildNotSupportedResponse(response);
	}
	
	private void deletePeople(HttpServletRequest request, HttpServletResponse response) throws Exception {
		buildNotSupportedResponse(response);
	}
	
	private void updatePeople(HttpServletRequest request, HttpServletResponse response) throws Exception {
		buildNotSupportedResponse(response);
	}

	private void timeout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request).timeout(1);
		DalClient client = DalClientFactory.getClient("MultiThreadingTest");
		List<Map<String, Object>> l = client.query("SELECT * FROM dal_client_test_big WHERE address like '%TIMEOUT%' ORDER BY address", new StatementParameters(), hints, new DalRowMapperExtractor<>(new DalColumnMapRowMapper()));
		buildResponse(l, hints, response);
	}
	
	private void nontimeout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DalHints hints = readHints(request).timeout(1);
		DalClient client = DalClientFactory.getClient("MultiThreadingTest");
		List<Map<String, Object>> l = client.query("SELECT TOP 10 * FROM dal_client_test_big", new StatementParameters(), hints, new DalRowMapperExtractor<>(new DalColumnMapRowMapper()));
		buildResponse(l, hints, response);
	}
	
	private void decrypt(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String rawValue = get(request, "value");
		String value = CommonUtil.desDecrypt(rawValue);
		PrintWriter writer = response.getWriter();
		if(rawValue.equals(value))
			writer.write("Decrypt faild for:<br/>" + value);
		else
			writer.write("Original Content is:<br/>" + value);
	}
	
	private void checkConnection(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String keyName = get(request, "keyName");
		String fws = "https://ws.titan.fws.qa.nt.ctripcorp.com/titanservice/query";

		DalDataSourceFactory dl = new DalDataSourceFactory();
		PrintWriter writer = response.getWriter();
		try {
			Connection conn = dl.createDataSource(keyName, fws, "12233").getConnection();
			conn.close();
			writer.write("Connect to: " + keyName + " success");
		} catch (Exception e) {
			writer.write("Error getting connection for: " + keyName + "<BR/>");
			e.printStackTrace(writer);
		}
	}
	
	private void writePeopleList(PrintWriter writer, List<Object> pList)
			throws IOException {
		writer.write("<p>Count: " + pList.size());
		writer.write("<table border=\"1\"><tr><th>PeopleID</th><th>Name</th><th>CityID</th><th>ProvinceID</th><th>CountryID</th></tr>");
		for(Object o: pList) {
			People p = (People)o;
			writer.write("<tr><td>" + p.getPeopleID() + "</td>");
			writer.write("<td>" + p.getName() + "</td>");
			writer.write("<td>" + p.getCityID() + "</td>");
			writer.write("<td>" + p.getProvinceID() + "</td>");
			writer.write("<td>" + p.getCountryID() + "</td></tr>");
		}
		writer.write("</table>");
	}
	
	private void writePeople(PrintWriter writer, People p)
			throws IOException {
		writer.write("<br/>PeopleID: " + p.getPeopleID());
		writer.write("\nName: " + p.getName());
		writer.write("\nCityID: " + p.getCityID());
		writer.write("\nProvinceID: " + p.getProvinceID());
		writer.write("\nCountryID: " + p.getCountryID());
	}
	
	private People readPeople(HttpServletRequest request) {
		People p = new People();
		
		p.setPeopleID(getLong(request, "PeopleID"));
		p.setName(get(request, "Name"));
		p.setCityID(getInt(request, "CityID"));
		p.setProvinceID(getInt(request, "ProvinceID"));
		p.setCountryID(getInt(request, "CountryID"));
		
		return p;
	}

	private List<People> readPeopleList(HttpServletRequest request) {
		List<People> pList = new ArrayList<>();
		
		for(int i = 0; i < 3; i++) {
			People p = new People();
			
			p.setPeopleID(getLong(request, "PeopleID_" + i));
			p.setName(get(request, "Name_" + i));
			p.setCityID(getInt(request, "CityID_" + i));
			p.setProvinceID(getInt(request, "ProvinceID_" + i));
			p.setCountryID(getInt(request, "CountryID_" + i));
			pList.add(p);
		}
		
		return pList;
	}
	
	private DalHints readHints(HttpServletRequest request) {
		DalHints hints = new DalHints();

		String a = request.getParameter("enableKeyHolder");
		if("on".equals(a))
			hints.setKeyHolder(new KeyHolder());

		String value = get(request, "shardConstrain");
		if("inShard".equals(value)) {
			value = get(request, "Shard");
			if(value != null)
				hints.inShard(value);
		} else if("inAllShards".equals(value)){
			hints.inAllShards();
		} else if("inShards".equals(value)){
			Set<String> shards = new HashSet<>();
			value = get(request, "Shards");
			shards.addAll(Arrays.asList(value.split(",")));
			hints.inShards(shards);
		}
		
		value = get(request, "invocationMode");
		if("asynchronours".equals(value))
			hints.asyncExecution();
		else if("callback".equals(value))
			hints.callbackWith(new DefaultResultCallback());
		
		return hints;
	}
	
	private String get(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		return value == null || value.length() == 0 ? null : value;
	}

	private Integer getInt(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		return value == null || value.length() == 0 ? null : Integer.parseInt(value);
	}

	private Long getLong(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		return value == null || value.length() == 0 ? null : Long.parseLong(value);
	}

	private void buildNotSupportedResponse(HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		writer.write("Not supported in Sql Server");
	}
	
	private void buildResponse(Object returnValue, DalHints hints, HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		writeDirectResult(returnValue, writer);
		writeAsyncResult(hints, writer);
		writeCallbackResult(hints, writer);
		writeKeyHolder(hints, writer);		
	}

	private void writeDirectResult(Object returnValue, PrintWriter writer)
			throws Exception {
		writer.write("Direct result:<br/>");
		if(returnValue == null) {
			writer.write("The result null");
		} else {
			writeResult(returnValue, writer);
		}
	}
	
	private void writeAsyncResult(DalHints hints, PrintWriter writer) {
		if(hints.isAsyncExecution()) {
			writer.write("<br/>Async Execution result:<br/>");
			
			try {
				writeResult(hints.getAsyncResult().get(), writer);
			} catch (Exception e) {
				writer.write("<br/>Error during execution" + e + "<br/>");
				e.printStackTrace();
			}
		}
	}

	private void writeResult(Object returnValue, PrintWriter writer) throws Exception {
		if(returnValue instanceof Number)
			writer.write(returnValue.toString());
		else if(returnValue instanceof int[]){
			int[] values = (int[])returnValue;
			for(int i: values)
				writer.write(String.valueOf(i));
		}
		else if(returnValue instanceof People){
			writePeople(writer, (People)returnValue);
		}
		else if(returnValue instanceof List) {
			List<Object> l = (List)returnValue;
			if(l.size() > 0 && l.get(0) instanceof People) {
				writePeopleList(writer, l);
			} else {
				for(Object o: l) {
					writer.write(o.toString());
				}
			}
		}
	}
	
	private void writeKeyHolder(DalHints hints, PrintWriter writer)
			throws InterruptedException, SQLException {
		KeyHolder keyHolder = hints.getKeyHolder();
		if(keyHolder != null) {
			if(keyHolder.isRequireMerge()) {
				keyHolder.waitForMerge(1000);
				if(keyHolder.isMerged() == false)
					throw new SQLException("wait for merge timeout. maybe there is error.");
			}
			
			if(keyHolder.size() == 1)
				writer.write("<br/>Generated key: " + keyHolder.getKey() + "<br/>");
			else {
				writer.write("<br/>Generated keys:<br/>");
				for(Number i: keyHolder.getIdList())
					writer.write(i + "; ");
			}
		}
	}

	private void writeCallbackResult(DalHints hints, PrintWriter writer)
			throws InterruptedException, Exception {
		DefaultResultCallback callback = (DefaultResultCallback)hints.get(DalHintEnum.resultCallback);
		if(callback != null) {
			writer.write("<br/>Result Callback is used<br/>");
			callback.waitForDone();
			if(callback.getError() != null)
				writer.write("<br/>There is error: " + callback.getError() + "<br/>");
			else{
				writer.write("<br/>The result is:<br/>");
				writeResult(callback.getResult(), writer);
			}
		}
	}
}
