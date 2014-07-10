package com.ctrip.platform.dal.daogen;

import java.util.Map;

import com.ctrip.platform.dal.daogen.entity.Progress;

public interface DalGenerator {

	public CodeGenContext createContext(int projectId, boolean regenerate,
			Progress progress, Map<String, ?> hints) throws Exception;

	public void prepareDirectory(CodeGenContext codeGenCtx) throws Exception;

	public void prepareData(CodeGenContext codeGenCtx) throws Exception;

	public void generateCode(CodeGenContext codeGenCtx) throws Exception;

}
