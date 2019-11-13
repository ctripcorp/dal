package com.ctrip.framework.dal.dbconfig.plugin;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.NetworkUtil;
import com.ctrip.framework.dal.dbconfig.plugin.util.PermissionCheckUtil;
import com.ctrip.framework.dal.dbconfig.plugin.util.SecurityUtil;
import com.dianping.cat.Cat;
import com.google.common.base.Strings;
import org.apache.commons.codec.binary.Base64;
import qunar.tc.qconfig.common.util.Constants;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.security.Key;
import java.util.List;

/**
 * @author c7ch23en
 */
public abstract class ServerPluginAdapter extends PluginAdapter implements CommonConstants {

    //whether allow to process this request
    protected boolean canProcess(HttpServletRequest request, String pluginAppId) {
        boolean canDo = false;
        String group = request.getParameter(Constants.GROUP_NAME);
        if (pluginAppId.equals(group)) {
            canDo = true;
        }
        return canDo;
    }

    protected void checkHttps(HttpServletRequest request, PluginConfig config) throws Exception {
        String schema = request.getScheme();
        if (!REQUEST_SCHEMA_HTTPS.equalsIgnoreCase(schema)) {
            //check whether this http client ip in white list
            checkHttpClientValid(request, config);
        }
    }

    protected void checkNoParent(String dataId, EnvProfile rawProfile, EnvProfile configProfile, PluginConfig config) throws Exception {
        //noParent check [2017-10-31]
        String subEnv_input = rawProfile.formatSubEnv();
        String noParentSuffix = config.getParamValue(NO_PARENT_SUFFIX);
        boolean isPro = CommonHelper.checkPro(configProfile.formatEnv());
        boolean noParent = CommonHelper.checkSubEnvNoParent(subEnv_input, noParentSuffix, isPro);//use 'subEnv_input'
        if (noParent) {
            //compare used subEnv is just user input one
            String subEnv_actual = configProfile.formatSubEnv();
            if (subEnv_input != null && !subEnv_input.equalsIgnoreCase(subEnv_actual)) {
                //let it go when profile is like 'LPT:xxx'  [2018-02-23]
                String topEnv = configProfile.formatEnv();
                if (!CommonHelper.checkLptEnv(topEnv)) {
                    throw new IllegalArgumentException("dataId=" + dataId + ", noParent=true, subEnv not match! subEnv_input=" + subEnv_input + ", subEnv_actual=" + subEnv_actual);
                }
            }
        }
    }

    //check http client valid. (client ip in white list)
    private void checkHttpClientValid(HttpServletRequest request, PluginConfig config) throws Exception {
        //get http white list
        String httpWhiteList = config.getParamValue(HTTP_WHITE_LIST);
        String realClientIp = getRealClientIp(request, config);
        boolean inHttpWhiteList = PermissionCheckUtil.checkClientIpInHttpWhiteList(httpWhiteList, realClientIp);
        if (!inHttpWhiteList) {
            throw new IllegalAccessException("Invalid request schema, only support https! inHttpWhiteList=" + inHttpWhiteList + ", realClientIp=" + realClientIp);
        }
    }

    //get real client ip from token
    private String getRealClientIp(HttpServletRequest request, PluginConfig config) throws Exception {
        String xRealIp = getXRealIp(request);
        String hiddenClientIp = getHiddenClientIp(request, config);

        if (Strings.isNullOrEmpty(xRealIp) || Strings.isNullOrEmpty(hiddenClientIp)) {
            StringBuilder sb = new StringBuilder(300);
            sb.append("Invalid request, xRealIp or hiddenClientIp is empty! ");
            sb.append("xRealIp=").append(xRealIp).append(", ");
            sb.append("hiddenClientIp=").append(hiddenClientIp);
            throw new IllegalAccessException(sb.toString());
        }

        // check whether public net
        String netType = NetworkUtil.getNetType(request);
        boolean fromPublicNet = NetworkUtil.isFromPublicNet(netType);
        Cat.logEvent("ServerPlugin.PreHandle.NetType", String.format("fromPublicNet:%s,netType:%s", fromPublicNet, netType));

        // 从专线过来的请求(内网ip)，需要比较
        if (!fromPublicNet) {
            // check xRealIp and hiddenClientIp whether equal
            if (!xRealIp.equals(hiddenClientIp)) {
                String ttToken = request.getHeader(TT_TOKEN);
                StringBuilder sb = new StringBuilder(300);
                sb.append("Invalid request, [xRealIp, hiddenClientIp] not equal! ");
                sb.append("xRealIp=").append(xRealIp).append(", ");
                sb.append("hiddenClientIp=").append(hiddenClientIp).append(", ");
                sb.append("ttToken=").append(ttToken);
                throw new IllegalAccessException(sb.toString());
            }
        } else {
            Cat.logEvent("ServerPlugin.PublicNet.Request", String.format("hiddenClientIp:%s,xRealIp:%s", hiddenClientIp, xRealIp));
        }

        return hiddenClientIp;
    }

    private String getXRealIp(HttpServletRequest request) throws Exception {
        String xRealIp = request.getHeader(X_REAL_IP);
        //if xRealIp is empty, use original clientIp
        if (Strings.isNullOrEmpty(xRealIp)) {
            xRealIp = NetworkUtil.getClientIp(request);
        }
        return xRealIp;
    }

    private String getHiddenClientIp(HttpServletRequest request, PluginConfig config) throws Exception {
        String hiddenClientIp = null;
        String ttToken = request.getHeader(TT_TOKEN);
        if (!Strings.isNullOrEmpty(ttToken)) {
            String configKey = config.getParamValue(TOKEN_KEY);
            byte[] bb = Base64.decodeBase64(configKey);
            Key key = null;
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new ByteArrayInputStream(bb));
                key = (Key) ois.readObject();
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (Exception e) {
                        //
                    }
                }
            }
            List<String> decodedToken = SecurityUtil.decode(ttToken, key);
            if (decodedToken != null && decodedToken.size() >= 3) {
                hiddenClientIp = decodedToken.get(2);
            }
        }

        return hiddenClientIp;
    }
}
