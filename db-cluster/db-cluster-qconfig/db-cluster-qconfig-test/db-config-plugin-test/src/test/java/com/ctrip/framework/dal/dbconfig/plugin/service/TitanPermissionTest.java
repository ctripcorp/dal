package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.ConfigUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.Utils;
import com.ctrip.framework.foundation.Foundation;
import org.apache.logging.log4j.util.Strings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by shenjie on 2019/4/10.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TitanPermissionTest {
    public static final String FAT_ENV = "fat";
    public static final String UAT_ENV = "uat";
    public static final String PRO_ENV = "pro";
    public static final String TITAN_KEY = "titantest_shenjie_v_01";
    public static final String RUN_IN_BIG_DATA = "false";

    @Autowired
    private TitanPluginService titanPluginService;

    @Test
    public void getClientConfig() throws Exception {
        // need add vm option.
//        Utils.addLocalVmOptions();
        Utils.addQConfig2Fat1VmOptions();
        String content = ConfigUtils.getTitanFileResult(TITAN_KEY);
        assert Strings.isNotBlank(content);
        System.out.println(content);
    }

    @Test
    public void addPermissionAndGetClientConfig() throws Exception {
        // add permission
        PluginResponse response = titanPluginService.addPermissions(FAT_ENV, TITAN_KEY, Foundation.app().getAppId(), RUN_IN_BIG_DATA);
        assert response.getStatus() == 0;

        // need add vm option.
//        Utils.addLocalVmOptions();
        Utils.addQConfig2Fat1VmOptions();
        String content = ConfigUtils.getTitanFileResult(TITAN_KEY);
        assert Strings.isNotBlank(content);
    }

    @Test
    public void getClientConfigFailed() throws Exception {
        // delete permission
        PluginResponse response = titanPluginService.deletePermissions(TITAN_KEY, Foundation.app().getAppId());
        assert response.getStatus() == 0;

        // no permission, failed.
        boolean isSuccess = true;
        try {
            // get client config from fat16, need add vm option.
            System.setProperty("qconfig.admin", "qconfig.fat16.qa.nt.ctripcorp.com");
            System.setProperty("qserver.http.urls", "10.5.80.175:8080");
            System.setProperty("qserver.https.urls", "10.5.80.175:8443");
            String content = ConfigUtils.getTitanFileResult(TITAN_KEY);
            assert Strings.isNotBlank(content);
        } catch (Exception e) {
            isSuccess = false;
        }
        assert !isSuccess;
    }

    //    @Test
    public void addPermissions() throws Exception {
        PluginResponse response = titanPluginService.addPermissions(FAT_ENV, TITAN_KEY, Foundation.app().getAppId(), RUN_IN_BIG_DATA);
        assert response.getStatus() == 0;
    }

    @Test
    public void deletePermissions() throws Exception {
        PluginResponse response = titanPluginService.deletePermissions(TITAN_KEY, Foundation.app().getAppId());
        assert response.getStatus() == 0;
    }

    @Test
    public void addFreeVerify() throws Exception {
        FreeVerifyRequest freeVerifyRequest = FreeVerifyRequest.builder()
                .titanKeyList(TITAN_KEY)
                .freeVerifyAppIdList(Foundation.app().getAppId())
                .build();

        PluginResponse response = titanPluginService.addFreeVerify(freeVerifyRequest, FAT_ENV);
        assert response.getStatus() == 0;
    }

    @Test
    public void deleteFreeVerify() throws Exception {
        FreeVerifyRequest freeVerifyRequest = FreeVerifyRequest.builder()
                .titanKeyList(TITAN_KEY)
                .freeVerifyAppIdList(Foundation.app().getAppId())
                .build();
        PluginResponse response = titanPluginService.deleteFreeVerify(freeVerifyRequest, FAT_ENV);
        assert response.getStatus() == 0;
    }

    @Test
    public void mergeTitanKeyPermission() throws Exception {
        TitanKeyEntity titanKeyEntity = generateTitanKey();
        PluginResponse response = titanPluginService.mergeTitanKeyPermission(titanKeyEntity, FAT_ENV);
        assert response.getStatus() == 0;

        // get titan key
        TitanKeyGetResponse titanKeyGetResponse = titanPluginService.getTitanKey(TITAN_KEY, FAT_ENV);
        assert titanKeyGetResponse.getStatus() == 0;

        TitanKeyGetOutputEntity entity = titanKeyGetResponse.getData();
        TitanKeyEntity titanKey = generateTitanKey();
        assert titanKey.getKeyName().equalsIgnoreCase(entity.getKeyName());
        assert entity.getPermissions().indexOf(titanKey.getWhiteList()) >= 0;
    }

    private TitanKeyEntity generateTitanKey() {
        TitanKeyEntity titanKeyEntity = TitanKeyEntity.builder()
                .keyName(TITAN_KEY)
                .whiteList("999999")
                .build();

        return titanKeyEntity;
    }
}
