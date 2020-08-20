package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;

/**
 * @author c7ch23en
 */
public class DefaultResource implements Resource<String> {

    private final String content;

    public DefaultResource(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public boolean isEmpty() {
        return StringUtils.isEmpty(content);
    }

}
