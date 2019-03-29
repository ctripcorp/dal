package com.ctrip.framework.dal.dbconfig.plugin.handler.permission;

import com.ctrip.framework.dal.dbconfig.plugin.context.IEnvProfile;

/**
 * @author c7ch23en
 */
public interface PermissionValidator {

    boolean validateWebRequest(String sourceIp, IEnvProfile envProfile);

    // TODO: add data content parameter
    boolean validateClientRequest(String appId, String clientIp, IEnvProfile envProfile);

}
