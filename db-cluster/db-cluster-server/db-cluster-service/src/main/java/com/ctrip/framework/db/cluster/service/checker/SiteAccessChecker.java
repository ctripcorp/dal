package com.ctrip.framework.db.cluster.service.checker;

import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.util.IpUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Created by shenjie on 2019/8/8.
 */
@Slf4j
@Component
@AllArgsConstructor
public class SiteAccessChecker {

    private final ConfigService configService;

    public boolean isAllowed(final HttpServletRequest request) {
        String ip = IpUtils.getRequestIp(request);
        if (StringUtils.isEmpty(ip)) {
            return false;
        }

        Set<String> allowedIps = configService.getAllowedIps();
        if (allowedIps == null) {
            return false;
        }

        return allowedIps.contains(ip);
    }

}
