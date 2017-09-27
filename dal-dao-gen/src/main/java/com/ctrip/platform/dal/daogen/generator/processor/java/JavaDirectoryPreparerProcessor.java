package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class JavaDirectoryPreparerProcessor {
    public void process(CodeGenContext context) throws Exception {
        JavaCodeGenContext ctx = (JavaCodeGenContext) context;
        File dir = new File(String.format("%s/%s/java", ctx.getGeneratePath(), ctx.getProjectId()));

        try {
            if (dir.exists() && ctx.isRegenerate())
                FileUtils.forceDelete(dir);

            File daoDir = new File(dir, "Dao");
            File entityDir = new File(dir, "Entity");
            File testDir = new File(dir, "Test");

            if (!daoDir.exists()) {
                FileUtils.forceMkdir(daoDir);
            }
            if (!entityDir.exists()) {
                FileUtils.forceMkdir(entityDir);
            }
            if (!testDir.exists()) {
                FileUtils.forceMkdir(testDir);
            }
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

}
