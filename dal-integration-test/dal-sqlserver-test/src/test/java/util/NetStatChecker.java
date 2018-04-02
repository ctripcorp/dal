package util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by lilj on 2018/3/4.
 */
public class NetStatChecker {
    private static Logger log = LoggerFactory.getLogger(NetStatChecker.class);

    public static Integer netstatCMD() throws Exception {
        String[] cmd = {"cmd", "/c", "netstat -ano | findstr \"10.2.8.231\" | findstr \"ESTABLISHED\""};
        int connectionNum=0;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line;
            log.info("current netstat");
            while (((line = br.readLine())) != null) {
                log.info(line);
                connectionNum++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return connectionNum;
    }
}
