package test.com.ctrip.platform.dal.dao.unittests;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.*;


import com.ctrip.platform.dal.dao.StatementParameter;
import org.junit.Test;
import com.ctrip.platform.dal.dao.StatementParameters;


public class StatementParametersTest {
    @Test
    public void testDuplicate() {
        StatementParameters test = new StatementParameters();
        test.set(1, "name1", Types.INTEGER, 1);
        test.set(2, "name2", Types.INTEGER, 2);
        StatementParameters test2 = test.duplicateWith("name1", 2);
        StatementParameters test3 = test.duplicateWith("name1", 3);
        assertEquals(2, test.size());
        assertEquals(2, test2.size());
        assertEquals(2, test3.size());

        assertEquals(1, test.get(0).getValue());
        assertEquals(2, test.get(1).getValue());

        assertEquals(2, test2.get(0).getValue());
        assertEquals(2, test2.get(1).getValue());

        assertEquals(3, test3.get(0).getValue());
        assertEquals(2, test3.get(1).getValue());
    }

    @Test
    public void testExpand() {
        StatementParameters test = new StatementParameters();
        List<Integer> values = new ArrayList<>();
        values.add(1);
        values.add(2);
        values.add(3);

        test.setInParameter(3, "name3", Types.INTEGER, values);
        test.set(2, "name2", Types.INTEGER, 2);
        test.set(1, "name1", Types.INTEGER, 1);

        test.compile();

        assertEquals("name1", test.get(0).getName());
        assertEquals("name2", test.get(1).getName());

        assertEquals("name3", test.get(2).getName());
        assertEquals("name3", test.get(3).getName());
        assertEquals("name3", test.get(4).getName());

        // check index
        assertEquals(1, test.get(0).getIndex());
        assertEquals(2, test.get(1).getIndex());

        assertEquals(3, test.get(2).getIndex());
        assertEquals(4, test.get(3).getIndex());
        assertEquals(5, test.get(4).getIndex());

        assertEquals(5, test.size());
    }

    @Test
    public void testInParmWithDefaultType() {
//     test for api  int setInParameter(int index, String name, List<?> values)
        StatementParameters test = new StatementParameters();
        List<Integer> values = new ArrayList<>();
        values.add(1);
        values.add(2);
        values.add(3);

        test.set(2, "name2", 2);
        test.set(1, "name1", 1);
        test.setInParameter(3, "name3", values);

        test.compile();

        assertEquals("name1", test.get(0).getName());
        assertEquals("name2", test.get(1).getName());

        assertEquals("name3", test.get(2).getName());
        assertEquals("name3", test.get(3).getName());
        assertEquals("name3", test.get(4).getName());

        // check index
        assertEquals(1, test.get(0).getIndex());
        assertEquals(2, test.get(1).getIndex());

        assertEquals(3, test.get(2).getIndex());
        assertEquals(4, test.get(3).getIndex());
        assertEquals(5, test.get(4).getIndex());

        assertEquals(5, test.size());

//        test for api int setInParameter(int index, List<?> values)
        StatementParameters test2 = new StatementParameters();
        List<Integer> values2 = new ArrayList<>();
        values2.add(1);
        values2.add(2);
        values2.add(3);

        test2.set(2,  2);
        test2.set(1,  1);
        test2.setInParameter(3,  values2);

        test2.compile();

        // check index
        assertEquals(1, test2.get(0).getIndex());
        assertEquals(1, test2.get(0).getValue());
        assertEquals(2, test2.get(1).getIndex());
        assertEquals(2, test2.get(1).getValue());

        assertEquals(3, test2.get(2).getIndex());
        assertEquals(4, test2.get(3).getIndex());
        assertEquals(5, test2.get(4).getIndex());
        assertEquals(1, test2.get(2).getValue());
        assertEquals(2, test2.get(3).getValue());
        assertEquals(3, test2.get(4).getValue());

        assertEquals(5, test2.size());
    }


    @Test
    public void testBuildParameters() {
        StatementParameters test = new StatementParameters();
        test.add(new StatementParameter(2, Types.INTEGER, 20));
        test.add(new StatementParameter(1, Types.INTEGER, 50));
        test.add(new StatementParameter(3, Types.INTEGER, 60));

        test.buildParameters();
        assertEquals(1, test.get(0).getIndex());
        assertEquals(50, test.get(0).getValue());

        assertEquals(2, test.get(1).getIndex());
        assertEquals(20, test.get(1).getValue());

        assertEquals(3, test.get(2).getIndex());
        assertEquals(60, test.get(2).getValue());
    }

