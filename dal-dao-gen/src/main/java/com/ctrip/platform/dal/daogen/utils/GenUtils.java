package com.ctrip.platform.dal.daogen.utils;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public final class GenUtils {

	/**
	 * 组建最基本的VelocityContext
	 * @return
	 */
	public static VelocityContext buildDefaultVelocityContext() {
		VelocityContext context = new VelocityContext();
		context.put("WordUtils", WordUtils.class);
		context.put("StringUtils", StringUtils.class);

		return context;
	}

	/**
	 * 根据Velocity模板，生成相应的文件
	 * @param context VelocityHost
	 * @param resultFilePath 生成的文件路径
	 * @param templateFile Velocity模板文件 
	 * @return
	 */
	public static boolean mergeVelocityContext(VelocityContext context,
			String resultFilePath, String templateFile) {
		FileWriter daoWriter = null;
		try {
			daoWriter = new FileWriter(resultFilePath);
			Velocity.mergeTemplate(templateFile, "UTF-8", context, daoWriter);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			JavaIOUtils.closeWriter(daoWriter);
		}

		return true;
	}

}
