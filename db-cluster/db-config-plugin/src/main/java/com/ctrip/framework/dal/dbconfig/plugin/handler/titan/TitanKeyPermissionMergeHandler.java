package com.ctrip.framework.dal.dbconfig.plugin.handler.titan;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfigManager;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.context.EnvProfile;
import com.ctrip.framework.dal.dbconfig.plugin.entity.titan.SiteInputEntity;
import com.ctrip.framework.dal.dbconfig.plugin.exception.DbConfigPluginException;
import com.ctrip.framework.dal.dbconfig.plugin.handler.BaseAdminHandler;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.QconfigServiceUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.TitanUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import qunar.tc.qconfig.plugin.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * This handler is to merge 'permission' field for titan key.
 *  Notice: permissions = union(permissions + whiteList + input)
 *          (key1, ""), (key1, "FRA-AWS"), (key1_SH, ""), (key1_SH, "FRA-AWS")
 * Sample:
 *  [POST]   http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan/config/permission/merge?titankey=titantest_lzyan_v_02&env=pro&operator=lzyan
 *
 *
 * [body] sample as bellow:
 *
 {
     "whiteList": "111111,222222"
 }

 *
 * Created by lzyan on 2018/09/25.
 */
public class TitanKeyPermissionMergeHandler extends BaseAdminHandler implements TitanConstants {

    private static final String URI = "/plugins/titan/config/permission/merge";
    private static final String METHOD = "POST";

    public TitanKeyPermissionMergeHandler(QconfigService qconfigService, PluginConfigManager pluginConfigManager) {
        super(qconfigService,pluginConfigManager);
    }

    @Override
    public String getUri() {
        return URI;
    }

    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    public PluginResult preHandle(HttpServletRequest request) {
        String env = request.getParameter(REQ_PARAM_ENV);
        String subEnv = request.getParameter(REQ_PARAM_SUB_ENV);
        if(Strings.isNullOrEmpty(env)) {    //默认: pro
            env = ENV_PRO;
        }
        String titanKey = request.getParameter(REQ_PARAM_TITAN_KEY);

        //format <titankey>
        titanKey = TitanUtils.formatTitanFileName(titanKey);
        request.setAttribute(REQ_ATTR_TITAN_KEY, titanKey);

        //format <profile>
        EnvProfile profile = new EnvProfile(env, subEnv);
        request.setAttribute(REQ_ATTR_ENV_PROFILE, profile);

        return PluginResult.oK();
    }

