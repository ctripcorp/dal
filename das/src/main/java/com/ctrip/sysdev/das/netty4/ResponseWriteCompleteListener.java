package com.ctrip.sysdev.das.netty4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import com.ctrip.sysdev.das.domain.Response;

public class ResponseWriteCompleteListener implements ChannelFutureListener {
	private static final Logger logger = LoggerFactory
			.getLogger(ResponseWriteCompleteListener.class);

	private Response response;

	public ResponseWriteCompleteListener(Response response) {
		this.response = response;
	}

	public void operationComplete(ChannelFuture future) throws Exception {
		logTime();
		if (!future.isSuccess()) {
			logger.error("server write response error ");
			logError();
		} else {
			logger.info("server write response ok ");
			logStatistics();
		}
	}

	public void logError() {
		logStatistics();
	}

	private void logTime() {
		logger.info("Task Id: " + response.getTaskid());
		logger.info("Total row count: " + response.totalCount
				+ "  Decode/Execution/Encode: "
				+ response.getDecodeRequestTime() + "/" + response.getDbTime()
				+ "/" + response.getEncodeResponseTime());
	}

	public void logStatistics() {
		URL url;
		String result = "";

		try {
			url = new URL(
					"http://localhost:8080/console/dal/das/monitor/timeCosts");
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(
					conn.getOutputStream());

			StringBuilder sb = new StringBuilder();
			sb.append("id=").append(response.getTaskid())
					.append("&timeCost=decodeRequestTime:")
					.append(response.getDecodeRequestTime()).append(";dbTime:")
					.append(response.getDbTime())
					.append(";encodeResponseTime:")
					.append(response.getEncodeResponseTime());

			result = sb.toString();
			writer.write(sb.toString());
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
