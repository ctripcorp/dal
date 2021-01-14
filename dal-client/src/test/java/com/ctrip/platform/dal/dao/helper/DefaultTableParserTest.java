package com.ctrip.platform.dal.dao.helper;

import net.sf.jsqlparser.JSQLParserException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DefaultTableParserTest {

    @Test
    public void getTablesFromCache() throws JSQLParserException {
        DefaultTableParser parser = new DefaultTableParser();
        String sql = "SELECT DISTINCT    o.FlightClass,    o.OrderID,    f.Sequence,    f.RecordNo,    f.Flight,    f.DPort,    f.APort,    f.TakeOffTime,    f.ArrivalTime,    f.TakeOffTimeOfBeijing,    f.ArriveTimeOfBeijing,    f.Class AS ClassLevel,    f.SubClass,    f.CarrierFlightNo,    fe.DepartAirportBuildingName AS DBuilding,    fe.ArriveAirportBuildingName AS ABuilding,    fe.DepartAirportBuildingID AS DBuildingId,    fe.ArriveAirportBuildingID AS ABuildingId,    f.OfficeNo AS BookingOfficeNO,    o.OrderDate AS EffectiveTime FROM   O_Flight f        LEFT JOIN    O_FlightExtend fe ON fe.OrderID = f.OrderID        AND fe.Sequence = f.Sequence        JOIN    O_Orders o ON f.OrderID = o.OrderID        JOIN    o_Orderdetail d ON d.OrderID = f.OrderID        AND (d.IsPostPoneFee IS NULL        OR d.IsPostPoneFee = 0)        JOIN    O_Passenger e ON e.OrderID = f.orderid WHERE    1 =1 AND O.OrderStatus not in('C','R')\n" +
                " AND f.DPort= ?                                                                                                                                                                                                                          \n" +
                " and f.APort= ?                                                                                                                                                                                                                            \n" +
                " AND f.TakeOffTime between ? and ?                                                                                                                                                                                       \n                     " +
                " AND f.Flight = ?                                                                                                                                                                                                                \n" +
                " ORDER BY o.OrderID,f.Sequence limit 2000                                                                                                                                                                               ";
        parser.getTablesFromCache(sql);
        Assert.assertEquals(0, DefaultTableParser.sqlToTables.size());
        sql = "select * from table1 left join table2 on table1.name = table2.name";
        parser.getTablesFromCache(sql);
        Assert.assertEquals(1, DefaultTableParser.sqlToTables.size());
        parser.getTablesFromCache(sql);
        Assert.assertEquals(1, DefaultTableParser.sqlToTables.size());
    }

    @Test
    public void ignoreWhereAndValues() {
        String sql = "insert into table values (id1, name1),(id1, name1),(id1, name1);";
        DefaultTableParser parser = new DefaultTableParser();
        Assert.assertEquals("insert into table ", parser.ignoreWhereAndValues(sql));

        sql = "select * from table where id = 1 and name = name";
        Assert.assertEquals("select * from table ", parser.ignoreWhereAndValues(sql));

        sql = "update table set name = name";
        Assert.assertEquals(sql, parser.ignoreWhereAndValues(sql));
    }

    @Test
    public void LRUTest() {
        int initSize = 10;
        float loadFactor = 0.3f;
        DefaultTableParser.sqlToTables = new LinkedHashMap(initSize, loadFactor, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() >= initSize * loadFactor;
            }
        };

        DefaultTableParser.sqlToTables.put("1", Arrays.asList("1"));
        DefaultTableParser.sqlToTables.put("2", Arrays.asList("2"));
        DefaultTableParser.sqlToTables.put("3", Arrays.asList("3"));
        Assert.assertEquals(2, DefaultTableParser.sqlToTables.size());
        Assert.assertEquals("{2=[2], 3=[3]}", DefaultTableParser.sqlToTables.toString());
        DefaultTableParser.sqlToTables.get("1");
        Assert.assertEquals("{2=[2], 3=[3]}", DefaultTableParser.sqlToTables.toString());
        DefaultTableParser.sqlToTables.get("2");
        Assert.assertEquals("{3=[3], 2=[2]}", DefaultTableParser.sqlToTables.toString());
    }

}