package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure.*;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by shenjie on 2019/5/7.
 */
public class XmlUtilsTest {

    @Test
    public void test() throws Exception {
        DalConfigure dalConfigure = generateDalConfigure();
        System.out.println("---将对象转换成File类型的xml Start---");
        String str = XmlUtils.toXml(dalConfigure);
        System.out.println(str);
        System.out.println("---将对象转换成File类型的xml End---");

        System.out.println("---将File类型的xml转换成对象 Start---");
        DalConfigure configure = (DalConfigure) XmlUtils.fromXml(str, DalConfigure.class);
        String content = GsonUtils.t2Json(configure);
        System.out.println(content);
        System.out.println("---将File类型的xml转换成对象 End---");
    }

    //    @Test
    public void testXmlConvertCost() throws Exception {
        final DalConfigure dalConfigure = generateDalConfigure();
        final String xml = XmlUtils.toXml(dalConfigure);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        xml2Object(xml);
                        object2Xml(dalConfigure);
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        Thread.currentThread().join();
    }

    private void object2Xml(Object object) {
        Transaction transaction = Cat.newTransaction("XmlConvert.Cost", "Object2Xml");
        try {
            String str = XmlUtils.toXml(object);
            transaction.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
            Cat.logError(e);
        } finally {
            transaction.complete();
        }
    }

    private void xml2Object(String xml) {
        Transaction transaction = Cat.newTransaction("XmlConvert.Cost", "Xml2Object");
        try {
            DalConfigure configure = (DalConfigure) XmlUtils.fromXml(xml, DalConfigure.class);
            transaction.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(e);
            Cat.logError(e);
        } finally {
            transaction.complete();
        }
    }

    private DalConfigure generateDalConfigure() {
        Database masterDatabase = new Database("master", "10.28.11.1", 55944, "bbzmembersinfoshard1db",
                "w_bbzminfo1", "123456", 1, null);
        Database slaveDatabase1 = new Database("slave", "10.28.33.1", 55944, "bbzmembersinfoshard1db",
                "w_bbzminfo1_r", "123456", 1, "");
        Database slaveDatabase2 = new Database("slave", "10.28.44.1", 55944, "bbzmembersinfoshard1db",
                "w_bbzminfo1_r", "123456", 1, "");
        DatabaseShard databaseShard = new DatabaseShard(0, "masterDomain", "slaveDomain", 55944, 55944,
                "masterTitanKey", "slaveTitanKey", Lists.newArrayList(masterDatabase, slaveDatabase1, slaveDatabase2));

        DatabaseShards databaseShards = new DatabaseShards(Lists.newArrayList(databaseShard, databaseShard));
        Cluster cluster = new Cluster("clusterbbzmembersinfo", "mysql", 1, databaseShards);
        cluster.setSslCode("VZ00000000000441");
        cluster.setOperator("testUser");
        cluster.setUpdateTime(DalClusterUtils.formatDate(new Date()));

        DalConfigure dalConfigure = new DalConfigure(cluster);

        return dalConfigure;
    }

    @Test
    public void testXmlConvert() throws Exception {
        DalConfigure input = generateExtendedDalConfigure();
        String serverContent = XmlUtils.toXml(input);
        DalConfigure output = (DalConfigure) XmlUtils.fromXml(serverContent, DalConfigure.class);
        String clientContent = XmlUtils.toXml(output);
        Assert.assertEquals(serverContent, clientContent);
    }

    private DalConfigure generateExtendedDalConfigure() {
        Database masterDatabase = new Database("master", "10.28.11.1", 55944, "bbzmembersinfoshard1db",
                "w_bbzminfo1", "123456", 1, null);
        Database slaveDatabase1 = new Database("slave", "10.28.33.1", 55944, "bbzmembersinfoshard1db",
                "w_bbzminfo1_r", "123456", 1, "");
        Database slaveDatabase2 = new Database("slave", "10.28.44.1", 55944, "bbzmembersinfoshard1db",
                "w_bbzminfo1_r", "123456", 1, "");
        DatabaseShard databaseShard = new DatabaseShard(0, "masterDomain", "slaveDomain", 55944, 55944,
                "masterTitanKey", "slaveTitanKey", Lists.newArrayList(masterDatabase, slaveDatabase1, slaveDatabase2));

        DatabaseShards databaseShards = new DatabaseShards(Lists.newArrayList(databaseShard, databaseShard));
        Cluster cluster = new Cluster("clusterbbzmembersinfo", "mysql", 1, databaseShards);
        cluster.setSslCode("VZ00000000000441");
        cluster.setOperator("testUser");
        cluster.setUpdateTime(DalClusterUtils.formatDate(new Date()));

        cluster.setShardStrategiesText("<ModStrategy><Property name=\"dbShardColumn\" value=\"id\"/><Property name=\"dbShardMod\" value=\"4\"/><Tables><Table name=\"strategy_meta\"/></Tables></ModStrategy><CustomStrategy class=\"cluster.FltShardStrategy\" default=\"true\"><Property name=\"dbShardColumn\" value=\"age\"/><Property name=\"dbShardMod\" value=\"4\"/></CustomStrategy>");
        cluster.setIdGeneratorsText("<IdGenerator><includes><include><tables><table>person</table></tables></include></includes></IdGenerator>");

        DalConfigure dalConfigure = new DalConfigure(cluster);

        return dalConfigure;
    }

}
