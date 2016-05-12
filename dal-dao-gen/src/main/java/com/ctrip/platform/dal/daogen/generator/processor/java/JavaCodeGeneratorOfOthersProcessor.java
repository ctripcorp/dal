package com.ctrip.platform.dal.daogen.generator.processor.java;

import com.ctrip.platform.dal.daogen.CodeGenContext;
import com.ctrip.platform.dal.daogen.DalProcessor;
import com.ctrip.platform.dal.daogen.resource.UserInfoResource;

public class JavaCodeGeneratorOfOthersProcessor implements DalProcessor {
    @Override
    public void process(CodeGenContext context) throws Exception {
        DalProcessor processor = null;
        String className = UserInfoResource.getInstance().getProcessorClassName();
        if (className == null || className.isEmpty()) {
            processor = new CommonJavaCodeGeneratorOfOthersProcessor();
        } else {
            Class<?> clazz = Class.forName(className);
            processor = (DalProcessor) clazz.newInstance();
        }

        processor.process(context);
    }
}
