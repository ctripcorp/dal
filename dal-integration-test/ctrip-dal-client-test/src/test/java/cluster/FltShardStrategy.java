package cluster;

import com.ctrip.framework.dal.cluster.client.sharding.strategy.ModShardStrategy;

public class FltShardStrategy extends ModShardStrategy {

    private FltShardService service = new FltShardService();

    @Override
    protected Long getLongValue(Object value) {
        if (value == null) {
            throw new RuntimeException("subOrderId is null");
        }

        if (!value.getClass().equals(Long.TYPE) && !value.getClass().equals(Long.class)) {
//            throw new RuntimeException("Shard value must be instance of java.lang.Long");
        }

        long subOrderId = ((Number)value).longValue();
        if (subOrderId <= 0) {
            throw new RuntimeException(String.format("subOrderId: %s is less than or equal to zero", subOrderId));
        }

        // 根据子订单号获取主订单号
        Long mainOrderId = getMainOrderId(subOrderId);
        if (mainOrderId == null) {
            throw new RuntimeException("MainOrderId is null");
        }
        return mainOrderId;
    }

    private Long getMainOrderId(Long subOrderId) {
        try {
            return service.getShardValue(subOrderId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
