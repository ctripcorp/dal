package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.entity.*;
import com.ctrip.framework.db.cluster.service.ClusterService;
import com.dianping.cat.Cat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Created by taochen on 2019/11/5.
 */
@RestController
@RequestMapping("/console")
public class ClusterController {

    @Autowired
    private ClusterService clusterService;

    @RequestMapping(value = "clusters/{clusterName}", method = RequestMethod.GET)
    public Cluster loadCluster(@PathVariable String clusterName) {
//        ClusterResponse response = new ClusterResponse();
//        try {
//
//            response.setStatus(ResponseStatus.SUCCESS);
//        } catch (Exception e) {
//            Cat.logError(e);
//            response.setStatus(ResponseStatus.FAIL);
//        }
//        return response;
        return clusterService.getCluster(clusterName).getResult();
    }

    @RequestMapping(value = "/clusters/all", method = RequestMethod.GET)
    public List<String> loadAllCluster() {
//        ClusterResponse response = new ClusterResponse();
//        try {
//
//            response.setStatus(ResponseStatus.SUCCESS);
//        } catch (Exception e) {
//            Cat.logError(e);
//            response.setStatus(ResponseStatus.FAIL);
//        }
        return clusterService.getAllClusters().getResult();
    }

    @RequestMapping(value = "/clusters/{clusterName}/zones", method = RequestMethod.GET)
    public List<Zone> findClusterZones(@PathVariable String clusterName) {
        return findClusterZones(clusterName);
    }
}
