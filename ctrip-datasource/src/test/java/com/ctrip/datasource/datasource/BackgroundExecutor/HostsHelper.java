package com.ctrip.datasource.datasource.BackgroundExecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;

public class HostsHelper {
    public static String getHostFile() {
        String fileName = null;
        if ("linux".equalsIgnoreCase(System.getProperty("os.name"))) {
            fileName = "/etc/hosts";
        } else {
            fileName = System.getenv("windir") + "\\system32\\drivers\\etc\\hosts";
        }
        return fileName;
    }

    public synchronized static boolean deleteHost(String ip, String domain) {
        if (ip == null || ip.trim().isEmpty() || domain == null || domain.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR： ip & domain must be specified");
        }
        String fileName = getHostFile();
        List<?> hostFileDataLines = null;
        try {
            hostFileDataLines = FileUtils.readLines(new File(fileName));
        } catch (IOException e) {
            System.out.println("Reading host file occurs error: " + e.getMessage());
            return false;
        }

        List<String> newLinesList = new ArrayList<>();
        boolean updateFlag = false;
        for (Object line : hostFileDataLines) {
            String strLine = line.toString();
            if (StringUtils.isEmpty(strLine) || strLine.trim().equals("#")) {
                continue;
            }

            if (!strLine.trim().startsWith("#")) {
                int index = strLine.toLowerCase().indexOf(domain.toLowerCase());

                if (index != -1) {
                    updateFlag = true;
                    continue;
                }
            }

            newLinesList.add(strLine);
        }

        if (updateFlag) {
            try {
                FileUtils.writeLines(new File(fileName), newLinesList);
            } catch (IOException e) {
                System.out.println("Updating host file occurs error: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public synchronized static boolean updateHost(String ip, String domain) {
        if (ip == null || ip.trim().isEmpty() || domain == null || domain.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR： ip & domain must be specified");
        }
        String splitter = " ";
        String fileName = getHostFile();
        List<?> hostFileDataLines = null;
        try {
            hostFileDataLines = FileUtils.readLines(new File(fileName));
        } catch (IOException e) {
            System.out.println("Reading host file occurs error: " + e.getMessage());
            return false;
        }

        List<String> newLinesList = new ArrayList<String>();
        boolean findFlag = false;
        boolean updateFlag = false;
        for (Object line : hostFileDataLines) {
            String strLine = line.toString();
            if (StringUtils.isEmpty(strLine) || strLine.trim().equals("#")) {
                continue;
            }
            if (!strLine.startsWith("#")) {
                strLine = strLine.replaceAll("", splitter);
                int index = strLine.toLowerCase().indexOf(domain.toLowerCase());
                if (index != -1) {
                    if (findFlag) {
                        updateFlag = true;
                        continue;
                    }
                    String[] array = strLine.trim().split(splitter);
                    Boolean isMatch = false;
                    for (int i = 1; i < array.length; i++) {
                        if (domain.equalsIgnoreCase(array[i]) == false) {
                            continue;
                        } else {
                            findFlag = true;
                            isMatch = true;
                            if (array[0].equals(ip) == false) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(ip);
                                for (int j = 1; i < array.length; i++) {
                                    sb.append(splitter).append(array[j]);
                                }
                                strLine = sb.toString();
                                updateFlag = true;
                            }
                        }
                    }
                }
            }
            newLinesList.add(strLine);
        }

        if (!findFlag) {
            newLinesList.add(new StringBuilder(ip).append(splitter).append(domain).toString());
        }

        if (updateFlag || !findFlag) {
            try {
                FileUtils.writeLines(new File(fileName), newLinesList);
            } catch (IOException e) {
                System.out.println("Updating host file occurs error: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

}
