package mybatis.sqlserver;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by lilj on 2017/11/2.
 */
@Repository
public class DRTestSQLServerMapperDao {

    @Autowired
    private DRTestSQLServerMapper drTestSQLServerMapper;
    

    public DRTestMybatisSQLServerPojo getDRTestMybatisSQLServerPojo(){
        return drTestSQLServerMapper.getDRTestMybatisSQLServerPojo(1);
    }

    public void addDRTestMybatisSQLServerPojo(){
        DRTestMybatisSQLServerPojo dRTestMybatisSQLServerPojo=new DRTestMybatisSQLServerPojo();
        dRTestMybatisSQLServerPojo.setName("안녕하세요");
        dRTestMybatisSQLServerPojo.setCityID(29);
        dRTestMybatisSQLServerPojo.setProvinceID(29);
        dRTestMybatisSQLServerPojo.setCountryID(29);

        drTestSQLServerMapper.addDRTestMybatisSQLServerPojo(dRTestMybatisSQLServerPojo);
    }

    public void updateDRTestMybatisSQLServerPojo(){
        DRTestMybatisSQLServerPojo DRTestMybatisSQLServerPojo=new DRTestMybatisSQLServerPojo();
        DRTestMybatisSQLServerPojo.setPeopleID(1L);
        DRTestMybatisSQLServerPojo.setName("testUpdateMybatis");
        DRTestMybatisSQLServerPojo.setCityID(99);
        drTestSQLServerMapper.updateDRTestMybatisSQLServerPojo(DRTestMybatisSQLServerPojo);
    }

   /* public int getCount(){
        return drTestSQLServerMapper.getCount();
    }*/


    public void truncateTableSQLServer(){
        drTestSQLServerMapper.truncateTableSQLServer();
    }

    public  String getHostNameSQLServer(){
        return drTestSQLServerMapper.getHostNameSQLServer();
    }

}
