package com.ctrip.framework.dal.datasourceswitch.netstat;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * Created by lilj on 2018/3/4.
 */
public class NetStat {
    private static Logger log = LoggerFactory.getLogger(NetStat.class);

    public void netstatCMD(String hostname, boolean closedByServer) throws Exception {
        String[] cmd = {"cmd", "/c", "netstat -ano | findstr \"10.2.74\" | findstr \"ESTABLISHED\""};
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line;
            log.info("current netstat");
            while (((line = br.readLine())) != null) {
                log.info(line);
                if (hostname.equalsIgnoreCase("FAT1868")) {
                    assertEquals(-1, line.indexOf("10.2.74.122:55111"));
                    if (closedByServer)
                        assertNotEquals(-1, line.indexOf("10.2.74.111:55111"));
                } else {
                    assertEquals(-1, line.indexOf("10.2.74.111:55111"));
                    if (closedByServer)
                        assertNotEquals(-1, line.indexOf("10.2.74.122:55111"));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            fail();
        }
    }
}
