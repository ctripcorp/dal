package com.ctrip.platform.dal.dao.client;

public class DalWatcher {
	private static ThreadLocal<CostRecorder> costRecorder = new ThreadLocal<CostRecorder>();
	
	private static final String JSON_PATTERN = "{'Decode':'%s','Connect':'%s','Prepare':'%s','Excute':'%s','ClearUp':'%s'}";
	
	private static class CostRecorder {
		long corssShardBegin;
		long begin;
		long beginConnect;
		long endConnect;
		long beginExecute;
		long endExecute;
		long end;
		long corssShardEnd;
		
		void reset() {
			begin = 0;
			beginConnect = 0;
			endConnect = 0;
			beginExecute = 0;
			endExecute = 0;
			end = 0;
		}
	}
	
	public static void init(){
		if(costRecorder!= null)
			return;
		
		costRecorder = new ThreadLocal<CostRecorder>();
	}
	
	public static void destroy(){
		if(costRecorder!= null) {
			costRecorder.remove();
			costRecorder = null;
		}
	}
	
	private static CostRecorder recorder() {
		CostRecorder curRecorder = costRecorder.get();
		if(curRecorder == null) {
			curRecorder = new CostRecorder();
			costRecorder.set(curRecorder);
		}
		
		return curRecorder;
	}
	
	
	public static void reset() {
		recorder().reset();
	}
	
	public static void crossShardBegin(){
		recorder().corssShardBegin = System.currentTimeMillis();
	}
	
	public static void crossShardEnd(){
		recorder().corssShardEnd = System.currentTimeMillis();
	}
	
	public static void begin(){
		reset();
		recorder().begin = System.currentTimeMillis();
	}
	
	public static void beginConnect(){
		CostRecorder curRecorder = recorder();
		
		// Check abnormal case. We only need to check it here
		if(curRecorder.beginConnect != 0)
			curRecorder.reset();
		
		curRecorder.beginConnect = System.currentTimeMillis();
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
	
	public static String toJson(){
		CostRecorder cur = recorder();
		
		// Final end
		cur.end = System.currentTimeMillis();
		
		String json = String.format(JSON_PATTERN, cur.begin == 0 ? 0 : cur.beginConnect - cur.begin,
				cur.endConnect - cur.beginConnect, cur.beginExecute - cur.endConnect,
				cur.endExecute - cur.beginExecute, cur.end - cur.endExecute);
		
		reset();
		return json;
	}
}
