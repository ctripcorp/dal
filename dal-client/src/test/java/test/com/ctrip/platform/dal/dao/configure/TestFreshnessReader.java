package test.com.ctrip.platform.dal.dao.configure;

import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.configure.FreshnessReader;

public class TestFreshnessReader implements FreshnessReader {
    private Map<String, Integer> freshnessMap = new HashMap<>();
    
    public TestFreshnessReader() {
        freshnessMap.put("MySqlShard_0", 3);
        freshnessMap.put("MySqlShard_1", 5);
        freshnessMap.put("dal_shard_0", 7);
        freshnessMap.put("dal_shard_1", 9);
    }
    
    
    @Override
    public int getSlaveFreshness(String logicDbName, String slaveDbName) {
        // Just return whant we have
        return freshnessMap.get(slaveDbName);
    }
}
