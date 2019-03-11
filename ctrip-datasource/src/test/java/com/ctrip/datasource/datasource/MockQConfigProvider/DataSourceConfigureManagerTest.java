package com.ctrip.datasource.datasource.MockQConfigProvider;


import com.ctrip.datasource.common.enums.SourceType;
import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class DataSourceConfigureManagerTest {

    @Test
    public void testSetUpAfterFirstSuccessfulSetUp() throws Exception{
        DataSourceConfigureManager.getInstance().setConnectionStringProvider(new InvalidQConfigConnectionStringProvider());
        Set<String> keyNames=new HashSet<>();
        keyNames.add("name1");
        keyNames.add("name2");
        DataSourceConfigureManager.getInstance().setup(keyNames, SourceType.Remote);
        DataSourceLocator.containsKey("name1");
        DataSourceLocator.containsKey("name2");
    }

    @Test
    public void testSetUpAfterFirstFailedSetUp() throws Exception{
        DataSourceConfigureManager.getInstance().setConnectionStringProvider(new ExceptionQConfigConnectionStringProvider());
        Set<String> keyNames=new HashSet<>();
        keyNames.add("name1");
        keyNames.add("name2");
        try {
            DataSourceConfigureManager.getInstance().setup(keyNames, SourceType.Remote);
            Assert.fail();
        }catch (Exception e){
            e.printStackTrace();
        }
        DataSourceLocator.containsKey("name1");
        DataSourceLocator.containsKey("name2");

        try {
            DataSourceConfigureManager.getInstance().setup(keyNames, SourceType.Remote);
            Assert.fail();
        }catch (Exception e){
            e.printStackTrace();
        }
        DataSourceLocator.containsKey("name1");
        DataSourceLocator.containsKey("name2");
    }
}
