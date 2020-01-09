package com.ctrip.platform.dal.dao.helper;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DalClassScannerTest {

    @Test
    public void testDalClassScanner() {
        ClassScanner scanner = new DalClassScanner(new ClassScanFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
//                return clazz.isAnnotationPresent(Retention.class);
                return !clazz.isInterface();
            }
        });
        String pkgName = "com.ctrip.platform.dal.dao.helper";
        pkgName = "org.junit";
//        pkgName = "";
        List<Class<?>> list = scanner.getClasses(pkgName, true);
        Assert.assertTrue(list.size() > 0);
    }

}
