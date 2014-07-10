package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;

public class JavaDirectoryPreparerProcessor implements DalProcessor{

	@Override
	public void process(CodeGenContext context) throws Exception {
		JavaCodeGenContext ctx = (JavaCodeGenContext)context;
		int projectId = ctx.getProjectId();
		boolean regenerate = ctx.isRegenerate();
		File dir = new File(String.format("%s/%s/java", CodeGenContext.generatePath, projectId));
		if (dir.exists() && regenerate)
			FileUtils.forceDelete(dir);

		File daoMavenLike = new File(dir, "Dao");
		File entityMavenLike = new File(dir, "Entity");
		File testMavenLike = new File(dir, "Test");

		if (!daoMavenLike.exists()) {
			FileUtils.forceMkdir(daoMavenLike);
		}
		if (!entityMavenLike.exists()) {
			FileUtils.forceMkdir(entityMavenLike);
		}
		if (!testMavenLike.exists()) {
			FileUtils.forceMkdir(testMavenLike);
		}
	}

}
