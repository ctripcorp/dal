package com.ctrip.framework.dal.dbconfig.plugin.util;

import org.junit.Test;

import javax.sound.midi.Soundbank;

import static org.junit.Assert.*;

/**
 * Created by shenjie on 2019/7/11.
 */
public class RC4Test {
    @Test
    public void encrypt() throws Exception {
    }

    @Test
    public void decrypt() throws Exception {
        String content = "EzcKEQUHHF4KDQcMAQAIDwkBCwEQERZxDxsBFwtdMzVLDwQRSxYEQBwRRwUQHAseFwwGJ3EBDAhJHhwbFk9bWlZFblkCOjBTBhMxFjEWFxAaOlBeBAQQFhQMFgBZOxsZMAA0F1U3PgslMAg2Myc9JgVfEAYRAwUPFABTDQ0aBAsaFRsUBQIAFwEQBgxdV19iZHJldG5lY2dpZm5vY3NuZA";
        String decrypted = RC4.decrypt(content);
        System.out.println(decrypted);
    }

}