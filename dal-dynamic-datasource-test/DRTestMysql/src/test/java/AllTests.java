import DRTestOnMysql.AutoTestForConnectionStringFat16;
import DRTestOnMysql.AutoTestForPoolPropertiesFat16;
import DRTestOnMysql.ConnectionTestFat16;
import DRTestOnMysql.QPSTestFat16;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        AutoTestForConnectionStringFat16.class,
        AutoTestForPoolPropertiesFat16.class,
        QPSTestFat16.class,
        ConnectionTestFat16.class
})
public class AllTests {
  /*@BeforeClass
  public static void setUp() throws Exception {
    System.setProperty("env", "fat");
  }*/
}
