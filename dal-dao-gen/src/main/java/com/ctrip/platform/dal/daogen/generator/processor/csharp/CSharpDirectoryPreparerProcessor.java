package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class CSharpDirectoryPreparerProcessor implements Processor{

	@Override
	public void process(Context context) {
		CSharpCodeGenContext ctx = (CSharpCodeGenContext)context;
		int projectId = ctx.getProjectId();
		boolean regenerate = ctx.isRegenerate();
		File mavenLikeDir = new File(String.format("%s/%s/cs", CodeGenContext.generatePath, projectId));

		try {
			if (mavenLikeDir.exists() && regenerate){
				FileUtils.forceDelete(mavenLikeDir);
			}
			
			File idaoMavenLike = new File(mavenLikeDir, "IDao");
			File daoMavenLike = new File(mavenLikeDir, "Dao");
			File entityMavenLike = new File(mavenLikeDir, "Entity");
			File testMavenLike = new File(mavenLikeDir, "Test");
			File configMavenLike = new File(mavenLikeDir, "Config");

			if (!idaoMavenLike.exists()) {
				FileUtils.forceMkdir(idaoMavenLike);
			}
			if (!daoMavenLike.exists()) {
				FileUtils.forceMkdir(daoMavenLike);
			}
			if (!entityMavenLike.exists()) {
				FileUtils.forceMkdir(entityMavenLike);
			}
			if (!testMavenLike.exists()) {
				FileUtils.forceMkdir(testMavenLike);
			}
			if (!configMavenLike.exists()) {
				FileUtils.forceMkdir(configMavenLike);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
