package shardTest.newVersionCodeTest;

import com.ctrip.platform.dal.dao.strategy.AbstractColumnShardStrategy;

/**
 * Created by lilj on 2018/3/9.
 */
public class FreeShardingStrategyOnSqlServer extends AbstractColumnShardStrategy {
    private Integer mod=2;
    private Integer tableMod=2;


    @Override
    public String calculateDbShard(Object value) {
        Long id = getLongValue(value);
        return String.valueOf(id%mod);
    }

    @Override
    public String calculateTableShard(String rawTableName, Object value){
        Long id = getLongValue(value);
        return String.valueOf(id%tableMod);
    }

    private Long getLongValue(Object value) {
        if(value == null)
            return null;

        if(value instanceof Long)
            return (Long)value;

        if(value instanceof Number)
            return ((Number)value).longValue();

        if(value instanceof String)
            return new Long((String)value);

        throw new RuntimeException(String.format("Shard value: %s can not be recoganized as int value", value.toString()));
    }
}
