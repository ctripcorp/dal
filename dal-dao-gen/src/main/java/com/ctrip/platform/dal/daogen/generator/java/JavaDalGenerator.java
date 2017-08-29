package com.ctrip.platform.dal.daogen.generator.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.entity.ExecuteResult;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.generator.processor.java.*;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.TaskUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class JavaDalGenerator implements DalGenerator {
    @Override
    public CodeGenContext createContext(int projectId, boolean regenerate, Progress progress, boolean newPojo,
            boolean ignoreApproveStatus) throws Exception {
        JavaCodeGenContext ctx = null;
        try {
            ctx = new JavaCodeGenContext(projectId, regenerate, progress);
            Project project = BeanGetter.getDaoOfProject().getProjectByID(projectId);
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
            new JavaDirectoryPreparerProcessor().process(codeGenCtx);
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @Override
    public void prepareData(CodeGenContext context) throws Exception {
        try {
            List<Callable<ExecuteResult>> tasks = new ArrayList<>();
            tasks.addAll(new JavaDataPreparerOfTableViewSpProcessor().prepareTableViewSp(context));
            tasks.addAll(new JavaDataPreparerOfSqlBuilderProcessor().prepareSqlBuilder(context));
            tasks.addAll(new JavaDataPreparerOfFreeSqlProcessor().prepareFreeSql(context));
            TaskUtils.invokeBatch(tasks);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void generateCode(CodeGenContext codeGenCtx) throws Exception {
        try {
            new JavaCodeGeneratorOfTableProcessor().process(codeGenCtx);
            new JavaCodeGeneratorOfViewProcessor().process(codeGenCtx);
            new JavaCodeGeneratorOfSpProcessor().process(codeGenCtx);
            new JavaCodeGeneratorOfFreeSqlProcessor().process(codeGenCtx);
            new JavaCodeGeneratorOfOthersProcessor().process(codeGenCtx);
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }
}
