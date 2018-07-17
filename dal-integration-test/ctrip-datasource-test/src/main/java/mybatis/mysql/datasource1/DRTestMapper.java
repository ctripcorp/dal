package mybatis.mysql.datasource1;

/**
 * Created by lilj on 2017/10/26.
 */
public interface DRTestMapper {
    String getHostNameMySQL();
    String getDatabaseMySQL();
    DRTestMybatisPojo getDRTestMybatisPojo(int iD);
    String testLongQuery();
    void addDRTestMybatisPojo(DRTestMybatisPojo drTestMybatisPojo);
    int getCount();
    void truncateTableMySQL();
    void updateDRTestMybatisPojo(DRTestMybatisPojo drTestMybatisPojo);
}
