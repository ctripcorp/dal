package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;

public class CSharpCodeGeneratorOfOthersProcessor implements DalProcessor {

    @Override
    public void process(CodeGenContext context) throws Exception {
        try {
            generateCommonCode(context);
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    /**
     * 生成C#的公共部分，如Dal.config，Program.cs以及DALFactory.cs
     */
    private void generateCommonCode(CodeGenContext codeGenCtx) throws Exception {
        CSharpCodeGenContext ctx = (CSharpCodeGenContext) codeGenCtx;
        final int id = ctx.getProjectId();
        final VelocityContext context = GenUtils.buildDefaultVelocityContext();
        final File csMavenLikeDir = new File(String.format("%s/%s/cs", ctx.getGeneratePath(), id));
        context.put("host", ctx.getDalConfigHost());
        context.put("dbs", ctx.getDbHosts().values());
        context.put("namespace", ctx.getNamespace());
        context.put("freeSqlHosts", ctx.getFreeDaos());
        context.put("tableHosts", ctx.getTableDaos());
        context.put("spHosts", ctx.getSpDaos());

        GenUtils.mergeVelocityContext(context,
                String.format("%s/Config/Dal.config.tpl", csMavenLikeDir.getAbsolutePath()),
                "templates/csharp/Dal.config.cs.tpl");
        GenUtils.mergeVelocityContext(context, String.format("%s/DalFactory.cs", csMavenLikeDir.getAbsolutePath()),
                "templates/csharp/DalFactory.cs.tpl");
    }

}
