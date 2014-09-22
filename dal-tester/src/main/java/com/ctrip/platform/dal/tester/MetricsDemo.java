package com.ctrip.platform.dal.tester;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.ctrip.framework.clogging.agent.config.LogConfig;
import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;

/**
 * @author: huang_jie
 * @date: 1/17/13 11:06 AM
 */
public class MetricsDemo {
    private static IMetric metricLogger = MetricManager.getMetricer();

    static {
        //set AppId and collector address
        LogConfig.setAppID("929143");
//        LogConfig.setLoggingServerIP("localhost");
        LogConfig.setLoggingServerIP("192.168.82.58");
        LogConfig.setLoggingServerPort("63100");
    }

    public static void main(String[] args) {
        long count = 0;
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 360; j += 10) {
                // sin metric
                float v = j * (float) Math.PI / 180;
                Map<String, String> tags1 = new HashMap<String, String>();
                tags1.put("type", "sin");
                float m1 = (float) Math.sin(v) + 1;
                metricLogger.log("showcount0720", m1, tags1);

                // cos metric
                Map<String, String> tags2 = new HashMap<String, String>();
                tags2.put("type", "cos");
                float m2 = (float) Math.cos(v) + 1;
                metricLogger.log("showcount0720", m2, tags2);

                // rate demo
                Map<String, String> tags3 = new HashMap<String, String>();
                tags3.put("type", "rate");
                count = count + 5 + rnd.nextInt(4);
                metricLogger.log("showcount0720", count, tags3);


                try {
                    Thread.sleep(2 * 1000);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }

    }
}
