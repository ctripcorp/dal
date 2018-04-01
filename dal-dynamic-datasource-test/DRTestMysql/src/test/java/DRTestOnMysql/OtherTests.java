package DRTestOnMysql;

import com.ctrip.platform.dal.dao.DalClientFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.DocFlavor;
import java.io.*;
import java.util.*;

/**
 * Created by lilj on 2018/1/10.
 */
public class OtherTests {

    private static DRTestDao dao = null;
    private static Logger log = LoggerFactory.getLogger(OtherTests.class);
    private static String DATA_BASE_1 = "noShardTestOnMysql_111";
    private static String DATA_BASE_2 = "noShardTestOnMysql_122";

    @Before
    public void setUp() throws Exception {
//        System.out.println("setUp");
        DalClientFactory.shutdownFactory();
        DalClientFactory.initClientFactory(this.getClass().getClassLoader().getResource(".").getPath() + "DalConfig/Dal.config");

    }

    @After
    public void tearDown() throws Exception {
//        System.out.println("tearDown");
        DalClientFactory.shutdownFactory();
    }

    @Test
    public void testCount() throws Exception {
        DRTestDao dao111 = new DRTestDao(DATA_BASE_1);
        int count111 = dao111.count(null);
        log.info(String.format("count in FAT1868.testtable: %d", count111));
        DRTestDao dao122 = new DRTestDao(DATA_BASE_2);
        int count122 = dao122.count(null);
        log.info(String.format("count in FAT1869.testtable: %d", count122));

        int totalNum=count111+count122;
        log.info(String.format("total count : %d",totalNum));
    }

    @Test
    public void test() throws Exception {
        System.setOut(new PrintStream(new File("test1.txt")));
        System.out.println("test1");
        System.setOut(new PrintStream(new File("test2.txt")));
        System.out.println("test2");
    }

    @Test
    public void testLocalCount() throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("D:/successButNonExistDataIds.txt"), "UTF-8"));
        String line;
        Set<Integer> ids = new HashSet<>();
        List<Integer> idsInJustInOldMaster = new ArrayList<>();

        while ((line = in.readLine()) != null) {
            ids.add(Integer.parseInt(line.trim()));
        }
        for (int i : ids) {
            DRTestPojo pojo = dao.queryByPk(i, null);
            if (pojo != null) {
                log.info(String.format("the id exists in old master but not new master: %d", i));
                idsInJustInOldMaster.add(i);
            }
        }
        System.out.println(idsInJustInOldMaster.size());
        Assert.assertEquals(ids.size(), idsInJustInOldMaster.size());
    }
}
