package com.ctrip.platform.dal.daogen.generator.processor.java;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class JavaDirectoryPreparerProcessor implements Processor{

	@Override
	public void process(Context context) {
		JavaCodeGenContext ctx = (JavaCodeGenContext)context;
		int projectId = ctx.getProjectId();
		boolean regenerate = ctx.isRegenerate();
		File dir = new File(String.format("%s/%s/java", CodeGenContext.generatePath, projectId));
		try {
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
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
