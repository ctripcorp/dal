package com.ctrip.platform.dal.dao.configure;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class ClasspathResourceLoaderTest {

    private final ClasspathResourceLoader loader = new ClasspathResourceLoader();

    @Test
    public void testGetResource() {
        DefaultResource resource = loader.getResource("Test-Resource1");
        Assert.assertNotNull(resource);
        Assert.assertEquals("test-resource1", resource.getContent());
        resource = loader.getResource("test-resource1");
        Assert.assertNotNull(resource);
        Assert.assertEquals("test-resource1", resource.getContent());
        resource = loader.getResource("Test-Resource2");
        Assert.assertNotNull(resource);
        Assert.assertEquals("test-resource2", resource.getContent());
        resource = loader.getResource("test-resource2-null");
        Assert.assertNull(resource);
    }

}
