package DRTestMybatisMySQL;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by lilj on 2017/10/25.
 */
@Repository
public class DRTestMapperDao {
    @Autowired
    private DRTestMapper drTestMapper;


    public  String getHostNameMySQL(){
        return drTestMapper.getHostNameMySQL();
    }

    public  String getDatabaseMySQL(){
        return drTestMapper.getDatabaseMySQL();
    }


    public DRTestMybatisPojo getDRTestMybatisPojo(){
        return drTestMapper.getDRTestMybatisPojo(1);
    }

    public void addDRTestMybatisPojo(){
        DRTestMybatisPojo drTestMybatisPojo=new DRTestMybatisPojo();
        drTestMybatisPojo.setName("testMybatis");
        drTestMybatisPojo.setAge(29);
        drTestMapper.addDRTestMybatisPojo(drTestMybatisPojo);
    }

    public void updateDRTestMybatisPojo(){
        DRTestMybatisPojo drTestMybatisPojo=new DRTestMybatisPojo();
        drTestMybatisPojo.setID(1);
        drTestMybatisPojo.setName("testUpdateMybatis");
        drTestMybatisPojo.setAge(99);
        drTestMapper.updateDRTestMybatisPojo(drTestMybatisPojo);
    }

    public int getCount(){
        return drTestMapper.getCount();
    }

    public void truncateTable(){
        drTestMapper.truncateTableMySQL();
    }

}
