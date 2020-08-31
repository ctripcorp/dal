package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.Resource;
import com.ctrip.platform.dal.dao.configure.ResourceLoader;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class CtripLocalResourceLoaderTest {

    @Test
    public void testGetResource1() {
        CtripLocalResourceLoader loader = new CtripLocalResourceLoader();
        assertResource(loader, "Test-Resource1", "test-resource1");
        assertResource(loader, "test-resource1", "test-resource1");
        assertResource(loader, "Test-Resource2", "test-resource2");
        assertResource(loader, "test-resource-fx-only", "fx-test-resource");
        assertResource(loader, "test-resource-classpath-only", "classpath-test-resource");
        assertResource(loader, "no-resource", null);
    }

    @Test
    public void testGetResource2() {
        CtripLocalContext context = new CtripLocalContextImpl("/abc/xyz/");
        CtripLocalResourceLoader loader = new CtripLocalResourceLoader(context);
        assertResource(loader, "Test-Resource1", "test-resource1");
        assertResource(loader, "test-resource1", "test-resource1");
        assertResource(loader, "Test-Resource2", "test-resource2");
        assertResource(loader, "test-resource-fx-only", "fx-test-resource");
        assertResource(loader, "test-resource-classpath-only", "classpath-test-resource");
        assertResource(loader, "no-resource", null);
    }

    @Test
    public void testGetResource3() {
        CtripLocalContext context = new CtripLocalContextImpl(CtripLocalResourceLoader.PATH_CLASSPATH);
        CtripLocalResourceLoader loader = new CtripLocalResourceLoader(context);
        assertResource(loader, "Test-Resource1", "classpath-test-resource1");
        assertResource(loader, "test-resource1", "classpath-test-resource1");
        assertResource(loader, "Test-Resource2", "classpath-test-resource2");
        assertResource(loader, "test-resource-fx-only", "fx-test-resource");
        assertResource(loader, "test-resource-classpath-only", "classpath-test-resource");
        assertResource(loader, "no-resource", null);
    }

    @Test
    public void testGetResource4() {
        CtripLocalContext context = new CtripLocalContextImpl(null, false);
        CtripLocalResourceLoader loader = new CtripLocalResourceLoader(context);
        assertResource(loader, "Test-Resource1", "classpath-test-resource1");
        assertResource(loader, "test-resource1", "classpath-test-resource1");
        assertResource(loader, "Test-Resource2", "classpath-test-resource2");
        assertResource(loader, "test-resource-fx-only", null);
        assertResource(loader, "test-resource-classpath-only", "classpath-test-resource");
        assertResource(loader, "no-resource", null);
    }

    @Test
    public void testGetResource5() {
        CtripLocalContext context = new CtripLocalContextImpl(CtripLocalResourceLoader.PATH_CLASSPATH, false);
        CtripLocalResourceLoader loader = new CtripLocalResourceLoader(context);
        assertResource(loader, "Test-Resource1", "classpath-test-resource1");
        assertResource(loader, "test-resource1", "classpath-test-resource1");
        assertResource(loader, "Test-Resource2", "classpath-test-resource2");
        assertResource(loader, "test-resource-fx-only", null);
        assertResource(loader, "test-resource-classpath-only", "classpath-test-resource");
        assertResource(loader, "no-resource", null);
    }

    private void assertResource(ResourceLoader<String> loader, String resourceName, String expectedContent) {
        Resource<String> resource = loader.getResource(resourceName);
        if (expectedContent != null) {
            Assert.assertNotNull(resource);
            Assert.assertEquals(expectedContent, resource.getContent());
        } else
            Assert.assertNull(resource);
    }

}
