package testUtil;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Created by lilj on 2017/6/27.
 */
public class DalHintsChecker {
    public static void checkNull(DalHints hints) {
        checkNull(hints, Collections.<DalHintEnum>emptyList());
    }

    public static void checkNull(DalHints hints, List<DalHintEnum> exclude) {
        for (DalHintEnum hintEnum : DalHintEnum.values()) {
            if (exclude.contains(hintEnum)) {
                continue;
            }
            assertTrue("hints.get(hintEnum) is not null",hints.get(hintEnum)==null);
//            assertNull(hints.get(hintEnum));
        }
    }

    public static void checkEquals(DalHints original, DalHints toCheck) {
        checkEquals(original, toCheck, Collections.<DalHintEnum>emptyList());
    }

    public static void checkEquals(DalHints original, DalHints toCheck, List<DalHintEnum> exclude) {
        for (DalHintEnum hintEnum : DalHintEnum.values()) {
            if (exclude.contains(hintEnum)) {
                continue;
            }
            switch (hintEnum) {
                case shardColValues:
                case fields:
                    assertMapEquals((Map)original.get(hintEnum),(Map)toCheck.get(hintEnum));
                    break;
                default:
                    assertEquals(String.format("check DalHintEnum.%s failed:",hintEnum),original.get(hintEnum), toCheck.get(hintEnum));
            }
        }
    }

    private static void assertMapEquals(Map original, Map toCheck) {
        if (original == null && toCheck == null) {
            return;
        }
        assertTrue("Both should not be null", original != null && toCheck != null);
        Assert.isTrue(original.keySet().size()==toCheck.keySet().size());
//        assertEquals(original.keySet().size(), toCheck.keySet().size());
        for (Object key : original.keySet()) {
            assertTrue(String.format("Should contain key: %s", key), toCheck.containsKey(key));
            assertEquals(String.format("Should have the same value for key: %s", key), original.get(key), toCheck.get(key));
        }
    }
}
