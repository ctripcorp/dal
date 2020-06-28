package com.ctrip.platform.dal.daogen.util;

import com.ctrip.framework.foundation.Foundation;
import com.ctrip.platform.dal.daogen.DalDynamicDSDao;
import com.ctrip.platform.dal.daogen.config.MonitorConfigManager;
import com.ctrip.platform.dal.daogen.entity.CheckTimeRange;
import com.ctrip.soa.platform.basesystem.emailservice.v1.*;
import com.dianping.cat.Cat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import qunar.tc.qconfig.client.MapConfig;

import java.io.File;
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
    private static final String EXCEL_NAME = "unUsedDynamicDSTitanKey.xls";

    public static void sendEmail(String content, String subject, String recipientStr, String cCStr, String attachmentPath) {
        String ip = IPUtils.getExecuteIPFromQConfig();
        if (!IPUtils.getLocalHostIp().equalsIgnoreCase(ip)) {
            return;
        }
        EmailServiceClient client = EmailServiceClient.getInstance();
        SendEmailRequest sendEmailRequest = new SendEmailRequest();
        //is need attachment
        List<Attachment> targetAttachmentList = null;
        if (StringUtils.isNotBlank(attachmentPath)) {
            try {
                UploadAttachmentRequest attachmentRequest = new UploadAttachmentRequest();
                Attachment attachment = new Attachment();
                attachment.setAttachmentName(EXCEL_NAME);
                attachment.setAttachmentContent(FileUtils.readFileToByteArray(new File(attachmentPath)));
                List<Attachment> attachmentList = new ArrayList<>();
                attachmentList.add(attachment);
                attachmentRequest.setAttachmentList(attachmentList);
                UploadAttachmentResponse attachmentResponse = client.uploadAttachment(attachmentRequest);
                targetAttachmentList = attachmentResponse.getAttachmentList();
            } catch (Exception e) {
                Cat.logError(e);
            }
        }

        sendEmailRequest.setAppID(getLocalAppID());
        sendEmailRequest.setBodyTemplateID(28050002);
        sendEmailRequest.setCharset("GB2312");
        sendEmailRequest.setIsBodyHtml(true);
        sendEmailRequest.setOrderID(0);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,2);
        sendEmailRequest.setExpiredTime(calendar);
        sendEmailRequest.setSendCode("28050002");
        sendEmailRequest.setSender(MonitorConfigManager.getMonitorConfig().getSender());
        sendEmailRequest.setSubject(subject);
        sendEmailRequest.setBodyContent(content);
        if (targetAttachmentList != null) {
            sendEmailRequest.setAttachmentList(targetAttachmentList);
        }

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
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            Cat.logError("send email fail!", e);
        }
    }

    public static int getLocalAppID() {
        return Integer.parseInt(Foundation.app().getAppId());
    }
}
