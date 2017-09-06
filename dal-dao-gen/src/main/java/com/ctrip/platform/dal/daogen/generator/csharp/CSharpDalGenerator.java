package com.ctrip.platform.dal.daogen.generator.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.*;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class CSharpDalGenerator implements DalGenerator {
    @Override
    public CodeGenContext createContext(int projectId, boolean regenerate, Progress progress, boolean newPojo,
            boolean ignoreApproveStatus) throws Exception {
        CSharpCodeGenContext ctx = null;
        try {
            Map<String, Boolean> hints = new HashMap<>();
            hints.put("newPojo", newPojo);
            ctx = new CSharpCodeGenContext(projectId, regenerate, progress, hints);
            ctx.setNewPojo(newPojo);
            Project project = BeanGetter.getDaoOfProject().getProjectByID(ctx.getProjectId());
            DalConfigHost dalConfigHost = null;
            if (project.getDal_config_name() != null && !project.getDal_config_name().isEmpty()) {
                dalConfigHost = new DalConfigHost(project.getDal_config_name());
            } else if (project.getNamespace() != null && !project.getNamespace().isEmpty()) {
                dalConfigHost = new DalConfigHost(project.getNamespace());
            } else {
                dalConfigHost = new DalConfigHost("");
            }
            ctx.setDalConfigHost(dalConfigHost);
            ctx.setNamespace(project.getNamespace());
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
        return ctx;
    }

    @Override
    public void prepareDirectory(CodeGenContext codeGenCtx) throws Exception {
        try {
            new CSharpDirectoryPreparerProcessor().process(codeGenCtx);
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @Override
    public void prepareData(CodeGenContext context) throws Exception {
        try {
            List<Callable<ExecuteResult>> tasks = new ArrayList<>();
            tasks.addAll(new CSharpDataPreparerOfFreeSqlProcessor().prepareFreeSql(context));
            tasks.addAll(new CSharpDataPreparerOfTableViewSpProcessor().prepareTableViewSp(context));
            tasks.addAll(new CSharpDataPreparerOfSqlBuilderProcessor().prepareSqlBuilder(context));
            TaskUtils.invokeBatch(tasks);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void generateCode(CodeGenContext codeGenCtx) throws Exception {
        try {
            new CSharpCodeGeneratorOfTableProcessor().process(codeGenCtx);
            new CSharpCodeGeneratorOfSpProcessor().process(codeGenCtx);
            new CSharpCodeGeneratorOfFreeSqlProcessor().process(codeGenCtx);
            new CSharpCodeGeneratorOfOthersProcessor().process(codeGenCtx);
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

}
