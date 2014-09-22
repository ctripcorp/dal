package com.ctrip.platform.dal.daogen.generator.java;

import org.apache.log4j.Logger;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalGenerator;
import com.ctrip.platform.dal.daogen.entity.Progress;
import com.ctrip.platform.dal.daogen.entity.Project;
import com.ctrip.platform.dal.daogen.generator.processor.java.JavaDirectoryPreparerProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.java.JavaCodeGeneratorOfFreeSqlProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.java.JavaDataPreparerOfFreeSqlProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.java.JavaCodeGeneratorOfOthersProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.java.JavaCodeGeneratorOfSpProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.java.JavaDataPreparerOfSqlBuilderProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.java.JavaCodeGeneratorOfTableProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.java.JavaDataPreparerOfTableViewSpProcessor;
import com.ctrip.platform.dal.daogen.generator.processor.java.JavaCodeGeneratorOfViewProcessor;
import com.ctrip.platform.dal.daogen.host.DalConfigHost;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public class JavaDalGenerator implements DalGenerator {
	
	private Logger log = Logger.getLogger(JavaDalGenerator.class);
	
	@Override
	public CodeGenContext createContext(int projectId, boolean regenerate,
			Progress progress, boolean newPojo) throws Exception {
		JavaCodeGenContext ctx = null;
		try {
			ctx = new JavaCodeGenContext(projectId, regenerate, progress);
			Project project = SpringBeanGetter.getDaoOfProject()
					.getProjectByID(projectId);
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
			new JavaDirectoryPreparerProcessor().process(codeGenCtx);
		} catch (Exception e) {
			log.warn("exception occur when prepareDirectory", e);
			throw e;
		}
	}

	@Override
	public void prepareData(CodeGenContext codeGenCtx) throws Exception {
		try {
			new JavaDataPreparerOfFreeSqlProcessor().process(codeGenCtx);
			new JavaDataPreparerOfTableViewSpProcessor().process(codeGenCtx);
			new JavaDataPreparerOfSqlBuilderProcessor().process(codeGenCtx);
		} catch (Exception e) {
			log.warn("exception occur when prepareData", e);
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
			log.warn("exception occur when generateCode", e);
			throw e;
		}
	}
	
}
