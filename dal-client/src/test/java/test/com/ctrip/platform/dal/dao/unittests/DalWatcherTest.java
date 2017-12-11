package test.com.ctrip.platform.dal.dao.unittests;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.StatementParameters;

import test.com.ctrip.platform.dal.dao.unitbase.SqlServerDatabaseInitializer;
import test.com.ctrip.platform.dal.dao.unitbase.BaseTestStub.DatabaseDifference;

public class DalWatcherTest {
    private static SqlServerDatabaseInitializer initializer = new SqlServerDatabaseInitializer();

    private final static String DATABASE_NAME = initializer.DATABASE_NAME;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        initializer.setUpBeforeClass();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        initializer.tearDownAfterClass();
    }

    @Before
    public void setUp() throws Exception {
        initializer.setUp();
    }

    @After
    public void tearDown() throws Exception {
        initializer.tearDown();
    }
    
    public final static String TABLE_NAME = "dal_client_test";
    
    @Test
    public void testQueryObjectWhenShutdown() throws Exception{
        final DalQueryDao client = new DalQueryDao(DATABASE_NAME);
        Runnable shutdown = new Runnable() {
            public void run() {
                while(true){
                    DalClientFactory.shutdownFactory();
                }
            }
        };
        
        Runnable query = new Runnable() {
            public void run() {
                while(true){
                    String sql = "SELECT quantity FROM " + TABLE_NAME;
                    StatementParameters param = new StatementParameters();
                    DalHints hints = new DalHints();
                    try {
                        System.out.println("query");
                        client.queryFrom(sql, param, hints, Integer.class, 0, 10);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        };
        
        new Thread(shutdown).start();
        new Thread(query).start();;
        
        int i = 100;
        while(i-- > 0) {
            System.out.println("waiting");
            Thread.sleep(1*10000);
        }
    }
    
    @Test
    public void testQueryObjectWhenStarup() throws Exception{
        final AtomicReference<Boolean> done = new AtomicReference<>();
        final AtomicReference<Boolean> p = new AtomicReference<>();
        done.set(false);
        p.set(false);
        final DalQueryDao client = new DalQueryDao(DATABASE_NAME);

        Runnable query = new Runnable() {
            public void run() {
                while(!done.get() && true){
                    String sql = "SELECT quantity FROM " + TABLE_NAME;
                    StatementParameters param = new StatementParameters();
                    DalHints hints = new DalHints();
                    try {
                        client.queryFrom(sql, param, hints, Integer.class, 0, 10);
                    } catch (SQLException e) {
                        if(!p.get() && !e.getMessage().contains("Invalid object name 'dal_client_test'.")) {
                            e.printStackTrace();
                            p.set(true);
                        }
                        break;
                    }
                    
                    if(done.get())
                        break;
                }
            }
        };
        
        DalClientFactory.shutdownFactory();
        System.out.println("Factory is down");
        
        int j = 3000;
        while(j-->0) {
            new Thread(query).start();
        }
        
        System.out.println("start threads...");
        
        int i = 3;
        while(i-- > 0) {
            System.out.println("tic");
            Thread.sleep(1*10000);
        }
        
        System.out.println("done main");
        DalClientFactory.shutdownFactory();
        done.set(true);
        System.out.println("cool down");
        Thread.sleep(2*1000);
    }

}
