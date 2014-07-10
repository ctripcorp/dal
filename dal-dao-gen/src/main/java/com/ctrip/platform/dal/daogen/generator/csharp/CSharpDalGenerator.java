package com.ctrip.platform.dal.daogen.generator.csharp;

import java.util.Map;
import org.apache.log4j.Logger;
import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.CSharpDirectoryPreparerProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.CSharpCodeGeneratorOfFreeSqlProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.CSharpDataPreparerOfFreeSqlProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.CSharpCodeGeneratorOfOthersProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.CSharpCodeGeneratorOfSpProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.CSharpDataPreparerOfSqlBuilderProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.CSharpCodeGeneratorOfTableProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.csharp.CSharpDataPreparerOfTableViewSpProcessor;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public class CSharpDalGenerator implements DalGenerator {
	
	private Logger log = Logger.getLogger(CSharpDalGenerator.class);
	
	@Override
	public CodeGenContext createContext(int projectId, boolean regenerate,
			Progress progress, Map<String, ?> hints) throws Exception {
		CSharpCodeGenContext ctx = null;
		try {
			ctx = new CSharpCodeGenContext(projectId, regenerate, progress,
					hints);
			Project project = SpringBeanGetter.getDaoOfProject().getProjectByID(ctx.getProjectId());
			DalConfigHost dalConfigHost = null;
			if (project.getDal_config_name() != null
					&& !project.getDal_config_name().isEmpty()) {
				dalConfigHost = new DalConfigHost(project.getDal_config_name());
			} else if (project.getNamespace() != null
					&& !project.getNamespace().isEmpty()) {
				dalConfigHost = new DalConfigHost(project.getNamespace());
			} else {
				dalConfigHost = new DalConfigHost("");
			}
			ctx.setDalConfigHost(dalConfigHost);
			if (null != hints && hints.containsKey("newPojo")) {
				Object _newPojo = hints.get("newPojo");
				boolean newPojo = Boolean.parseBoolean(_newPojo.toString());
				ctx.setNewPojo(newPojo);
			}
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
	public void generateCode(CodeGenContext codeGenCtx) throws Exception{
		
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
