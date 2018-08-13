package test.com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.helper.DefaultTableParser;
import com.ctrip.platform.dal.dao.helper.TableParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by lilj on 2018/7/26.
 */
public class TableParserTest {
    private TableParser tableParser = new DefaultTableParser();

    @Test
    public void getTablesFromMultipleSqls() throws Exception{
        String[] selectSql={"/*1234567-PersonShardColModShardByDBOnMysqlDao.queryListMultipleAllShards[MSG_ID:123456-123456-123456-123456]*/select * from person;select * from person;select * from person;select * from person;select * from person;select * from people;select * from person;select * from person;select count(*) from testtable;select count(*) from person;select count(*) from person;select count(*) from person;select count(*) from person;select count(*) from person;select count(*) from person_0;select Name from person;select Name from person;select Name from person;select Name from person;select Name from person;select Name from person;select Name from person;select Name from person;select * from person_1 where ID = ?;select * from person where ID = ?;select * from person where ID = ?;select * from person where ID = ?;select * from person where ID = ?;select * from person where ID = ?;select * from person where ID = ?;select * from person where ID = ?;select * from person where ID = ? and Age=21;select * from person where ID = ? and Age=21;select * from person where ID = ? and Age=21;select * from person where ID = ? and Age=21;select * from person where ID = ? and Age=21;select * from person where ID = ? and Age=21;select * from person where ID = ? and Age=21;select * from person where ID = ? and Age=21;select * from person where Age In (?,?,?) ;select * from person where Age In (?,?,?) ;select * from person where Age In (?,?,?) ;select * from person where Age In (?,?,?) ;select * from person where Age In (?,?,?) ;select * from person where Age In (?,?,?) ;select * from person where Age In (?,?,?) ;select * from person where Age In (?,?,?) ;select * from person where ID = 4;select * from person where ID = 4;select * from person where ID = 4;select * from person where ID = 4;select * from person where ID = 4;select * from person where ID = 4;select * from person where ID = 4;select * from person where ID = 4;select count(*) from person;"};
        Set<String> selectSqlTables= tableParser.getTablesFromSqls(selectSql);
        assertEquals(5,selectSqlTables.size());
        assertTrue(selectSqlTables.contains("person"));
        assertTrue(selectSqlTables.contains("people"));
        assertTrue(selectSqlTables.contains("testtable"));
        assertTrue(selectSqlTables.contains("person_0"));
        assertTrue(selectSqlTables.contains("person_1"));
    }

    @Test
    public void getTablesFromSqlsWithSingleTable() throws Exception {
        String[] sqls = {"/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/update testTable set name=name1 where id>2",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/delete from testTable where id>3",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/insert into testTable (name) values (\"hello\")",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from testTable where id=1",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from testTable a where a.id=1",
        };
        for (String sql : sqls) {
            Set<String> tables = tableParser.getTablesFromSqls(sql);
            assertEquals(1, tables.size());
            assertTrue(tables.contains("testtable"));
        }


        String[] sqlsWithDB = {"/*123456-DALDao.combinedInsert*/update testDb.testTable set name=name1 where id>2",
                "/*123456-DALDao.combinedInsert*/delete from testDb.testTable where id>3",
                "/*123456-DALDao.combinedInsert*/insert into testDb.testTable (name) values (\"hello\")",
                "/*123456-DALDao.combinedInsert*/select * from testDb.testTable where id=1",
                "/*123456-DALDao.combinedInsert*/select * from testDb.testTable a where a.id=1",
        };
        for (String sql : sqlsWithDB) {
            Set<String> tables = tableParser.getTablesFromSqls(sql);
            assertEquals(1, tables.size());
            assertTrue(tables.contains("testtable"));
        }
    }


    @Test
    public void getTablesFromSqlsWithSingleQuoteTable() throws Exception {
        String[] sqls = {"/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/update `testTable` set name=name1 where id>2",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/delete from `testTable` where id>3",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/insert into `testTable` (name) values (\"hello\")",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `testTable` where id=1",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `testTable` a where a.id=1",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/update [testTable] set name=name1 where id>2",
                "/*123456-DALDao.combinedInsert*/delete from [testTable]  where id>3",
                "/*123456-DALDao.combinedInsert*/insert into [testTable]  (name) values (\"hello\")",
                "/*123456-DALDao.combinedInsert*/select * from [testTable]  where id=1",
                "/*123456-DALDao.combinedInsert*/select * from [testTable]  a where a.id=1",
        };
        for (String sql : sqls) {
            Set<String> tables = tableParser.getTablesFromSqls(sql);
            assertEquals(1, tables.size());
            assertTrue(tables.contains("testtable"));
        }


        String[] sqlsWithDB = {"/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/update `testDb`.`testTable` set name=name1 where id>2",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/delete from `testDb`.`testTable` where id>3",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/insert into `testDb`.`testTable` (name) values (\"hello\")",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `testDb`.`testTable` where id=1",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `testDb`.`testTable` a where a.id=1",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/update [testDb].[testTable] set name=name1 where id>2",
                "/*123456-DALDao.combinedInsert*/delete from [testDb].[testTable] where id>3",
                "/*123456-DALDao.combinedInsert*/insert into [testDb].[testTable] (name) values (\"hello\")",
                "/*123456-DALDao.combinedInsert*/select * from [testDb].[testTable] where id=1",
                "/*123456-DALDao.combinedInsert*/select * from [testDb].[testTable] a where a.id=1",
        };
        for (String sql : sqlsWithDB) {
            Set<String> tables = tableParser.getTablesFromSqls(sql);
            assertEquals(1, tables.size());
            assertTrue(tables.contains("testtable"));
        }
    }

