package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.enums.DatabaseType;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AllInOneConfigParser {

    private static final Logger log = Logger.getLogger(AllInOneConfigParser.class);
    private static final Pattern dbLinePattern = Pattern.compile(" name=\"([^\"]+)\" connectionString=\"([^\"]+)\"");
    private static final Pattern dburlPattern =
            Pattern.compile("(data\\ssource|server|address|addr|network)=([^;]+)", 2);
    private static final Pattern dbuserPattern = Pattern.compile("(uid|user\\sid)=([^;]+)", 2);
    private static final Pattern dbpasswdPattern = Pattern.compile("(password|pwd)=([^;]+)", 2);
    private static final Pattern dbnamePattern = Pattern.compile("(database|initial\\scatalog)=([^;]+)", 2);
    @SuppressWarnings("unused")
    private static final Pattern dbcharsetPattern = Pattern.compile("(charset)=([^;]+)", 2);
    private static final Pattern dbportPattern = Pattern.compile("(port)=([^;]+)", 2);
    private ConcurrentHashMap<String, DalGroupDB> allDbs = new ConcurrentHashMap<>();

    public AllInOneConfigParser(String configFilePath) throws Exception {
        BufferedReader br = null;
        try {
            if (null != configFilePath && new File(configFilePath).exists()) {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(configFilePath)));
            } else {
                br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/Database.config")));
            }

            String line = br.readLine();
            while (line != null) {
                try {
                    Matcher matcher = dbLinePattern.matcher(line);
                    if (matcher.find()) {
                        String dbLogicName = matcher.group(1);
                        DalGroupDB db = parseDotNetDBConnString(matcher.group(2));
                        db.setDbname(dbLogicName);
                        db.setDalGroupId(-1);
                        this.allDbs.put(dbLogicName, db);
                    }
                    line = br.readLine();
                } catch (Exception ex) {
                    log.error("parse all in one error: " + line, ex);
                }
            }
            return;
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("close DB config file IO error", e);
                }
            }
        }
    }

    public Map<String, DalGroupDB> getDBAllInOneConfig() {
        return this.allDbs;
    }

    private DalGroupDB parseDotNetDBConnString(String connStr) {
        DalGroupDB db = new DalGroupDB();
        try {
            String dbhost = null;
            Matcher matcher = dbnamePattern.matcher(connStr);
            if (matcher.find()) {
                db.setDbCatalog(matcher.group(2));
            }
            matcher = dburlPattern.matcher(connStr);
            if (matcher.find()) {
                String[] dburls = matcher.group(2).split(",");
                dbhost = dburls[0];
                if (dburls.length == 2) {
                    db.setDbAddress(dbhost);
                    db.setDbPort(dburls[1]);
                    db.setDbProvidername(DatabaseType.SQLServer.getValue());

                } else {
                    matcher = dbportPattern.matcher(connStr);
                    if (matcher.find()) {
                        db.setDbAddress(dbhost);
                        db.setDbPort(matcher.group(2));
                    } else {
                        db.setDbAddress(dbhost);
                        db.setDbPort("3306");
                    }
                    db.setDbProvidername(DatabaseType.MySQL.getValue());
                }
            }
            matcher = dbuserPattern.matcher(connStr);
            if (matcher.find()) {
                db.setDbUser(matcher.group(2));
            }
            matcher = dbpasswdPattern.matcher(connStr);
            if (matcher.find()) {
                db.setDbPassword(matcher.group(2));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return db;
    }

}
