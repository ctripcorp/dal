package com.ctrip.datasource.dynamicdatasource.QConfigConnectionStringProvider;

import com.ctrip.datasource.configure.qconfig.ConnectionStringProviderImpl;
import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringProvider;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.MapConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QConfigConnectionStringProviderTest {
    private static final String TITAN_APP_ID = "100010061";
    private static final String normal = DataSourceConfigureConstants.TITAN_KEY_NORMAL;
    private static final String failover = DataSourceConfigureConstants.TITAN_KEY_FAILOVER;

    // @Test
    public void testGetConnectionStringKeysOneByOne() {
        String[] keys = getTitanKeys();
        StopWatch sw = new StopWatch();
        sw.start();
        for (String key : keys) {
            MapConfig config = getTitanMapConfig(key);
            Map<String, String> map = config.asMap();
            String normalKey = map.get(normal);
            String failoverKey = map.get(failover);
        }
        sw.stop();
        System.out.println(sw.getTime());
    }

    // @Test
    public void testGetConnectionStringKeysAtOneShoot() {
        String[] keys = getTitanKeys();
        Map<String, MapConfig> configs = new HashMap<>();
        StopWatch sw = new StopWatch();
        sw.start();
        for (String key : keys) {
            MapConfig config = getTitanMapConfig(key);
            configs.put(key, config);
        }

        for (Map.Entry<String, MapConfig> entry : configs.entrySet()) {
            MapConfig config = entry.getValue();
            Map<String, String> map = config.asMap();
            String normalKey = map.get(normal);
            String failoverKey = map.get(failover);
        }

        sw.stop();
        System.out.println(sw.getTime());
    }

    private String[] getTitanKeys() {
        return new String[] {"HotelInputDB_SELECT_1", "HotelPubDB_SELECT_2", "HotelPubDB_INSERT_2",
                "HotelInputDB_INSERT_1", "eBookingDB_SELECT_2", "htlroominforecordshard15db_W",
                "htlroominforecordshard20db_W", "htlroominforecordshard19db_W", "htlroominforecordshard21db_W",
                "htlroominforecordshard18db_W", "eBookingDB_INSERT_2", "HtlOverseasPubShard41DB",
                "HtlOverseasPubShard44DB", "HtlRoomsPubShard01DB_W", "HtlRoomsPubShard02DB_W", "HtlRoomsPubShard00DB_W",
                "HtlOverseasPubShard42DB", "HtlOverseasPubShard11DB", "HtlOverseasPubShard26DB",
                "HtlOverseasPubShard46DB", "HtlOverseasPubShard08DB", "HtlOverseasPubShard45DB",
                "HtlOverseasPubShard40DB", "HtlOverseasPubShard09DB", "HtlOverseasPubShard07DB",
                "HtlOverseasPubShard51DB", "HtlOverseasPubShard43DB", "HtlOverseasPubShard14DB", "htlinputlogdb",
                "HtlOverseasPubShard47DB", "HtlOverseasPubShard24DB", "HtlOverseasPubShard35DB",
                "HtlRoomsPubShard28DB_R", "HtlRoomsPubShard26DB_R", "HtlOverseasPubShard27DB",
                "HtlOverseasPubShard13DB", "HtlOverseasPubShard58DB", "htlroominforecordshard33db_W",
                "htlroominforecordshard22db_W", "htlovspubmShard63db_W", "HtlOverseasPubShard48DB",
                "HtlOverseasPubShard01DB", "htlovspubmShard61db_W", "HtlRoomsPubShard06DB_W", "HtlRoomsPubShard07DB_W",
                "HtlOverseasPubShard12DB", "HtlOverseasPubShard10DB", "HtlOverseasPubShard17DB",
                "HtlOverseasPubShard29DB", "HtlRoomsPubShard25DB_W", "htlovspubmShard56db_W", "HtlRoomsPubShard21DB_W",
                "HtlRoomsPubShard06DB_R", "HtlRoomsPubShard09DB_R", "htlroominforecordshard14db_W",
                "htlroominforecordshard08db_W", "HtlOverseasPubShard18DB", "HtlOverseasPubShard15DB",
                "htlovspubmShard35db_W", "HtlRoomsPubShard08DB_W", "HtlOverseasPubShard22DB", "HtlRoomsPubShard21DB_R",
                "HtlOverseasPubShard23DB", "HtlRoomsPubShard20DB_R", "HtlRoomsPubShard31DB_R", "htlovspubmShard44db_W",
                "htlovspubmShard47db_W", "HtlRoomsPubShard19DB_R", "htlroominforecordshard01db_W",
                "htlroominforecordshard36db_W", "htlroominforecordshard13db_W", "htlovspubmShard37db_W",
                "HtlRoomsPubShard05DB_W", "HtlRoomsPubShard09DB_W", "htlroominforecordshard25db_W",
                "htlovspubmShard57db_W", "HtlRoomsPubShard18DB_R", "HtlOverseasPubShard34DB", "HtlProductInputDB_W",
                "htlovspubmShard40db_W", "HtlOverseasPubShard55DB", "htlovspubmShard39db_W", "HtlOverseasPubShard38DB",
                "HtlOverseasPubShard77DB", "HtlRoomsPubShard05DB_R", "HtlOverseasPubShard80DB",
                "HtlRoomsPubShard03DB_W", "htlovspubmShard41db_W", "HtlOverseasPubShard65DB", "HtlRoomsPubShard04DB_W",
                "htlovspubmShard50db_W", "HtlOverseasPubShard31DB", "HtlOverseasPubShard30DB",
                "HtlOverseasPubShard32DB", "htlovspubmShard42db_W", "HtlOverseasPubShard56DB", "HtlRoomsPubShard01DB_R",
                "HtlOverseasPubShard62DB", "HtlOverseasPubShard57DB", "HtlOverseasPubShard28DB",
                "HtlRoomsPubShard17DB_R", "HtlRoomsPubShard28DB_W", "HtlRoomsPubShard20DB_W", "HtlOverseasPubShard50DB",
                "htlovspubmShard58db_W", "htlovspubmShard59db_W", "HtlRoomsPubShard27DB_W", "HtlOverseasPubShard49DB",
                "htlovspubmShard45db_W", "HtlOverseasPubShard33DB", "htlovspubmShard55db_W", "HtlRoomsPubShard08DB_R",
                "HtlOverseasPubShard04DB", "HtlOverseasPubShard52DB", "htlroominforecordshard16db_W",
                "htlovspubmShard01db_W", "HtlRoomsPubShard14DB_R", "htlroominforecordshard27db_W",
                "HtlOverseasPubShard53DB", "htlroominforecordshard11db_W", "HtlRoomsPubShard07DB_R",
                "HtlRoomsPubShard30DB_R", "htlroominforecordshard03db_W", "htlovspubmShard34db_W",
                "HtlOverseasPubShard20DB", "HtlOverseasPubShard19DB", "htlroominforecordshard37db_W",
                "HtlOverseasPubShard70DB", "HtlOverseasPubShard25DB", "HtlRoomsPubShard29DB_R",
                "htlroominforecordshard28db_W", "HtlRoomsPubShard25DB_R", "htlovspubmShard38db_W",
                "htlroominforecordshard34db_W", "htlroominforecordshard12db_W", "HtlOverseasPubShard16DB",
                "htlovspubmShard31db_W", "HtlRoompriceBakDB_W", "htlroominforecordshard07db_W",
                "HtlRoomsPubShard04DB_R", "htlroominforecordshard26db_W", "htlovspubmShard08db_W",
                "HtlOverseasPubShard69DB", "HtlOverseasPubShard84DB", "HtlRoomsPubShard10DB_R", "htlovspubmShard60db_W",
                "htlroominforecordshard09db_W", "htlovspubmShard54db_W", "HtlOverseasPubShard37DB",
                "HtlOverseasPubShard02DB", "HtlRoomsPubShard11DB_R", "htlroominforecordshard52db_W",
                "htlroominforecordshard55db_W", "HtlRoomsPubShard12DB_R", "HtlRoomsPubShard22DB_W",
                "HtlRoomsPubShard02DB_R", "HtlRoomsPubShard00DB_R", "HtlOverseasPubShard03DB",
                "htlroominforecordshard35db_W", "htlovspubmShard26db_W", "htlovspubmShard27db_W",
                "HtlRoomsPubShard23DB_W", "HtlOverseasPubShard81DB", "HtlRoomsPubShard03DB_R", "htlovspubmShard24db_W",
                "htlroominforecordshard53db_W", "htlroominforecordshard05db_W", "HtlRoomsPubShard13DB_W",
                "htlroominforecordshard06db_W", "HtlRoomsPubShard24DB_W", "HtlOverseasPubShard79DB",
                "htlovspubmShard25db_W", "htlroominforecordshard54db_W", "htlovspubmShard53db_W",
                "HtlOverseasPubShard128DB", "HtlOverseasPubShard06DB", "htlroominforecordshard23db_W",
                "htlroominforecordshard44db_W", "htlovspubmShard43db_W", "htlroominforecordshard17db_W",
                "HtlOverseasPubShard39DB", "htlovspubmShard06db_W", "HtlRoomsPubShard11DB_W", "htlovspubmShard64db_W",
                "HtlRoomsPubShard12DB_W", "htlovspubmShard48db_W", "HtlRoomsPubShard22DB_R", "htlovspubmShard19db_W",
                "HtlRoomsPubShard14DB_W", "htlroominforecordshard45db_W", "HtlInputMsgQueueDB_W",
                "htlovspubmShard18db_W", "htlovspubmShard16db_W", "HtlRoomsPubShard30DB_W", "HtlOverseasPubShard05DB",
                "htlroominforecordshard02db_W", "HtlRoomsPubShard29DB_W", "htlovspubmShard62db_W",
                "htlroominforecordshard24db_W", "htlovspubmShard17db_W", "HtlRoomsPubShard10DB_W",
                "HtlOverseasPubShard71DB", "HtlOverseasPubShard83DB", "HtlOverseasPubShard82DB",
                "HtlOverseasPubShard36DB", "htlroominforecordshard39db_W", "HtlOverseasPubShard97DB",
                "htlovspubmShard09db_W", "HtlOverseasPubShard72DB", "htlovspubmShard04db_W", "HtlOverseasPubShard59DB",
                "htlovspubmShard29db_W", "htlroominforecordshard57db_W", "HtlRoomsPubShard15DB_R",
                "htlroominforecordshard38db_W", "htlroominforecordshard32db_W", "HtlRoomsPubShard23DB_R",
                "HtlOverseasPubShard98DB", "htlroominforecordshard40db_W", "htlovspubmShard13db_W",
                "htlroominforecordshard59db_W", "HtlOverseasPubShard63DB", "htlovspubmShard32db_W",
                "htlroominforecordshard63db_W", "HtlOverseasPubShard99DB", "htlroominforecordshard58db_W",
                "htlroominforecordshard56db_W", "HtlRoomsPubShard16DB_R", "htlovspubmShard12db_W",
                "HtlOverseasPubShard75DB", "htlovspubmShard14db_W", "HtlOverseasPubShard73DB", "htlovspubmShard23db_W",
                "htlroominforecordshard30db_W", "HtlRoomsPubShard17DB_W", "htlovspubmShard51db_W",
                "HtlOverseasPubShard76DB", "htlovspubmShard49db_W", "htlovspubmShard21db_W", "HtlOverseasPubShard100DB",
                "htlroominforecordshard62db_W", "htlovspubmShard22db_W", "HtlRoomsPubShard26DB_W", "HotelPubDB_R",
                "HtlRoomsPubShard18DB_W", "htlroominforecordshard10db_W", "htlroominforecordshard61db_W",
                "htlovspubmShard52db_W", "HtlOverseasPubShard64DB", "htlroominforecordshard31db_W",
                "HtlOverseasPubShard78DB", "HtlRoomsPubShard19DB_W", "htlovspubmShard20db_W", "htlovspubmShard30db_W",
                "htlovspubmShard46db_W", "HtlOverseasPubShard86DB", "HtlOverseasPubShard21DB",
                "htlroominforecordshard46db_W", "HtlOverseasPubShard87DB", "htlovspubmShard07db_W",
                "HtlRoomsPubShard15DB_W", "HtlOverseasPubShard54DB", "htlovspubmShard10db_W", "HtlOverseasPubShard60DB",
                "htlroominforecordshard29db_W", "HtlOverseasPubShard66DB", "HtlOverseasPubShard61DB",
                "HtlRoomsPubShard16DB_W", "htlovspubmShard05db_W", "HtlOverseasPubShard68DB", "HtlOverseasPubShard67DB",
                "htlovspubmShard36db_W", "htlroominforecordshard42db_W", "htlovspubmShard33db_W",
                "htlovspubmShard15db_W", "HtlInputRequestDB_W", "HtlRoomsPubShard13DB_R", "HtlOverseasPubShard95DB",
                "HtlOverseasPubShard96DB", "HtlOverseasPubShard93DB", "HtlOverseasPubShard94DB",
                "htlroominforecordshard60db_W", "htlroominforecordshard48db_W", "htlroominforecordshard50db_W",
                "htlroominforecordshard51db_W", "htlroominforecordshard49db_W", "htlroominforecordshard04db_W",
                "HTLRoomInfoRecDB", "HtlOverseasPubShard90DB", "HtlOverseasPubShard92DB", "HtlOverseasPubShard91DB",
                "HtlRoomsPubShard24DB_R", "HtlOverseasPubShard89DB", "htlroominforecordshard43db_W",
                "htlroominforecordshard64db_W", "HtlOverseasPubShard74DB", "HtlOverseasPubShard88DB",
                "htlovspubmShard02db_W", "htlroominforecordshard47db_W", "htlovspubmShard03db_W",
                "htlroominforecordshard41db_W", "htlovspubmShard11db_W", "HtlRoomsPubShard27DB_R",
                "HtlRoomsPubShard31DB_W", "HtlProductPropertyDB", "HtlOverseasPubShard85DB", "htlovspubmShard28db_W"};
    }

    private MapConfig getTitanMapConfig(String name) {
        Feature feature = Feature.create().setHttpsEnable(true).build();
        return MapConfig.get(TITAN_APP_ID, name, feature);
    }

    @Test
    public void testTitanKeysNotFoundExceptionMessage() {
        ConnectionStringProvider connectionStringProvider = new ConnectionStringProviderImpl();
        String name = "non_exist";
        Set<String> names = new HashSet<>();
        names.add(name);
        try {
            Map<String, ConnectionString> connectionStrings = connectionStringProvider.getConnectionStrings(names);
            Assert.fail();
        } catch (Throwable e) {
            Assert.assertTrue(e.getMessage().equals(
                    "Titan key non_exist does not exist or has been disabled, please remove it from your Dal.config or code."));
        }
    }

}
