package com.ctrip.platform.dal.daogen.hickwall;

import com.ctrip.ops.hickwall.HickwallUDPReporter;
import com.ctrip.platform.dal.daogen.util.IPUtils;
import io.dropwizard.metrics5.MetricName;
import io.dropwizard.metrics5.MetricRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class HickwallMetrics {
    private static final MetricRegistry metrics = new MetricRegistry();
    // apps count
    private static final String ALL = "all";
    private static final String ALL_JAVA = "all.java";
    private static final String ALL_NET = "all.net";

    // cat app count
    private static final String ALL_JAVA_IN_CAT = "all.java.cat";
    private static final String ALL_NET_IN_CAT = "all.net.cat";

    // dal client users count
    private static final String JAVA_ALL = "java.all";
    private static final String JAVA_CTRIP_DAL_CLIENT = "java.ctrip.dal.client";
    private static final String JAVA_CTRIP_DATASOURCE = "java.ctrip.datasource";
    private static final String NET_DAL = "net.dal";

    private static AtomicInteger allCount = new AtomicInteger();
    private static AtomicInteger allJavaCount = new AtomicInteger();
    private static AtomicInteger allNetCount = new AtomicInteger();

    private static AtomicInteger allJavaCountInCat = new AtomicInteger();
    private static AtomicInteger allNetCountInCat = new AtomicInteger();

    private static AtomicInteger javaAllCount = new AtomicInteger();
    private static AtomicInteger javaCtripDalClientCount = new AtomicInteger();
    private static AtomicInteger javaCtripDataSourceCount = new AtomicInteger();
    private static AtomicInteger netDalCount = new AtomicInteger();

    public static void initHickwallMetrics() {
        HickwallUDPReporter.enable(metrics, // singleton metrics registry
                1 * 60, // interval, minimum 10s
                TimeUnit.SECONDS, // interval time unit
                "udp.sink.hickwall.ctripcorp.com", // hickwall receive address //udp.sink.hickwall.ctripcorp.com:8090
                // //10.2.29.118:8090
                8090, // port
                "FX" // influxdb database name
        );

        // all app
        initMetricValue(ALL, allCount);
        initMetricValue(ALL_JAVA, allJavaCount);
        initMetricValue(ALL_NET, allNetCount);

        // all cat
        initMetricValue(ALL_JAVA_IN_CAT, allJavaCountInCat);
        initMetricValue(ALL_NET_IN_CAT, allNetCountInCat);

        initMetricValue(JAVA_ALL, javaAllCount);
        initMetricValue(JAVA_CTRIP_DAL_CLIENT, javaCtripDalClientCount);
        initMetricValue(JAVA_CTRIP_DATASOURCE, javaCtripDataSourceCount);
        initMetricValue(NET_DAL, netDalCount);
    }

    private static void initMetricValue(String name, AtomicInteger count) {
        Map<String, String> tags = new HashMap<>();
        tags.put("appid", "930201");

        MetricName metricName = new MetricName(name, tags);
        metrics.gauge(metricName, () -> () -> count.get());
    }

    public static void logMetricValue(String name, Map<String, String> tags, int appIdCount) {
        String ip = IPUtils.getExecuteIPFromQConfig();
        if (!IPUtils.getLocalHostIp().equalsIgnoreCase(ip)) {
            return;
        }
        MetricName metricName = new MetricName(name, tags);
        metrics.resetCounter(metricName).inc(appIdCount);
    }

    public static void setAllMetricValue(Integer count) {
        allCount.set(count);
    }

    public static void setAllJavaMetricValue(Integer count) {
        allJavaCount.set(count);
    }

    public static void setAllNetMetricValue(Integer count) {
        allNetCount.set(count);
    }

    public static void setAllJavaInCatMetricValue(Integer count) {
        allJavaCountInCat.set(count);
    }

    public static void setAllNetInCatMetricValue(Integer count) {
        allNetCountInCat.set(count);
    }

    public static void setJavaAllMetricValue(Integer count) {
        javaAllCount.set(count);
    }

    public static void setJavaCtripDalClientMetricValue(Integer count) {
        javaCtripDalClientCount.set(count);
    }

    public static void setJavaCtripDataSourceMetricValue(Integer count) {
        javaCtripDataSourceCount.set(count);
    }

    public static void setNetDalMetricValue(Integer count) {
        netDalCount.set(count);
    }

}
