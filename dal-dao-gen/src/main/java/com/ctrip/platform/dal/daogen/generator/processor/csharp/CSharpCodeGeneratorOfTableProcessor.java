package com.ctrip.platform.dal.daogen.generator.processor.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.generator.csharp.CSharpCodeGenContext;
import com.ctrip.platform.dal.daogen.host.csharp.CSharpTableHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.resource.ProgressResource;
import com.ctrip.platform.dal.daogen.utils.GenUtils;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;

public class CSharpCodeGeneratorOfTableProcessor implements DalProcessor {
    @Override
    public void process(CodeGenContext context) throws Exception {
        try {
            CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
            int projectId = ctx.getProjectId();
            Progress progress = ctx.getProgress();
            final File dir = new File(String.format("%s/%s/cs", ctx.getGeneratePath(), projectId));
            List<Callable<ExecuteResult>> tableCallables = generateTableDao(ctx, dir);
            TaskUtils.invokeBatch(tableCallables);
            ProgressResource.addDoneFiles(progress, ctx.getTableViewHosts().size());
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    private List<Callable<ExecuteResult>> generateTableDao(CodeGenContext codeGenCtx, final File mavenLikeDir) {
        final CSharpCodeGenContext ctx = (CSharpCodeGenContext) codeGenCtx;
        final Progress progress = ctx.getProgress();
        List<Callable<ExecuteResult>> results = new ArrayList<>();
        Queue<CSharpTableHost> _tableViewHosts = ctx.getTableViewHosts();

        for (final CSharpTableHost host : _tableViewHosts) {
            Callable<ExecuteResult> worker = new Callable<ExecuteResult>() {
                @Override
                public ExecuteResult call() throws Exception {
                    // progress.setOtherMessage("正在生成 " + host.getClassName());
                    ExecuteResult result = new ExecuteResult("Generate Table[" + host.getTableName() + "] Dao");
                    progress.setOtherMessage(result.getTaskName());
                    try {
                        VelocityContext context = GenUtils.buildDefaultVelocityContext();
                        context.put("host", host);
                        GenUtils.mergeVelocityContext(context,
                                String.format("%s/Dao/%sDao.cs", mavenLikeDir.getAbsolutePath(), host.getClassName()),
                                "templates/csharp/dao/standard/DAO.cs.tpl");
                        GenUtils.mergeVelocityContext(context,
                                String.format("%s/Entity/%s.cs", mavenLikeDir.getAbsolutePath(), host.getClassName()),
                                ctx.isNewPojo() ? "templates/csharp/PojoNew.cs.tpl" : "templates/csharp/Pojo.cs.tpl");
                        GenUtils.mergeVelocityContext(context,
                                String.format("%s/IDao/I%sDao.cs", mavenLikeDir.getAbsolutePath(), host.getClassName()),
                                "templates/csharp/dao/standard/IDAO.cs.tpl");
                        GenUtils.mergeVelocityContext(context,
                                String.format("%s/Test/%sTest.cs", mavenLikeDir.getAbsolutePath(), host.getClassName()),
                                "templates/csharp/test/DAOTest.cs.tpl");
                        GenUtils.mergeVelocityContext(context, String.format("%s/Test/%sUnitTest.cs",
                                mavenLikeDir.getAbsolutePath(), host.getClassName()),
                                "templates/csharp/test/DAOUnitTest.cs.tpl");
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
