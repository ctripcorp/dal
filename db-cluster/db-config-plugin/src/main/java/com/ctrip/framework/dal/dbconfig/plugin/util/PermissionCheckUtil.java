package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.ctrip.framework.dal.dbconfig.plugin.entity.PermissionCheckEnum;
import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.*;


public class PermissionCheckUtil {
    private static final String WHITE_BLACK_LIST_SPLITTER = ",";

    /**
     * Check permission for reading
     * [*] clientAppId 就是当前TitanPlugin直接返回true [读取自身配置不走这里, 暂不使用]
     * [1]白名单验证检查, 如果配置了白名单使用白名单验证，如果没有配置则默认放行
     * [2]黑名单验证检查, 如果配置了黑名单使用黑名单验证，如果没有配置则默认放行
     * [3]如果黑白名单都配置，则仅白名单生效
     * [4]检查'enabled'开关是否打开
     *
     * @param configuration, original text
     * @param clientAppId
     * @return
     */
    public static PermissionCheckEnum readPermissionCheck(String configuration, String clientAppId) throws Exception {
        PermissionCheckEnum checkResult = PermissionCheckEnum.PASS;

        //other client to check it
        Properties properties = CommonHelper.parseString2Properties(configuration);
        if (properties != null) {
            String appIdWhiteListStr = (String) properties.get(WHITE_LIST);   //eg: 111111,222222
            if (!Strings.isNullOrEmpty(appIdWhiteListStr)) {
                String[] appIdArray = appIdWhiteListStr.split(WHITE_BLACK_LIST_SPLITTER);
                List<String> appIdList = Arrays.asList(appIdArray);
                if (!appIdList.contains(clientAppId)) {
                    checkResult = PermissionCheckEnum.FAIL_WHITE_LIST;
                }
            } else {
                String appIdBlackListStr = (String) properties.get(BLACK_LIST);   //eg: 333333,444444
                if (!Strings.isNullOrEmpty(appIdBlackListStr)) {
                    String[] appIdArray = appIdBlackListStr.split(WHITE_BLACK_LIST_SPLITTER);
                    List<String> appIdList = Arrays.asList(appIdArray);
                    if (appIdList.contains(clientAppId)) {
                        checkResult = PermissionCheckEnum.FAIL_BLACK_LIST;
                    }
                }
            }
            //check 'enabled'
            String enabledStr = (String) properties.get(ENABLED);
            if (!Strings.isNullOrEmpty(enabledStr)) {
                boolean enabled = Boolean.parseBoolean(enabledStr);
                if (enabled == false) {
                    checkResult = PermissionCheckEnum.FAIL_KEY_DISABLED;
                }
            }
        }
        return checkResult;
    }

    /**
     * check site permission
     * Only clientIp in whiteIp list can get the permission
     *
     * @param whiteIps, format: 10.1.1.0,10.1.1.1
     * @param clientIp, request client ip
     * @return
     * @throws Exception
     */
    public static boolean checkSitePermission(String whiteIps, String clientIp) {
        boolean checkPass = false;
        if (!Strings.isNullOrEmpty(whiteIps)) {
            String[] whiteIpArray = whiteIps.split(",");
            List<String> whiteIpList = Arrays.asList(whiteIpArray);
            if (whiteIpList.contains(clientIp)) {
                checkPass = true;
            }
        }
        return checkPass;
    }


    /**
     * check whether input client ip in httpWhiteIps
     *
     * @param httpsWhiteIps, format: 10.1.1.0,10.1.1.1
     * @param clientIp,      client ip
     * @return
     */
    public static boolean checkClientIpInHttpWhiteList(String httpsWhiteIps, String clientIp) {
        boolean inHttpWhiteList = false;
        if (!Strings.isNullOrEmpty(httpsWhiteIps) && !Strings.isNullOrEmpty(clientIp)) {
            String[] whiteIpArray = httpsWhiteIps.split(WHITE_BLACK_LIST_SPLITTER);
            List<String> whiteIpList = Arrays.asList(whiteIpArray);
            if (whiteIpList.contains(clientIp)) {
                inHttpWhiteList = true;
            }
        }
        return inHttpWhiteList;
    }


}
