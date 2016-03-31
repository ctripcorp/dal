package com.ctrip.platform.dal.daogen.generator.csharp;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.*;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CSharpDalGenerator implements DalGenerator {
    private Logger log = Logger.getLogger(CSharpDalGenerator.class);

    @Override
    public CodeGenContext createContext(int projectId, boolean regenerate, Progress progress, boolean newPojo, boolean ignoreApproveStatus) throws Exception {
        CSharpCodeGenContext ctx = null;
        try {
            Map<String, Boolean> hints = new HashMap<>();
            hints.put("newPojo", newPojo);
            ctx = new CSharpCodeGenContext(projectId, regenerate, progress, hints);
            ctx.setNewPojo(newPojo);
            Project project = SpringBeanGetter.getDaoOfProject().getProjectByID(ctx.getProjectId());
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
            log.warn("exception occur when createContext", e);
            throw e;
        }
        return ctx;
    }

    @Override
    public void prepareDirectory(CodeGenContext codeGenCtx) throws Exception {
        try {
            new CSharpDirectoryPreparerProcessor().process(codeGenCtx);
        } catch (Exception e) {
            log.warn("exception occur when prepareDirectory", e);
            throw e;
        }
    }

    @Override
    public void prepareData(CodeGenContext codeGenCtx) throws Exception {
        try {
            new CSharpDataPreparerOfFreeSqlProcessor().process(codeGenCtx);
            new CSharpDataPreparerOfTableViewSpProcessor().process(codeGenCtx);
            new CSharpDataPreparerOfSqlBuilderProcessor().process(codeGenCtx);
        } catch (Exception e) {
            log.warn("exception occur when prepareData", e);
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
            log.warn("exception occur when generateCode", e);
            throw e;
        }
    }

}
