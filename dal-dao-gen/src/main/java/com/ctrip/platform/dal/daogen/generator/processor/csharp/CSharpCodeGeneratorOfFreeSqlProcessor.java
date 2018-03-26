package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlHost;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpFreeSqlPojoHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.resource.ProgressResource;
import com.ctrip.platform.dal.daogen.utils.CommonUtils;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

public class CSharpCodeGeneratorOfFreeSqlProcessor implements DalProcessor {
    @Override
    public void process(CodeGenContext context) throws Exception {
        try {
            CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
            int projectId = ctx.getProjectId();
            final File dir = new File(String.format("%s/%s/cs", ctx.getGeneratePath(), projectId));
            List<Callable<ExecuteResult>> freeCallables = generateFreeSqlDao(ctx, dir);
            TaskUtils.invokeBatch(freeCallables);
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    private List<Callable<ExecuteResult>> generateFreeSqlDao(CodeGenContext codeGenCtx, final File mavenLikeDir) {
        final CSharpCodeGenContext ctx = (CSharpCodeGenContext) codeGenCtx;
        final Progress progress = ctx.getProgress();
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        Map<String, CSharpFreeSqlPojoHost> _freeSqlPojoHosts = ctx.getFreeSqlPojoHosts();

        for (final CSharpFreeSqlPojoHost host : _freeSqlPojoHosts.values()) {
            Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {
                    // progress.setOtherMessage("正在生成 " + host.getClassName());
                    ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getClassName() + "] Pojo");
                    progress.setOtherMessage(result.getTaskName());
                    try {
                        VelocityContext context = GenUtils.buildDefaultVelocityContext();
                        context.put("host", host);
                        GenUtils.mergeVelocityContext(context,
                                String.format("%s/Entity/%s.cs", mavenLikeDir.getAbsolutePath(),
                                        CommonUtils.normalizeVariable(host.getClassName())),
                                ctx.isNewPojo() ? "templates/csharp/PojoNew.cs.tpl" : "templates/csharp/Pojo.cs.tpl");
                        result.setSuccessal(true);
                    } catch (Throwable e) {
                        throw e;
                    }
                    return result;
                }
            };
            results.add(worker);
        }
        ProgressResource.addDoneFiles(progress, _freeSqlPojoHosts.size());

        Queue<CSharpFreeSqlHost> _freeSqlHosts = ctx.getFreeSqlHosts();
        for (final CSharpFreeSqlHost host : _freeSqlHosts) {
            Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {
                    // progress.setOtherMessage("正在生成 " + host.getClassName());
                    ExecuteResult result =
                            new ExecuteResult("Generate Free SQL[" + host.getClassName() + "] Dap, Test");
                    progress.setOtherMessage(result.getTaskName());
                    try {
                        VelocityContext context = GenUtils.buildDefaultVelocityContext();
                        context.put("host", host);
                        GenUtils.mergeVelocityContext(context,
                                String.format("%s/Dao/%sDao.cs", mavenLikeDir.getAbsolutePath(),
                                        CommonUtils.normalizeVariable(host.getClassName())),
                                "templates/csharp/dao/freesql/FreeSqlDAO.cs.tpl");
                        GenUtils.mergeVelocityContext(context,
                                String.format("%s/Test/%sTest.cs", mavenLikeDir.getAbsolutePath(),
                                        CommonUtils.normalizeVariable(host.getClassName())),
                                "templates/csharp/test/FreeSqlTest.cs.tpl");
                        GenUtils.mergeVelocityContext(context,
                                String.format("%s/Test/%sUnitTest.cs", mavenLikeDir.getAbsolutePath(),
                                        CommonUtils.normalizeVariable(host.getClassName())),
                                "templates/csharp/test/FreeSqlUnitTest.cs.tpl");
                        result.setSuccessal(true);
                    } catch (Throwable e) {
                        throw e;
                    }
                    return result;
                }
            };
            results.add(worker);
        }

        return results;
    }


}
