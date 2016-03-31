package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpTableHost;
import com.ctrip.platform.dal.daogen.resource.ProgressResource;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

public class CSharpCodeGeneratorOfSpProcessor implements DalProcessor {
    private static Logger log = Logger.getLogger(CSharpCodeGeneratorOfSpProcessor.class);

    @Override
    public void process(CodeGenContext context) throws Exception {
        CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
        int projectId = ctx.getProjectId();
        Progress progress = ctx.getProgress();
        final File dir = new File(String.format("%s/%s/cs", ctx.getGeneratePath(), projectId));
        List<Callable<ExecuteResult>> spCallables = generateSpDao(ctx, dir);
        TaskUtils.invokeBatch(log, spCallables);
        ProgressResource.addDoneFiles(progress, ctx.getSpHosts().size());
    }

    private List<Callable<ExecuteResult>> generateSpDao(CodeGenContext codeGenCtx, final File mavenLikeDir) {
        final CSharpCodeGenContext ctx = (CSharpCodeGenContext) codeGenCtx;
        final Progress progress = ctx.getProgress();
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        Queue<CSharpTableHost> _spHosts = ctx.getSpHosts();

        for (final CSharpTableHost host : _spHosts) {
            Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() {
                    //progress.setOtherMessage("正在生成 " + host.getClassName());
                    ExecuteResult result = new ExecuteResult("Generate SP[" + host.getClassName() + "] Dao");
                    progress.setOtherMessage(result.getTaskName());
                    try {
                        VelocityContext context = GenUtils.buildDefaultVelocityContext();
                        context.put("host", host);
                        GenUtils.mergeVelocityContext(context, String.format("%s/Dao/%sDao.cs", mavenLikeDir.getAbsolutePath(), host.getClassName()), "templates/csharp/DAOBySp.cs.tpl");
                        GenUtils.mergeVelocityContext(context, String.format("%s/Entity/%s.cs", mavenLikeDir.getAbsolutePath(), host.getClassName()), "templates/csharp/PojoBySp.cs.tpl");
                        GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sTest.cs", mavenLikeDir.getAbsolutePath(), host.getClassName()), "templates/csharp/test/SpTest.cs.tpl");
                        GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sUnitTest.cs", mavenLikeDir.getAbsolutePath(), host.getClassName()), "templates/csharp/test/SpUnitTest.cs.tpl");
                        result.setSuccessal(true);
                    } catch (Exception e) {
                        log.error(result.getTaskName() + "exception", e);
                    }
                    return result;
                }
            };
            results.add(worker);
        }

        return results;
    }

}
