package com.dal.sqlserver.test.control;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.dal.sqlserver.test.People;
import com.xrosstools.xunit.Context;
import com.xrosstools.xunit.Processor;

public class ResponseBuilder implements Processor {

	@Override
	public void process(Context ctx) {
		WebContext context = (WebContext)ctx;
		DalHints hints = context.getHints();
		HttpServletResponse response = context.getResponse();
		response.setContentType("text/html;charset=UTF-8");
		try {
			if(context.isSupported()) {
				buildResponse(context.getResponsValue(), hints, response);
			}else{
				buildNotSupportedResponse(response);
			}
		} catch (Exception e) {
			context.handle(e);
		}
	}

	private void buildResponse(Object returnValue, DalHints hints, HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		writeDirectResult(returnValue, writer);
		writeAsyncResult(hints, writer);
		writeCallbackResult(hints, writer);
		writeKeyHolder(hints, writer);		
	}

	private void buildNotSupportedResponse(HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		writer.write("Not supported in Sql Server");
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
		if(returnValue instanceof String)
			writer.write((String)returnValue);
		else if(returnValue instanceof Number)
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
}
