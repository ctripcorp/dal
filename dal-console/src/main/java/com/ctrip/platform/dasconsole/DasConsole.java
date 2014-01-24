package com.ctrip.platform.dasconsole;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class DasConsole {
	public static void main(String[] args) throws Exception {
		// Server server = new Server(8080);
		// server.setHandler(new DasConsoleHandler());
		//
		// server.start();
		// server.join();
		new DasConsole().testPost();
	}

	private void testPost() {
		URL url;
		try {
			url = new URL(
					"http://localhost:8080/console/dal/das/monitor/timeCosts");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(
					conn.getOutputStream());

			writer.write("id=112233&timeCost=a:123;b:123");
			writer.flush();
			writer.close();
			
			InputStreamReader reder = new InputStreamReader(conn.getInputStream(), "utf-8");  
			  
	        BufferedReader breader = new BufferedReader(reder);  
	  
	        String content = null;  
	        String result = null;  
	        while ((content = breader.readLine()) != null) {  
	            result += content + "\n";  
	        }
	        breader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
