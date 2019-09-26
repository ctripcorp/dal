package com.ctrip.platform.dal.daogen.util;

import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.config.MonitorConfigManager;
import com.ctrip.platform.dal.daogen.entity.CheckTimeRange;
import com.ctrip.soa.platform.basesystem.emailservice.v1.EmailServiceClient;
import com.ctrip.soa.platform.basesystem.emailservice.v1.SendEmailRequest;
import com.ctrip.soa.platform.basesystem.emailservice.v1.SendEmailResponse;
import com.dianping.cat.Cat;
import org.apache.commons.lang.StringUtils;
import qunar.tc.qconfig.client.MapConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by taochen on 2019/7/26.
 */
public class EmailUtils {
    private static final String APP_PROPERTIES_CLASSPATH = "/META-INF/app.properties";

    public static void sendEmail(String content, String subject, String recipientStr, String cCStr) {
        String ip = IPUtils.getExecuteIPFromQConfig();
        if (!IPUtils.getLocalHostIp().equalsIgnoreCase(ip)) {
            return;
        }
        EmailServiceClient client = EmailServiceClient.getInstance();
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        sendEmailRequest.setAppID(getLocalAppID());
        sendEmailRequest.setBodyTemplateID(28030004);
        sendEmailRequest.setCharset("GB2312");
        sendEmailRequest.setIsBodyHtml(true);
        sendEmailRequest.setOrderID(0);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,2);
        sendEmailRequest.setExpiredTime(calendar);
        sendEmailRequest.setSendCode("28030004");
        sendEmailRequest.setSender(MonitorConfigManager.getMonitorConfig().getSender());
        sendEmailRequest.setSubject(subject);
        sendEmailRequest.setBodyContent(content);

        if (StringUtils.isNotBlank(recipientStr)) {
            String[] recipientArray = recipientStr.split(",");
            List<String> recipient = new ArrayList<>();
            Collections.addAll(recipient, recipientArray);
            sendEmailRequest.setRecipient(recipient);
        }

        if (StringUtils.isNotBlank(cCStr)) {
            String[] ccArray = cCStr.split(",");
            List<String> cc = new ArrayList<>();
            Collections.addAll(cc, ccArray);
            sendEmailRequest.setCc(cc);
        }
        try {
            SendEmailResponse response = client.sendEmail(sendEmailRequest);
            if (response != null && response.getResultCode() == 1) {
                return;
            }
            else {
                throw new Exception();
            }
        }catch (Exception e) {
            Cat.logError("send email fail!" , e);
        }
    }

    public static int getLocalAppID() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_CLASSPATH);
        Properties m_appProperties = new Properties();
        if (in == null) {
            in = DalDynamicDSDao.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
        }
        try {
            m_appProperties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            Cat.logError("get local appID fail!", e);
        }
        return Integer.valueOf(m_appProperties.getProperty("app.id"));
    }
}
