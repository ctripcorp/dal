package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;

public class CtripJavaCodeGeneratorOfOthersProcessor implements DalProcessor {
    @Override
    public void process(CodeGenContext context) throws Exception {
        JavaCodeGenContext ctx = (JavaCodeGenContext) context;
        int projectId = ctx.getProjectId();
        File dir = new File(String.format("%s/%s/java", ctx.getGeneratePath(), projectId));

        VelocityContext vltCcontext = GenUtils.buildDefaultVelocityContext();
        vltCcontext.put("host", ctx.getDalConfigHost());
        GenUtils.mergeVelocityContext(vltCcontext, String.format("%s/Dal.config", dir.getAbsolutePath()),
                "templates/java/Dal.config.java.tpl");

        GenUtils.mergeVelocityContext(vltCcontext, String.format("%s/Database.Config", dir.getAbsolutePath()),
                "templates/java/Database.config.java.tpl");
    }
}
