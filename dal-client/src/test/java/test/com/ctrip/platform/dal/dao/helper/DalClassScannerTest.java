package test.com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.helper.ClassScanFilter;
import com.ctrip.platform.dal.dao.helper.ClassScanner;
import com.ctrip.platform.dal.dao.helper.DalClassScanner;
import org.junit.Test;

import javax.persistence.Entity;
import java.lang.annotation.Retention;
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
    }

}
