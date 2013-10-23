package com.ctrip.sysdev.das.netty4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import com.ctrip.sysdev.das.domain.Response;

public class TimeCostSendTask implements Runnable {
	private UUID taskId;
	private long decodeRequestTime;
	private long dbTime;
	private long encodeResponseTime;
	
	public TimeCostSendTask(Response response) {
		taskId = response.getTaskid();
		decodeRequestTime = response.getDecodeRequestTime();
		dbTime = response.getDbTime();
		encodeResponseTime = response.getEncodeResponseTime();
	}
	
	@Override
	public void run() {
		URL url;
		try {
			url = new URL("http://localhost:8080/console/dal/das/monitor/timeCosts");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(
					conn.getOutputStream());

			StringBuilder sb = new StringBuilder();
			sb.append("id=").append(taskId).
				append("&timeCost=decodeRequestTime:").append(decodeRequestTime).
				append(";dbTime:").append(dbTime).
				append(";encodeResponseTime:").append(encodeResponseTime);
			
			writer.write(sb.toString());
			writer.flush();
			writer.close();
			
			InputStreamReader reder = new InputStreamReader(conn.getInputStream(), "utf-8");  
	        BufferedReader breader = new BufferedReader(reder);  
	        while ((breader.readLine()) != null) {}
	        breader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
