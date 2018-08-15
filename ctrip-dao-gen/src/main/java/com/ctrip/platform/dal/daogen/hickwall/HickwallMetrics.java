package com.ctrip.platform.dal.daogen.hickwall;

import com.ctrip.ops.hickwall.HickwallUDPReporter;
import io.dropwizard.metrics5.Gauge;
import io.dropwizard.metrics5.MetricName;
import io.dropwizard.metrics5.MetricRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HickwallMetrics {
    private static final MetricRegistry metrics = new MetricRegistry();
    private static final String ALL = "all";
    private static final String JAVA_ALL = "java all";
    private static final String JAVA_CTRIP_DAL_CLIENT = "java ctrip dal client";
    private static final String JAVA_CTRIP_DATASOURCE = "java ctrip datasource";
    private static final String NET_DAL = ".net dal";

    public static void initHickwallMetrics() {
        HickwallUDPReporter.enable(metrics, // singleton metrics registry
                30 * 60, // interval, minimum 10s
                TimeUnit.SECONDS, // interval time unit
                "udp.sink.hickwall.ctripcorp.com", // hickwall receive address //udp.sink.hickwall.ctripcorp.com:8090
                                                   // //10.2.29.118:8090
                8090, // port
                "SmallestDB" // influxdb database name
        );
    }

    public static void setAllMetricValue(Integer count) {
        setMetricValue(ALL, count);
    }

    public static void setJavaAllMetricValue(Integer count) {
        setMetricValue(JAVA_ALL, count);
    }

    public static void setJavaCtripDalClientMetricValue(Integer count) {
        setMetricValue(JAVA_CTRIP_DAL_CLIENT, count);
    }

    public static void setJavaCtripDataSourceMetricValue(Integer count) {
        setMetricValue(JAVA_CTRIP_DATASOURCE, count);
    }

    public static void setNetDalMetricValue(Integer count) {
        setMetricValue(NET_DAL, count);
    }

    private static void setMetricValue(String name, Integer count) {
        Map<String, String> tags = new HashMap<>();
        tags.put("appid", "930201");

        MetricName metricName = new MetricName(name, tags);
        Gauge<Integer> gauge = () -> count;
        MetricRegistry.MetricSupplier<Gauge> supplier = () -> gauge;
        metrics.gauge(metricName, supplier);
    }

}