    @Test
    public void getTablesFromSqlsWithMultipleTables() throws Exception {
        String[] sqls={"/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from Table1 join Table2 on table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from Table1,Table2 where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from Table1, Table2 where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from Table1 ,Table2 where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from Table1 , Table2 where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from Table1 as a , Table2 as b where a.id=b.id",
        };
        for (String sql : sqls) {
            Set<String> tables = tableParser.getTablesFromSqls(sql);
            assertEquals(2, tables.size());
            assertTrue(tables.contains("table1"));
            assertTrue(tables.contains("table2"));
        }

        String[] sqlsWithDB={"/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from DB.Table1 join DB.Table2 on table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from DB.Table1,DB.Table2 where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from DB.Table1, DB.Table2 where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from DB.Table1 ,DB.Table2 where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from DB.Table1 , DB.Table2 where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from DB.Table1 as a , DB.Table2 as b where a.id=b.id",
        };
        for (String sql : sqlsWithDB) {
            Set<String> tables = tableParser.getTablesFromSqls(sql);
            assertEquals(2, tables.size());
            assertTrue(tables.contains("table1"));
            assertTrue(tables.contains("table2"));
        }
    }

    @Test
    public void getTablesFromSqlsWithMultipleQuoteTables() throws Exception {
        String[] sqls={"/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `Table1` join `Table2` on table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `Table1`,`Table2` where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `Table1`, `Table2` where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `Table1` ,`Table2` where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `Table1` , `Table2` where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert*/select * from [Table1] join [Table2] on table1.id=table2.id",
                "/*123456-DALDao.combinedInsert*/select * from [Table1],[Table2] where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert*/select * from [Table1], [Table2] where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert*/select * from [Table1] ,[Table2] where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert*/select * from [Table1] , [Table2] where table1.id=table2.id",
        };
        for (String sql : sqls) {
            Set<String> tables = tableParser.getTablesFromSqls(sql);
            assertEquals(2, tables.size());
            assertTrue(tables.contains("table1"));
            assertTrue(tables.contains("table2"));
        }

        String[] sqlsWithDB={"/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `DB`.`Table1` join `DB`.`Table2` on table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `DB`.`Table1`,`DB`.`Table2` where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `DB`.`Table1`, `DB`.`Table2` where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `DB`.`Table1` ,`DB`.`Table2` where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert[MSG_ID:123456-1a3456-123456-123456]*/select * from `DB`.`Table1` , `DB`.`Table2` where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert*/select * from [DB].[Table1] join [DB].[Table2] on table1.id=table2.id",
                "/*123456-DALDao.combinedInsert*/select * from [DB].[Table1],[DB].[Table2] where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert*/select * from [DB].[Table1], [DB].[Table2] where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert*/select * from [DB].[Table1] ,[DB].[Table2] where table1.id=table2.id",
                "/*123456-DALDao.combinedInsert*/select * from [DB].[Table1] , [DB].[Table2] where table1.id=table2.id",
        };
        for (String sql : sqlsWithDB) {
            Set<String> tables = tableParser.getTablesFromSqls(sql);
            assertEquals(2, tables.size());
            assertTrue(tables.contains("table1"));
            assertTrue(tables.contains("table2"));
        }
    }

    @Test
    public void getTablesFromSubQuerySqls() throws Exception {
        String[] sqls = {"with table1 as (select * from Person where age < 30) select * from table1",
                "select * from (select a , b ,c from Person)"
        };

        for (String sql : sqls) {
            Set<String> tables = tableParser.getTablesFromSqls(sql);
            assertEquals(1, tables.size());
            assertTrue(tables.contains("person"));
        }
    }

    @Test
    public void getTablesFromSqlsWithLock() throws Exception {
        String[] sqls = {"select * from [Table1]with(nolock),[Table2]with (nolock), [Table3](nolock)," +
                "[Table4] with  (nolock),[Table5] with(nolock), [Table6] (nolock),"+
                "Table7 with(nolock),Table8 with " +
                "(nolock), Table9(nolock),Table10 (nolock),[Table11]with(nolock)" +
                "where id=1"
        };

        for (String sql : sqls) {
            Set<String> tables = tableParser.getTablesFromSqls(sql);
            assertEquals(11, tables.size());
            assertTrue(tables.contains("table1"));
            assertTrue(tables.contains("table2"));
            assertTrue(tables.contains("table3"));
            assertTrue(tables.contains("table4"));
            assertTrue(tables.contains("table5"));
            assertTrue(tables.contains("table6"));
            assertTrue(tables.contains("table7"));
            assertTrue(tables.contains("table8"));
            assertTrue(tables.contains("table9"));
            assertTrue(tables.contains("table10"));
            assertTrue(tables.contains("table11"));
        }
    }

