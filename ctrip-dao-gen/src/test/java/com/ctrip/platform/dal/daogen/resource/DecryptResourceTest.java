package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.decrypt.DecryptInfo;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class DecryptResourceTest {

    private DecryptResource decryptResource = new DecryptResource();

    @Test
    public void testJavaDecrypt() {
        DecryptInfo decryptInfo = decryptResource.getDecryptInfo("nonEncrypted");
        Assert.assertEquals("nonEncrypted", decryptInfo.getDecryptMsg());
    }

    @Test
    public void testNetDecrypt() {
        DecryptInfo decryptInfo = decryptResource.getDecryptInfo("nonEncrypted");
        Assert.assertEquals("nonEncrypted", decryptInfo.getDecryptMsg());
    }

}
