package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class JavaDirectoryPreparerProcessor {
    public void process(CodeGenContext codeGenCtx) throws Exception {
        JavaCodeGenContext ctx = (JavaCodeGenContext) codeGenCtx;
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
        } catch (IOException e) {
            throw e;
        }
    }

}
