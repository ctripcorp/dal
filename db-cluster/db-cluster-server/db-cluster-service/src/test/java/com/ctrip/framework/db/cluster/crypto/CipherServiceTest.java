package com.ctrip.framework.db.cluster.crypto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by shenjie on 2019/3/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CipherServiceTest {

    @Autowired
    private CipherService cipherService;

    @Test
    public void encrypt() throws Exception {
        String content = "Hello Key!";
        String encrypted = cipherService.encrypt(content);
        String decrypted = cipherService.decrypt(encrypted);
        assert decrypted.equals(content);
    }

    @Test
    public void decrypt() throws Exception {
        String content = "bvbtxcxqa0hmipxjdzvp";
        String encrypted = "6AB19295D24759A65DF2CC32B76B6947EB65CBE6C74A5585F7F486574045ADFC";
        String decrypted = cipherService.decrypt(encrypted);
        assert decrypted.equals(content);
    }

}