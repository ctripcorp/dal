package DRTestMybatisMySQL2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by lilj on 2017/11/3.
 */
@Repository
public class DRTestMapperDao2 {
    @Autowired
    private DRTestMapper2 drTestMapper2;


    public  String getHostNameMySQL2(){
        return drTestMapper2.getHostNameMySQL2();
    }


    public DRTestMybatisPojo2 getDRTestMybatisPojo2(){
        return drTestMapper2.getDRTestMybatisPojo2(1);
    }

    public void addDRTestMybatisPojo2(){
        DRTestMybatisPojo2 drTestMybatisPojo=new DRTestMybatisPojo2();
        drTestMybatisPojo.setName("testMybatis");
        drTestMybatisPojo.setAge(29);
        drTestMapper2.addDRTestMybatisPojo2(drTestMybatisPojo);
    }

    public void updateDRTestMybatisPojo2(){
        DRTestMybatisPojo2 drTestMybatisPojo=new DRTestMybatisPojo2();
        drTestMybatisPojo.setID(1);
        drTestMybatisPojo.setName("testUpdateMybatis");
        drTestMybatisPojo.setAge(99);
        drTestMapper2.updateDRTestMybatisPojo2(drTestMybatisPojo);
    }

    public int getCount2(){
        return drTestMapper2.getCount2();
    }

    public void truncateTable2(){
        drTestMapper2.truncateTableMySQL2();
    }
}
