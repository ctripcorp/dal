package testUtil;


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
    private static String[] cmd;

    public static Integer netstatCMD() throws Exception {
        Process process;
        log.info(String.format("check netstat"));
        try{
            cmd = new String[]{"/bin/sh", "-c", "netstat -ano | grep \"10.5.21.126\" | grep \"ESTABLISHED\""};
            process = Runtime.getRuntime().exec(cmd);
        }catch (Exception e) {
            cmd = new String[]{"cmd", "/c", "netstat -ano | findstr \"10.5.21.126\" | findstr \"ESTABLISHED\""};
            process = Runtime.getRuntime().exec(cmd);
        }
        int connectionNum = 0;
        try {
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
