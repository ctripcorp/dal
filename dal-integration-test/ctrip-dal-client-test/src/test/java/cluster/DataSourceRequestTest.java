package cluster;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * @author c7ch23en
 */
public class DataSourceRequestTest {

    @Test
    public void testSql() throws Exception {
        DataSource dataSource = new DalDataSourceFactory().getOrCreateDataSource("dal_sharding_cluster", 3);
        dataSource.getConnection().prepareStatement("select name from person where id > 1").executeQuery();
        dataSource.getConnection().prepareStatement("update person set age = 1 where name = 'p'").executeUpdate();
        TimeUnit.SECONDS.sleep(100);
    }

}