    @Test
    public void getTablesFromCallString() throws Exception{
        String noTablecallString="{call sp_getslavestatus}/*123456-Executors$RunnableAdapter.call[MSG_ID:Unknown-00000000-000000-0]*/";
        String execSpAString="exec spA_People_i @PeopleID=?, @Name=?, @CityID=?, @ProvinceID=?, @CountryID=?/*123456-NoShardOnSqlServerDao.insert*/";
        String execSpTString="exec spT_People_i ?/*123456-NoShardOnSqlServerDao.batchInsert*/";
        String callString="{call spA_People_i(?, ?, ?, ?, ?)}/*123456-NoShardOnSqlServerDao.insert*/";
        String execString="exec sp3_O_Person_i @PeopleID=?, @Name=?, @CityID=?, @ProvinceID=?, @CountryID=?/*123456-NoShardOnSqlServerDao.insert*/";

        Set<String> callStringNoTable=tableParser.getTablesFromSqls(noTablecallString);
        assertEquals(0,callStringNoTable.size());

        Set<String> execSpAStringTable=tableParser.getTablesFromSqls(execSpAString);
        assertEquals(1,execSpAStringTable.size());
        assertTrue(execSpAStringTable.contains("people"));

        Set<String> execSpTStringTable=tableParser.getTablesFromSqls(execSpTString);
        assertEquals(1,execSpTStringTable.size());
        assertTrue(execSpTStringTable.contains("people"));

        Set<String> callStringTable=tableParser.getTablesFromSqls(callString);
        assertEquals(1,callStringTable.size());
        assertTrue(callStringTable.contains("people"));

        Set<String> execStringTable=tableParser.getTablesFromSqls(execString);
        assertEquals(1,execStringTable.size());
        assertTrue(execStringTable.contains("o_person"));
    }

    @Test
    public void testConcurrentTableParse() throws Exception {
        int threadCount = 1000;
        final CountDownLatch latch = new CountDownLatch(threadCount);
        final List<Integer> ret = Collections.synchronizedList(new ArrayList<Integer>());
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String name = Thread.currentThread().getName();
                        String suffix = name.split("-")[1];

                        String sql = "select * from table" + suffix + " where age < 50";
                        Set<String> tables = tableParser.getTablesFromSqls(sql);
                        assertEquals("table" + suffix, tables.toArray()[0]);
                        ret.add(0);
                    } catch (Throwable e) {
                        ret.add(1);
                        fail();
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }
        latch.await();
        if (ret.contains(1))
            fail();
    }

    /*@Test
    public void testJSqlParserPerformance() throws Exception{
        String sql="*//*100008423-PersonShardColModShardByDBOnMysqlDao.queryListMultipleAllShards[MSG_ID:100008423-0a201579-425503-10051]*//*select * from person1;select * from person2;select * from person3;select * from person4;select * from person5;select * from person6;select * from person7;select * from person8;select count(*) from person9;select count(*) from person10" +
                ";select count(*) from person11;select count(*) from person12;select count(*) from person13;select count(*) from person14;select count(*) from person15;select Name from person16;select Name from person17;select Name from person18;select Name from person19;select Name from person20;" +
                "select Name from person21;select Name from person22;select Name from person23;select * from person24 where ID = ?;select * from person25 where ID = ?;select * from person26 where ID = ?;select * from person27 where ID = ?;select * from person28 where ID = ?;select * from person29 where ID = ?;select * from person30 where ID = ?;" +
                "select * from person31 where ID = ?;select * from person32 where ID = ? and Age=21;select * from person33 where ID = ? and Age=21;select * from person where34 ID = ? and Age=21;select * from person35 where ID = ? and Age=21;select * from person36 where ID = ? and Age=21;select * from person37 where ID = ? and Age=21;select * from person38 where ID = ? and Age=21;select * from person39 where ID = ? and Age=21;select * from person40 where Age In (?,?,?) ;" +
                "select * from person41 where Age In (?,?,?) ;select * from person42 where Age In (?,?,?) ;select * from person43 where Age In (?,?,?) ;select * from person44 where Age In (?,?,?) ;select * from person45 where Age In (?,?,?) ;select * from person46 where Age In (?,?,?) ;select * from person47 where Age In (?,?,?) ;select * from person48 where ID = 4;select * from person49 where ID = 4;select * from person50 where ID = 4;" +
                "select * from person51 where ID = 4;select * from person52 where ID = 4;select * from person53 where ID = 4;select * from person54 where ID = 4;select * from person55 where ID = 4;select count(*) from person56;";
//        String sql="select * from person";
        Set<String> tables=new HashSet<>();
        long start=System.currentTimeMillis();
        for(int i=0;i<1000;i++){
            tables.addAll(tableParser.getTablesFromSqls(sql));
        }
        long end=System.currentTimeMillis();
        System.out.println("cost time: "+ (end-start)/1000);
        for(String table:tables)
        System.out.println(table);
    }*/
}
