package com.ctrip.platform.dal.daogen.util;

import com.ctrip.platform.dal.daogen.entity.ClusterListResponse;
import com.ctrip.platform.dal.daogen.utils.ClusterInfoApi;
import com.dianping.cat.Cat;
import org.springframework.web.client.RestTemplate;

public class DalClusterInfoApi implements ClusterInfoApi {

    private static final String DB_CLUSTER_GET_CLUSTERS_FAT = "http://service.dbcluster.fws.qa.nt.ctripcorp.com/api/dal/v2/clusters?operator=dbclusteradmin";

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Override
    public ClusterListResponse getClusterListDb() {
        ClusterListResponse response = null;
        try {
            response = REST_TEMPLATE.getForObject(DB_CLUSTER_GET_CLUSTERS_FAT, ClusterListResponse.class);
        } catch (Exception e) {
            Cat.logError("get all cluster fail!", e);
        }
        return response;
    }
}
