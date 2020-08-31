package com.ctrip.platform.dal.dao.configure;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class CommonFileLoaderTest {

    private final CommonFileLoader loader = new CommonFileLoader();

    @Test
    public void testGetResource() {
        DefaultResource resource = loader.getResource(getPathForResource("/Test-Resource1"));
        Assert.assertNotNull(resource);
        Assert.assertEquals("test-resource1", resource.getContent());
        resource = loader.getResource(getPathForResource("/test-resource1"));
        Assert.assertNotNull(resource);
        Assert.assertEquals("test-resource1", resource.getContent());
        resource = loader.getResource(getPathForResource("/Test-Resource2"));
        Assert.assertNotNull(resource);
        Assert.assertEquals("test-resource2", resource.getContent());
    }

    private String getPathForResource(String file) {
        return this.getClass().getResource(file).getPath();
    }

}
