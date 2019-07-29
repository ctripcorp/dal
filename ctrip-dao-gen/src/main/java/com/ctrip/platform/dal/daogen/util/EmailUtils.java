package com.ctrip.platform.dal.daogen.util;

import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.entity.CheckTimeRange;
import com.ctrip.soa.platform.basesystem.emailservice.v1.EmailServiceClient;
import com.ctrip.soa.platform.basesystem.emailservice.v1.SendEmailRequest;
import com.ctrip.soa.platform.basesystem.emailservice.v1.SendEmailResponse;
import com.dianping.cat.Cat;

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

    public static void sendEmail(String content, Date checkDate, CheckTimeRange checkTimeRange) {
        String startCheckTime = null;
        String endCheckTime = null;
        if (CheckTimeRange.ONE_WEEK.equals(checkTimeRange)) {
            startCheckTime = DateUtils.getStartOneWeek(checkDate);
            endCheckTime = DateUtils.getEndOneWeek(checkDate);
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
        sendEmailRequest.setSender("taochen@ctrip.com");
        sendEmailRequest.setSubject(String.format("%s-%s动态数据源切换统计",startCheckTime, endCheckTime));
        sendEmailRequest.setBodyContent(content);
        List<String> recipient = new ArrayList<>();
        recipient.add("taochen@ctrip.com");
        sendEmailRequest.setRecipient(recipient);
        try {
            SendEmailResponse response = client.sendEmail(sendEmailRequest);
            if (response != null && response.getResultCode() == 1) {
                return;
            }
            else {
                throw new Exception();
            }
        }catch (Exception e) {
            Cat.logError("send email fail, time:" + checkDate.toString(), e);
        }
    }

    private static int getLocalAppID() {
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
