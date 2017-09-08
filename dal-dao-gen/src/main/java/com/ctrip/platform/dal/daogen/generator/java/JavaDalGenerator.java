package com.ctrip.platform.dal.daogen.generator.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.generator.processor.java.*;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;

import java.util.ArrayList;
import java.util.List;

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
    public void prepareDirectory(CodeGenContext context) throws Exception {
        try {
            JavaCodeGenContext ctx = (JavaCodeGenContext) context;
            LoggerManager.getInstance()
                    .info(String.format("Begin to prepare java directory for project %s", ctx.getProjectId()));
            new JavaDirectoryPreparerProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Prepare java directory for project %s completed.", ctx.getProjectId()));
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @Override
    public void prepareData(CodeGenContext context) throws Exception {
        List<String> exceptions = new ArrayList<>();
        JavaCodeGenContext ctx = (JavaCodeGenContext) context;
        try {
            LoggerManager.getInstance()
                    .info(String.format("Begin to prepare java table data for project %s", ctx.getProjectId()));
            new JavaDataPreparerOfTableViewSpProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Prepare java table data for project %s completed.", ctx.getProjectId()));
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            exceptions.add(e.getMessage());
        }

        try {
            LoggerManager.getInstance()
                    .info(String.format("Begin to prepare java sqlbuilder data for project %s", ctx.getProjectId()));
            new JavaDataPreparerOfSqlBuilderProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Prepare java sqlbuilder data for project %s completed.", ctx.getProjectId()));
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            exceptions.add(e.getMessage());
        }

        try {
            LoggerManager.getInstance()
                    .info(String.format("Begin to prepare java freesql data for project %s", ctx.getProjectId()));
            new JavaDataPreparerOfFreeSqlProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Prepare java freesql data for project %s completed.", ctx.getProjectId()));
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            exceptions.add(e.getMessage());
        }

        if (exceptions.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String exception : exceptions) {
                sb.append(exception);
            }

            throw new RuntimeException(sb.toString());
        }
    }

    @Override
    public void generateCode(CodeGenContext context) throws Exception {
        JavaCodeGenContext ctx = (JavaCodeGenContext) context;
        try {
            LoggerManager.getInstance()
                    .info(String.format("Begin to generate java table code for project %s", ctx.getProjectId()));
            new JavaCodeGeneratorOfTableProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Generate java table code for project %s completed.", ctx.getProjectId()));

            LoggerManager.getInstance()
                    .info(String.format("Begin to generate java view code for project %s", ctx.getProjectId()));
            new JavaCodeGeneratorOfViewProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Generate java view code for project %s completed.", ctx.getProjectId()));

            LoggerManager.getInstance()
                    .info(String.format("Begin to generate java sp code for project %s", ctx.getProjectId()));
            new JavaCodeGeneratorOfSpProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Generate java sp code for project %s completed.", ctx.getProjectId()));

            LoggerManager.getInstance()
                    .info(String.format("Begin to generate java freesql code for project %s", ctx.getProjectId()));
            new JavaCodeGeneratorOfFreeSqlProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Generate java freesql code for project %s completed.", ctx.getProjectId()));

            LoggerManager.getInstance()
                    .info(String.format("Begin to generate java other code for project %s", ctx.getProjectId()));
            new JavaCodeGeneratorOfOthersProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Generate java other code for project %s completed.", ctx.getProjectId()));
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

}