    @Override
    public PluginResult postHandle(HttpServletRequest request) {
        PluginResult pluginResult = PluginResult.oK();
//        Stopwatch stopwatch = Stopwatch.createStarted();
        Transaction t = Cat.newTransaction("TitanQconfigPlugin", "TitanKeyPermissionMergeHandler");
        try {
            t.addData("running class=" + getClass().getSimpleName());

            //检查参数
            paramCheck(request, t);

            String body = CommonHelper.getBody(request, true);

            /**
             * Sample body
             {
             "whiteList": "111111,222222"
             }
             */
            if(body != null) {
                SiteInputEntity siteInputEntity = GsonUtils.json2T(body, SiteInputEntity.class);
                if (siteInputEntity != null) {
                    Preconditions.checkArgument(!Strings.isNullOrEmpty(siteInputEntity.getWhiteList()), "whiteList参数不能为空");

                    EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
                    PluginConfig config = getPluginConfigManager().getPluginConfig(profile);

                    //AdminSite白名单检查
                    String clientIp = (String) request.getAttribute(PluginConstant.REMOTE_IP);
                    boolean permitted = checkPermission(clientIp, profile);
                    if(permitted) {
                        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
                        // get available pro subEnv from config item 'permission.pro.subenv.list'
                        String proSubEnvs = config.getParamValue(PERMISSION_PRO_SUBENV_LIST);
                        List<String> subEnvList = Lists.newArrayList(splitter.splitToList(proSubEnvs));
                        subEnvList.add(""); // parent env

                        // update for each key in titanKeys
                        List<ConfigDetail> updateCdList = Lists.newArrayList();
                        String titanKeys = (String) request.getAttribute(REQ_ATTR_TITAN_KEY);
                        List<String> titanKeyList = splitter.splitToList(titanKeys);
                        for(String titanKey : titanKeyList) {
                            //process key, if end with "_SH", remove it. eg: abc_SH -> abc
                            titanKey = CommonHelper.trimSH(titanKey);
                            //获取欲更新TitanKey ('permissions'字段)
                            List<ConfigDetail> cdList = buildCdListPerKey(request, siteInputEntity, titanKey, subEnvList);
                            if(cdList != null && !cdList.isEmpty()) {
                                updateCdList.addAll(cdList);
                            }
                        }

                        // get final update titanKey
                        String debugInfo = buildDebugInfo(updateCdList);
                        t.addData(debugInfo);
                        Cat.logEvent("TitanKeyPermissionMergeHandler", "SUCCESS", Event.SUCCESS, debugInfo);

                        // batch save
                        QconfigServiceUtils.batchSave(getQconfigService(), "TitanKeyPermissionMergeHandler", updateCdList, true);
                    } else {
                        t.addData("postHandleDetail(): sitePermission=false, not allow to merge!");
                        Cat.logEvent("TitanKeyPermissionMergeHandler", "NO_PERMISSION", Event.SUCCESS, "sitePermission=false, not allow to merge! clientIp=" + clientIp);
                        pluginResult = new PluginResult(PluginStatusCode.TITAN_KEY_CANNOT_WRITE, "Access ip whitelist check fail! clientIp=" + clientIp);
                    }

                }else{
                    t.addData("postHandleDetail(): siteInputEntity=null, no need to merge!");
                }
            }else{
                t.addData("postHandleDetail(): site body is null or empty, no need to merge!");
            }


            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DbConfigPluginException(e);
        } finally {
            t.complete();
//            stopwatch.stop();
//            long cost = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        }
        return pluginResult;
    }

    //parameter check
    private void paramCheck(HttpServletRequest request, Transaction t) {
        String titanKey = (String) request.getAttribute(REQ_ATTR_TITAN_KEY);
        EnvProfile profile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
        Preconditions.checkArgument(profile != null && !Strings.isNullOrEmpty(profile.formatProfile()),
                "profile参数不能为空");
        t.addData("titanKey=" + titanKey);
        t.addData("profile=" + profile.formatProfile());
        Preconditions.checkArgument(!Strings.isNullOrEmpty(titanKey), "titanKey参数不能为空");
    }

    // build update configDetail list per key ==> (key1, ""), (key1, "FRA-AWS"), (key1_SH, ""), (key1_SH, "FRA-AWS")
    private List<ConfigDetail> buildCdListPerKey(HttpServletRequest request, SiteInputEntity siteInputEntity, String titanKey, List<String> subEnvList) throws Exception {
        List<ConfigDetail> cdList = Lists.newArrayList();
        List<String> suffixList = Lists.newArrayList();
        suffixList.add(""); //current key, xxx
        EnvProfile initProfile = (EnvProfile) request.getAttribute(REQ_ATTR_ENV_PROFILE);
        String env = initProfile.formatEnv();
        boolean isPro = CommonHelper.checkPro(initProfile.formatProfile());
        if(isPro) {
            suffixList.add("_SH");  //xxx_SH
        }
        String finalKeyName;
        ConfigDetail cd;
        for(String suffix : suffixList) {
            finalKeyName = titanKey + suffix;
            for(String subEnv : subEnvList) {
                cd = buildUpdateConfigDetail(siteInputEntity, finalKeyName,
                        new EnvProfile(env, subEnv).formatProfile());
                if(cd != null) {
                    cdList.add(cd);
                }
            }
        }
        return cdList;
    }


    // build update configDetail (append clientAppId to 'permissions' of titanKey)
    private ConfigDetail buildUpdateConfigDetail(SiteInputEntity siteInputEntity, String dataId, String profile) throws Exception {
        ConfigDetail cd = null;
        String group = TITAN_QCONFIG_KEYS_APPID;
        //get input 'whiteList' from <siteInputEntity>
        String whiteListInput = siteInputEntity.getWhiteList();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(dataId), "dataId参数不能为空");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(profile), "profile参数不能为空");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(whiteListInput), "whiteListInput参数不能为空");



        //get current config from qconfig
        ConfigField cm = new ConfigField(group, dataId, profile);
        List<ConfigField> configFieldList = Lists.newArrayList(cm);
        List<ConfigDetail> configDetailList = QconfigServiceUtils.currentConfigWithoutPriority(getQconfigService(),
                "TitanKeyPermissionMergeHandler", configFieldList);
        if(configDetailList != null && !configDetailList.isEmpty()) {
            //已经存在，更新
            ConfigDetail configDetail = configDetailList.get(0);    //get first
            String encryptOldConf = configDetail.getContent();
            Properties prop = CommonHelper.parseString2Properties(encryptOldConf);
            // 合并 (permissions, whiteList, whiteListInput)
            CommonHelper.mergePermissions(prop, whiteListInput);

            //field 'version' increase one
            //HelpUtil.increaseVersionInProperties(prop);

            String text = CommonHelper.parseProperties2String(prop);
            configDetail.setVersion(-2);    //修改
            configDetail.setContent(text);
            cd = configDetail;
        } else {
            //此(dataId, profile)不存在, 忽略
        }
        return cd;
    }

    // build debug info
    private String buildDebugInfo(List<ConfigDetail> updateCdList) {
        StringBuilder sb = new StringBuilder("updateCdList=[");
        if(updateCdList != null) {
            boolean isFirst = true;
            for (ConfigDetail cd : updateCdList) {
                ConfigField cf = cd.getConfigField();
                String dataId = cf.getDataId();
                String pf = cf.getProfile();
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(", ");
                }
                sb.append("(").append(dataId).append(",").append(pf).append(")");
            }
        }
        sb.append("]");
        return sb.toString();
    }



}
