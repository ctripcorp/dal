package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.log.LoggerManager;
import com.ctrip.platform.dal.daogen.resource.CustomizedResource;

public class JavaCodeGeneratorOfOthersProcessor implements DalProcessor {
    @Override
    public void process(CodeGenContext context) throws Exception {
        try {
            DalProcessor processor = null;
            String className = CustomizedResource.getInstance().getConfigClassName();
            if (className == null || className.isEmpty()) {
                processor = new CommonJavaCodeGeneratorOfOthersProcessor();
            } else {
                Class<?> clazz = Class.forName(className);
                processor = (DalProcessor) clazz.newInstance();
            }

            processor.process(context);
        } catch (Throwable e) {
            LoggerManager.getInstance().error(e);
            throw e;
        }
    }
}
