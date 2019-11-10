package com.ctrip.framework.dal.dbconfig.plugin.context;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author c7ch23en
 */
public class EnvProfileTest {

    @Test
    public void testFormatProfile1() {
        String env = "fat";
        EnvProfile envProfile = new EnvProfile(env);
        String profile = envProfile.formatProfile();
        Assert.assertEquals(profile, env + ":");
    }

    @Test
    public void testFormatProfile2() {
        String env = "fat:";
        EnvProfile envProfile = new EnvProfile(env);
        String profile = envProfile.formatProfile();
        Assert.assertEquals(profile, env);
    }

    @Test
    public void testFormatProfile3() {
        String env = "fat";
        String subEnv = "fat2";
        EnvProfile envProfile = new EnvProfile(env, subEnv);
        String profile = envProfile.formatProfile();
        Assert.assertEquals(profile, env + ":" + subEnv);
    }

    @Test
    public void testFormatTopProfile1() {
        String env = "fat";
        EnvProfile envProfile = new EnvProfile(env);
        String profile = envProfile.formatTopProfile();
        Assert.assertEquals(profile, env + ":");
    }

    @Test
    public void testFormatTopProfile2() {
        String env = "fat:";
        EnvProfile envProfile = new EnvProfile(env);
        String profile = envProfile.formatTopProfile();
        Assert.assertEquals(profile, env);
    }

    @Test
    public void testFormatTopProfile3() {
        String env = "fat";
        String subEnv = "fat2";
        EnvProfile envProfile = new EnvProfile(env, subEnv);
        String profile = envProfile.formatTopProfile();
        Assert.assertEquals(profile, env + ":");
    }

    @Test
    public void testFormatEnv1() {
        String env = "fat";
        EnvProfile envProfile = new EnvProfile(env);
        String profile = envProfile.formatEnv();
        Assert.assertEquals(profile, env);
    }

    @Test
    public void testFormatEnv2() {
        String env = "fat";
        EnvProfile envProfile = new EnvProfile(env + ":");
        String profile = envProfile.formatEnv();
        Assert.assertEquals(profile, env);
    }

    @Test
    public void testFormatEnv3() {
        String env = "fat";
        String subEnv = "fat2";
        EnvProfile envProfile = new EnvProfile(env, subEnv);
        String profile = envProfile.formatEnv();
        Assert.assertEquals(profile, env);
    }

    @Test
    public void testFormatSubEnv1() {
        String env = "fat";
        EnvProfile envProfile = new EnvProfile(env);
        String profile = envProfile.formatSubEnv();
        Assert.assertTrue(profile.isEmpty());
    }

    @Test
    public void testFormatSubEnv2() {
        String env = "fat:";
        EnvProfile envProfile = new EnvProfile(env);
        String profile = envProfile.formatSubEnv();
        Assert.assertTrue(profile.isEmpty());
    }

    @Test
    public void testFormatSubEnv3() {
        String env = "fat";
        String subEnv = "fat2";
        EnvProfile envProfile = new EnvProfile(env, subEnv);
        String profile = envProfile.formatSubEnv();
        Assert.assertEquals(profile, subEnv);
    }

}
