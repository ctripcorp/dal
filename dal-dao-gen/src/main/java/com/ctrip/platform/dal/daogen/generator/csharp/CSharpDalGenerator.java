package com.ctrip.platform.dal.daogen.generator.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.*;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            ctx.setProjectName(project.getName());
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
        return ctx;
    }

    @Override
    public void prepareDirectory(CodeGenContext context) throws Exception {
        try {
            CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
            LoggerManager.getInstance()
                    .info(String.format("Begin to prepare csharp directory for project %s", ctx.getProjectId()));
            new CSharpDirectoryPreparerProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Prepare csharp directory for project %s completed.", ctx.getProjectId()));
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

    @Override
    public void prepareData(CodeGenContext context) throws Exception {
        List<String> exceptions = new ArrayList<>();
        CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
        try {
            LoggerManager.getInstance()
                    .info(String.format("Begin to prepare csharp table data for project %s", ctx.getProjectId()));
            new CSharpDataPreparerOfTableViewSpProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Prepare csharp table data for project %s completed.", ctx.getProjectId()));
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            exceptions.add(e.getMessage());
        }

        try {
            LoggerManager.getInstance()
                    .info(String.format("Begin to prepare csharp sqlbuilder data for project %s", ctx.getProjectId()));
            new CSharpDataPreparerOfSqlBuilderProcessor().process(ctx);
            LoggerManager.getInstance().info(
                    String.format("Prepare csharp sqlbuilder data for project %s completed.", ctx.getProjectId()));
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            exceptions.add(e.getMessage());
        }

        try {
            LoggerManager.getInstance()
                    .info(String.format("Begin to prepare csharp freesql data for project %s", ctx.getProjectId()));
            new CSharpDataPreparerOfFreeSqlProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Prepare csharp freesql data for project %s completed.", ctx.getProjectId()));
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
        CSharpCodeGenContext ctx = (CSharpCodeGenContext) context;
        try {
            LoggerManager.getInstance()
                    .info(String.format("Begin to generate csharp table code for project %s", ctx.getProjectId()));
            new CSharpCodeGeneratorOfTableProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Generate csharp table code for project %s completed.", ctx.getProjectId()));

            LoggerManager.getInstance()
                    .info(String.format("Begin to generate csharp sp code for project %s", ctx.getProjectId()));
            new CSharpCodeGeneratorOfSpProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Generate csharp sp code for project %s completed.", ctx.getProjectId()));

            LoggerManager.getInstance()
                    .info(String.format("Begin to generate csharp freesql code for project %s", ctx.getProjectId()));
            new CSharpCodeGeneratorOfFreeSqlProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Generate csharp freesql code for project %s completed.", ctx.getProjectId()));

            LoggerManager.getInstance()
                    .info(String.format("Begin to generate csharp other code for project %s", ctx.getProjectId()));
            new CSharpCodeGeneratorOfOthersProcessor().process(ctx);
            LoggerManager.getInstance()
                    .info(String.format("Generate csharp other code for project %s completed.", ctx.getProjectId()));
        } catch (Exception e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }

}
