package com.ctrip.platform.dal.sql.logging;

public class Watcher {
	
	private static final String JSON_PATTERN = "{'Decode':'%s','Connect':'%s','Prepare':'%s','Excute':'%s','ClearUp':'%s'}";
	
	private long begin;
	private long beginConnect;
	private long endConnect;
	private long beginExecute;
	private long endExecute;
	private long end;
	
	public void begin(){
		this.begin = System.currentTimeMillis();
	}
	
	public void beginConnect(){
		this.beginConnect = System.currentTimeMillis();
	}
	
	public void endConnect(){
		this.endConnect = System.currentTimeMillis();
	}
	
	public void beginExecute(){
		this.beginExecute = System.currentTimeMillis();
	}
	
	public void endExectue(){
		this.endExecute = System.currentTimeMillis();
	}
	
	public void end(){
		this.end = System.currentTimeMillis();
	}	
	
	public String toJson(){
		String json = "";
		if(this.begin != 0 && this.beginConnect >= this.begin && 
				this.endConnect >= this.beginConnect && 
				this.beginExecute >= this.endConnect && 
				this.endExecute >= beginExecute && 
				this.end >= this.endExecute){
			json = String.format(JSON_PATTERN, this.beginConnect - this.begin,
					this.endConnect - this.beginConnect, this.beginExecute - this.endConnect,
					this.endExecute - this.beginExecute, this.end - this.endExecute);
		} 
		return json;
	}
}
