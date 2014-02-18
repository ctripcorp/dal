package com.ctrip.platform.dal.dao.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author gawu
 * @deprecated need to revise for production
 */
public class TimeCostSender extends Thread {

	private static TimeCostSender instance = new TimeCostSender();

	private ConcurrentLinkedQueue<String> queue;

	public ConcurrentLinkedQueue<String> getQueue() {
		return queue;
	}

	private TimeCostSender() {
		queue = new ConcurrentLinkedQueue<String>();
		this.start();
	}

	public static TimeCostSender getInstance() {
		return instance;
	}

	private Logger logger = LoggerFactory.getLogger(this.getClass());

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
						String.format("http://%s/rest/monitor/timeCosts", "localhost:8080"));
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

