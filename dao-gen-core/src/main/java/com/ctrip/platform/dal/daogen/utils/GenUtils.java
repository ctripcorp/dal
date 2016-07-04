package com.ctrip.platform.dal.daogen.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public final class GenUtils {
    private static Logger log;

    static {
        log = Logger.getLogger(GenUtils.class);
        java.util.Properties property = new java.util.Properties();
        property.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute");
        property.setProperty(VelocityEngine.RESOURCE_LOADER, "class");
        property.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(property);
    }

    /**
     * 组建最基本的VelocityContext
     *
     * @return
     */
    public static VelocityContext buildDefaultVelocityContext() {
        VelocityContext context = new VelocityContext();
        context.put("WordUtils", WordUtils.class);
        context.put("StringUtils", StringUtils.class);
        context.put("helper", VelocityHelper.class);

        return context;
    }

    /**
     * 根据Velocity模板，生成相应的文件
     *
     * @param context        VelocityHost
     * @param resultFilePath 生成的文件路径
     * @param templateFile   Velocity模板文件
     * @return
     */
    public static boolean mergeVelocityContext(VelocityContext context, String resultFilePath, String templateFile) {
        FileWriter daoWriter = null;
        try {
            daoWriter = new FileWriter(resultFilePath);
            Velocity.mergeTemplate(templateFile, "UTF-8", context, daoWriter);
        } catch (IOException e) {
            log.error(String.format("merge velocity context error: [context=%s;resultFilePath=%s;templateFile=%s]", CommonUtils.toJson(context.get("host")), resultFilePath, templateFile), e);
            return false;
        } finally {
            JavaIOUtils.closeWriter(daoWriter);
        }

        return true;
    }

    /**
     * 根据Velocity模板，生成相应的文件
     *
     * @param context        VelocityHost
     * @param resultFilePath 生成的文件路径
     * @param templateFile   Velocity模板文件
     * @return
     */
    public static String mergeVelocityContext(VelocityContext context, String templateFile) {
        Writer writer = null;
        try {
            writer = new StringWriter();
            Velocity.mergeTemplate(templateFile, "UTF-8", context, writer);
        } finally {
            JavaIOUtils.closeWriter(writer);
        }

        return writer.toString();
    }

}
