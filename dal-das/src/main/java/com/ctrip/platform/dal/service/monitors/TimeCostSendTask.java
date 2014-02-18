package com.ctrip.platform.dal.service.monitors;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.service.DalServer;

/**
 * 
 * @author gawu
 * 
 */
public class TimeCostSendTask extends Thread {

	private static TimeCostSendTask instance = new TimeCostSendTask();

	private ConcurrentLinkedQueue<String> queue;

	public ConcurrentLinkedQueue<String> getQueue() {
		return queue;
	}

	private TimeCostSendTask() {
		queue = new ConcurrentLinkedQueue<String>();
		this.start();
	}

	public static TimeCostSendTask getInstance() {
		return instance;
	}

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	// private UUID taskId;
	// private long decodeRequestTime;
	// private long dbTime;
	// private long encodeResponseTime;
	//
	// public TimeCostSendTask(Response response) {
	// taskId = response.getTaskid();
	// decodeRequestTime = response.getDecodeRequestTime();
	// dbTime = response.getDbTime();
	// encodeResponseTime = response.getEncodeResponseTime();
	// }
	//
	@Override
	public void run() {
		URL url;
		String result = "";

		while (true) {
			try {
				String timeCost = queue.poll();
				if (null == timeCost) {
					Thread.sleep(100);
					continue;
				}
				url = new URL(
						String.format("http://%s/rest/monitor/timeCosts", DalServer.consoleAddr));
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);

				OutputStreamWriter writer = new OutputStreamWriter(
						conn.getOutputStream());

				writer.write(timeCost);
				writer.flush();
				writer.close();

				InputStreamReader reder = new InputStreamReader(
						conn.getInputStream(), "utf-8");
				BufferedReader breader = new BufferedReader(reder);
				while ((breader.readLine()) != null) {
				}
				breader.close();
			} catch (Exception e) {
				logger.error("Error sending statistics: " + result, e);
			}
		}
	}
}
