package com.ctrip.platform.dal.sql.logging;

public class DalWatcher {
	private static ThreadLocal<CostRecorder> curRecorder = new ThreadLocal<CostRecorder>();
	
	private static final String JSON_PATTERN = "{'Decode':'%s','Connect':'%s','Prepare':'%s','Excute':'%s','ClearUp':'%s'}";
	
	private static class CostRecorder {
		long begin;
		long beginConnect;
		long endConnect;
		long beginExecute;
		long endExecute;
		long end;
	}
	
	private static CostRecorder recorder() {
		CostRecorder value = curRecorder.get();
		if(value == null) {
			value = new CostRecorder();
			curRecorder.set(value);
		}
		
		return value;
	}
	
	public static void begin(){
		recorder().begin = System.currentTimeMillis();
	}
	
	public static void beginConnect(){
		recorder().beginConnect = System.currentTimeMillis();
	}
	
	public static void endConnect(){
		recorder().endConnect = System.currentTimeMillis();
	}
	
	public static void beginExecute(){
		recorder().beginExecute = System.currentTimeMillis();
	}
	
	public static void endExectue(){
		recorder().endExecute = System.currentTimeMillis();
	}
	
	public static void end(){
		recorder().end = System.currentTimeMillis();
	}	
	
	public static String toJson(){
		CostRecorder cur = recorder();
		String json = "";
		if(cur.begin != 0 && cur.beginConnect >= cur.begin && 
				cur.endConnect >= cur.beginConnect && 
				cur.beginExecute >= cur.endConnect && 
				cur.endExecute >= cur.beginExecute && 
				cur.end >= cur.endExecute){
			json = String.format(JSON_PATTERN, cur.beginConnect - cur.begin,
					cur.endConnect - cur.beginConnect, cur.beginExecute - cur.endConnect,
					cur.endExecute - cur.beginExecute, cur.end - cur.endExecute);
		} 
		return json;
	}
}
