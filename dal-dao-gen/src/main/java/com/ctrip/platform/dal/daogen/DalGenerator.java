package com.ctrip.platform.dal.daogen;

import java.util.Map;

import com.ctrip.platform.dal.daogen.entity.Progress;

public interface DalGenerator {

	CodeGenContext createContext(int projectId, boolean regenerate, Progress progress, Map<String,?> hints) throws Exception;

	boolean prepareDirectory(CodeGenContext codeGenCtx) throws Exception;
	
	boolean prepareData(CodeGenContext codeGenCtx) throws Exception;
	
	boolean generateCode(CodeGenContext codeGenCtx) throws Exception;
	
}
