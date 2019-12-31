package com.ctrip.datasource.cluster;

import org.junit.Test;

/**
 * @author c7ch23en
 */
public class ClusterMetaQueryTest {

    @Test
    public void test() {
        String format = "p1=%s";
        System.out.println(String.format(format, "v1") + "&op=dal");
    }

}
