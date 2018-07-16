package provider;

import com.ctrip.platform.dal.dao.configure.ConnectionString;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringChanged;

import java.util.HashMap;
import java.util.Map;

public class LocalConnectionStringProviderImpl extends AbstractConnectionStringProvider
        implements DataSourceConfigureConstants {

    private String connectionString2 =
            "Server=10.32.21.149;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_1;";

    private String connectionString2Failover =
            "Server=DST56614;port=3306;UID=root;password=!QAZ@WSX1qaz2wsx;database=dal_shard_1;";

    private HashMap<String,ConnectionStringChanged> callbackMap = new HashMap<>();

    public void triggerConnectionStringChanged(Map.Entry<Integer,String> keyMapEntry) {
//        for(Map.Entry<Integer,String> entry : keyMap.entrySet()) {
            Integer version = keyMapEntry.getKey();
            String name = keyMapEntry.getValue();
            Map<String, String> map = new HashMap<>();
            String newConnectionString2=connectionString2 + "version=" + version;
            String newConnection2Failover=connectionString2Failover+"version=" + version;
            map.put(TITAN_KEY_NORMAL, newConnectionString2);
            map.put(TITAN_KEY_FAILOVER, newConnection2Failover);
            ConnectionString connectionString = new ConnectionString(name, newConnectionString2, newConnection2Failover);
            callbackMap.get(name.toLowerCase()).onChanged(connectionString);
//        }
    }

    @Override
    public void addConnectionStringChangedListener(String name, final ConnectionStringChanged callback) {
        if (callback == null)
            return;

        this.callbackMap.put(name,callback);
    }

}
