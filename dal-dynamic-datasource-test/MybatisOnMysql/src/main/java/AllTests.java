import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TestCase.MybatisPoolPropertiesTestFat16.class,
        TestCase.MybatisDRTestMultipleKeysFat16.class,
        TestCase.MybatisDRTestSingleKeyFat16.class,
})
public class AllTests {
  /*@BeforeClass
  public static void setUp() throws Exception {
    System.setProperty("env", "fat");
  }*/
}
