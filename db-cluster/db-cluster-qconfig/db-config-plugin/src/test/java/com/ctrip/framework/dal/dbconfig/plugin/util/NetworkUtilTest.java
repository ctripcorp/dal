package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import org.junit.Test;

/**
 * Created by shenjie on 2019/5/13.
 */
public class NetworkUtilTest {

    @Test
    public void getClientIp() throws Exception {
    }

    @Test
    public void checkIpTest() throws Exception {
        String netType = TitanConstants.PUBLIC_NET_TYPE;
        assert NetworkUtil.isFromPublicNet(netType);

        netType = TitanConstants.PRIVATE_NET_TYPE;
        assert !NetworkUtil.isFromPublicNet(netType);

        netType = null;
        assert !NetworkUtil.isFromPublicNet(netType);

        netType = "";
        assert !NetworkUtil.isFromPublicNet(netType);

        netType = "test";
        assert !NetworkUtil.isFromPublicNet(netType);
    }

}