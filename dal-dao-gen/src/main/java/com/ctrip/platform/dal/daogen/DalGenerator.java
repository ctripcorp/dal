package com.ctrip.platform.dal.daogen;

import com.ctrip.platform.dal.daogen.entity.Progress;

public interface DalGenerator {
    CodeGenContext createContext(int projectId, boolean regenerate, Progress progress, boolean newPojo, boolean ignoreApproveStatus) throws Exception;

    void prepareDirectory(CodeGenContext codeGenCtx) throws Exception;

    void prepareData(CodeGenContext codeGenCtx) throws Exception;

    void generateCode(CodeGenContext codeGenCtx) throws Exception;

}
