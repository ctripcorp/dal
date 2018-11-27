package switchtest.mybatis;

import com.ctrip.platform.dal.dao.DalHints;
import mybatis.mysql.DRTestDao;
import mybatis.mysql.DRTestPojo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lilj on 2018/10/19.
 */
public class IpDomainConnectionTest {
    private static Logger log = LoggerFactory.getLogger(IpDomainConnectionTest.class);

    @Test
    public void test() throws Exception {
        final DRTestDao dao = new DRTestDao();
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            log.info(dao.selectHostname(null));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
//                            k
//                            Thread.sleep(1000);
                        }

                    }
                }
            }).start();
        }
    }

}
