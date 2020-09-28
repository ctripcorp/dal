import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalTableDao;
import entity.MysqlPersonTable;
import org.junit.Test;

import java.util.regex.Pattern;

/**
 * @author c7ch23en
 */
public class TempTest {

    @Test
    public void testStrategyUpdate() throws Exception {
        DalTableDao<MysqlPersonTable> dao = new DalTableDao<>(MysqlPersonTable.class, "dal_sharding_cluster2", "people");
        int age = 0;
        while (age++ < 100) {
            MysqlPersonTable pojo = new MysqlPersonTable();
            pojo.setName("temp-test");
            pojo.setAge(age);
            try {
                int ret = dao.insert(new DalHints().inTableShard(0), pojo);
                System.out.println("\n  ret = " + ret + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRegex() {
        String raw = "abc_sh";
        String reg = "(_w|_w_sh|_r|_r_sh|_sh)$";
        Pattern pattern = Pattern.compile(reg);
        System.out.println("raw: " + raw);
        System.out.println("result: " + raw.replaceFirst(reg, ""));
        System.out.println("raw: " + raw);
    }

    @Test
    public void testParsePath() {
        String raw = "abc/xyz\\";
        int len = raw.length();
        int idx1 = raw.lastIndexOf("/");
        if (idx1 >= 0) {
            String temp = raw.substring(idx1);
            int idx2 = temp.lastIndexOf("\\");
            if (idx2 < 0)
                len = idx1 + 1;
            else
                len = idx1 + idx2 + 1;
        } else {
            int idx2 = raw.lastIndexOf("\\");
            if (idx2 >= 0)
                len = idx2 + 1;
        }
        System.out.println("raw: " + raw);
        System.out.println("path: " + raw.substring(0, len));
        System.out.println("file: " + (len == raw.length() ? "n/a" : raw.substring(len)));
        System.out.println("raw: " + raw);
    }

}
