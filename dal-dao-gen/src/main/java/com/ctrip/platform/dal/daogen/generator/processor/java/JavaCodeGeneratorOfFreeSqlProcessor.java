package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.java.JavaCodeGenContext;
import com.ctrip.platform.dal.daogen.host.java.FreeSqlHost;
import com.ctrip.platform.dal.daogen.host.java.JavaMethodHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

public class JavaCodeGeneratorOfFreeSqlProcessor implements DalProcessor {

    public void process(CodeGenContext context) throws Exception {
        try {
            JavaCodeGenContext ctx = (JavaCodeGenContext) context;
            int projectId = ctx.getProjectId();
            File dir = new File(String.format("%s/%s/java", ctx.getGeneratePath(), projectId));
            List<Callable<ExecuteResult>> freeCallables = generateFreeSqlDao(ctx, dir);
            TaskUtils.invokeBatch(freeCallables);
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    private List<Callable<ExecuteResult>> generateFreeSqlDao(CodeGenContext codeGenCtx, final File mavenLikeDir) {
        JavaCodeGenContext ctx = (JavaCodeGenContext) codeGenCtx;
        final Progress progress = ctx.getProgress();
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        Map<String, JavaMethodHost> freeSqlPojoHosts = ctx.get_freeSqlPojoHosts();
        for (final JavaMethodHost host : freeSqlPojoHosts.values()) {
            if (host.isSampleType())
                continue;
            Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {
                    ExecuteResult result = new ExecuteResult("Generate Free SQL[" + host.getPojoClassName() + "] Pojo");
                    progress.setOtherMessage(result.getTaskName());
                    try {
                        VelocityContext context = GenUtils.buildDefaultVelocityContext();
                        context.put("host", host);

                        GenUtils.mergeVelocityContext(context, String.format("%s/Entity/%s.java",
                                mavenLikeDir.getAbsolutePath(), host.getPojoClassName()),
                                "templates/java/Pojo.java.tpl");
                        result.setSuccessal(true);
                    } catch (Throwable e) {
                        throw e;
                    }
                    return result;
                }
            };
            results.add(worker);
        }

        Queue<FreeSqlHost> freeSqlHosts = ctx.getFreeSqlHosts();
        for (final FreeSqlHost host : freeSqlHosts) {
            Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {

                @Override
                public ExecuteResult call() throws Exception {
                    ExecuteResult result =
                            new ExecuteResult("Generate Free SQL[" + host.getClassName() + "] Dap, Test");
                    progress.setOtherMessage(result.getTaskName());
                    try {
                        VelocityContext context = GenUtils.buildDefaultVelocityContext();
                        context.put("host", host);
                        GenUtils.mergeVelocityContext(context,
                                String.format("%s/Dao/%sDao.java", mavenLikeDir.getAbsolutePath(), host.getClassName()),
                                "templates/java/dao/freesql/FreeSqlDAO.java.tpl");
                        GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sDaoUnitTest.java",
                                mavenLikeDir.getAbsolutePath(), host.getClassName()),
                                "templates/java/test/FreeSqlDaoUnitTest.java.tpl");

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
