
import TestCases.QPSTestFat16;
import TestCases.TestBothDALAndMybatisFat16;
import TestCases.TestLocalDalAndMybatisFat16;
import TestCases.TestPoolPropertiesFat16;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TestBothDALAndMybatisFat16.class,
        TestLocalDalAndMybatisFat16.class,
        TestPoolPropertiesFat16.class,
        QPSTestFat16.class,
})
public class AllTests {
  /*@BeforeClass
  public static void setUp() throws Exception {
    System.setProperty("env", "fat");
  }*/
}
