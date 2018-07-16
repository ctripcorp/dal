package mybatis.mysql.datasource2;

/**
 * Created by lilj on 2017/11/3.
 */
public interface DRTestMapper2 {
    String getHostNameMySQL2();
    DRTestMybatisPojo2 getDRTestMybatisPojo2(int iD);
    void addDRTestMybatisPojo2(DRTestMybatisPojo2 drTestMybatisPojo);
    int getCount2();
    void truncateTableMySQL2();
    void updateDRTestMybatisPojo2(DRTestMybatisPojo2 drTestMybatisPojo);
}
