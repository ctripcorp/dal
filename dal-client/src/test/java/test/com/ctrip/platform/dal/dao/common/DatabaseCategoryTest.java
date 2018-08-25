package test.com.ctrip.platform.dal.dao.common;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.exceptions.DalParameterException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class DatabaseCategoryTest {
    @Before
    public void setUp() throws Exception {
        DalClientFactory.initClientFactory();
    }

    @Test
    public void testSetObjectThrowExceptionMessage() throws Exception {
        TestTableDao dao = new TestTableDao();
        TestTable pojo = new TestTable();
        pojo.setID(1);
        pojo.setName("1");

        try {
            List<TestTable> list = dao.queryLike(pojo);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertTrue(e instanceof DalParameterException);
            Assert.assertTrue(e.getMessage().equals(
                    "Unknown Types value. Parameter[Index:2, Name:Name, java.sql.Types:-9, java.sql.Types name:NVARCHAR, Value:1]"));
        }
    }

}