    @Test
    public void testParametersWithDiscontinuousIndex() {
        StatementParameters test = new StatementParameters();
        test.add(new StatementParameter(3, Types.INTEGER, 60));
        test.add(new StatementParameter(1, Types.INTEGER, 50));
        test.add(new StatementParameter(6, Types.INTEGER, 20));

        test.buildParameters();
        assertEquals(1, test.get(0).getIndex());
        assertEquals(50, test.get(0).getValue());

        assertEquals(2, test.get(1).getIndex());
        assertEquals(60, test.get(1).getValue());

        assertEquals(3, test.get(2).getIndex());
        assertEquals(20, test.get(2).getValue());
    }

    @Test
    public void testParametersWithDefaultType(){
//        test for api public StatementParameters set(int index, String name, Object value)
        StatementParameters test=new StatementParameters();
        test.set(2,"param2",20);
        test.set(1,"param1",10);
        test.set(5,"param5",50);

        test.buildParameters();
        assertEquals(1, test.get(0).getIndex());
        assertEquals("param1", test.get(0).getName());
        assertEquals(10, test.get(0).getValue());

        assertEquals(2, test.get(1).getIndex());
        assertEquals("param2", test.get(1).getName());
        assertEquals(20, test.get(1).getValue());

        assertEquals(3, test.get(2).getIndex());
        assertEquals("param5", test.get(2).getName());
        assertEquals(50, test.get(2).getValue());

//      test for api StatementParameters set(String name, Object value)
        StatementParameters test2=new StatementParameters();
        test2.set("param8",80);
        test2.set("param3",30);
        test2.set("param10",100);
        test2.buildParameters();
        assertEquals(1, test2.get(0).getIndex());
        assertEquals("param8", test2.get(0).getName());
        assertEquals(80, test2.get(0).getValue());

        assertEquals(2, test2.get(1).getIndex());
        assertEquals("param3", test2.get(1).getName());
        assertEquals(30, test2.get(1).getValue());

        assertEquals(3, test2.get(2).getIndex());
        assertEquals("param10", test2.get(2).getName());
        assertEquals(100, test2.get(2).getValue());
    }

    @Test
    public void testMixUse() {
        StatementParameters test = new StatementParameters();
        test.set(1, Types.INTEGER, 20);
        try {
            test.set("param", Types.INTEGER, 30);
            fail();
        }catch (Exception e){

        }

        StatementParameters test1 = new StatementParameters();
        test1.set("param", Types.INTEGER, 20);
        try {
            test1.set(1, Types.INTEGER, 30);
            fail();
        }catch (Exception e){

        }
    }


    @Test
    public void testSetParameterWithDuplicatedIndex() throws Exception{
        StatementParameters test = new StatementParameters();
        test.add(new StatementParameter(6, Types.INTEGER, 20));
        try {
            test.add(new StatementParameter(6, Types.INTEGER, 50));
            fail();
        }catch (Exception e){

        }
    }


    @Test
    public void testSetParameterWithoutIndex() throws Exception{
        StatementParameters test = new StatementParameters();
        test.add(new StatementParameter("param2",Types.INTEGER, 50));
        test.add(new StatementParameter("param1",Types.INTEGER, 20));
        test.add(new StatementParameter("param3",Types.INTEGER, 60));

        test.buildParameters();
        assertEquals(1, test.get(0).getIndex());
        assertEquals("param2", test.get(0).getName());
        assertEquals(50, test.get(0).getValue());

        assertEquals(2, test.get(1).getIndex());
        assertEquals("param1", test.get(1).getName());
        assertEquals(20, test.get(1).getValue());

        assertEquals(3, test.get(2).getIndex());
        assertEquals("param3", test.get(2).getName());
        assertEquals(60, test.get(2).getValue());
    }

    @Test
    public void testSetResultParameter() throws Exception{
        StatementParameters test=new StatementParameters();
        test.setResultsParameter("param1");
        test.setResultsParameter("param2");
        test.setResultsParameter("param3");

        assertEquals("param1", test.getResultParameters().get(0).getName());
        assertEquals(0, test.getResultParameters().get(0).getIndex());

        assertEquals("param2", test.getResultParameters().get(1).getName());
        assertEquals(0, test.getResultParameters().get(1).getIndex());

        assertEquals("param3", test.getResultParameters().get(2).getName());
        assertEquals(0, test.getResultParameters().get(2).getIndex());

    }
}