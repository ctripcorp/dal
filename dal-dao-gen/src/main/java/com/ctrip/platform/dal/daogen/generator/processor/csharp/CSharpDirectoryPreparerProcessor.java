package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class CSharpDirectoryPreparerProcessor implements DalProcessor {
    @Override
    public void process(CodeGenContext context) throws Exception {
        CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
        int projectId = ctx.getProjectId();
        boolean regenerate = ctx.isRegenerate();
        File mavenLikeDir = new File(String.format("%s/%s/cs", ctx.getGeneratePath(), projectId));

        try {
            if (mavenLikeDir.exists() && regenerate) {
                FileUtils.forceDelete(mavenLikeDir);
            }

            File idaoDir = new File(mavenLikeDir, "IDao");
            File daoDir = new File(mavenLikeDir, "Dao");
            File entityDir = new File(mavenLikeDir, "Entity");
            File testDir = new File(mavenLikeDir, "Test");
            File configDir = new File(mavenLikeDir, "Config");

            if (!idaoDir.exists()) {
                FileUtils.forceMkdir(idaoDir);
            }
            if (!daoDir.exists()) {
                FileUtils.forceMkdir(daoDir);
            }
            if (!entityDir.exists()) {
                FileUtils.forceMkdir(entityDir);
            }
            if (!testDir.exists()) {
                FileUtils.forceMkdir(testDir);
            }
            if (!configDir.exists()) {
                FileUtils.forceMkdir(configDir);
            }
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

}
