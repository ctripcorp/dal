package com.ctrip.datasource.datasource.MockQConfigProvider;


import com.ctrip.datasource.common.enums.SourceType;
import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.datasource.titan.DataSourceConfigureManager;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.datasource.DataSourceLocator;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    @Test
    public void testCreateDataSourceAfterFailedGetPoolProperties() throws Exception {
        String inValidKey = "name1";
        String validKey = "dalservice2db_w";
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(new FailedQConfigPoolPropertiesProvider());
        Map<String, String> settings = new HashMap<>();
        settings.put("ignoreExternalException", "true");
        DataSourceConfigureManager.getInstance().initialize(settings);
        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(inValidKey, true);
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(validKey, true);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateDataSourceAfterFailedGetPoolProperties2() throws Exception {
        String inValidKey = "name1";
        String validKey = "dalservice2db_w";
        DataSourceConfigureManager.getInstance().setPoolPropertiesProvider(new FailedQConfigPoolPropertiesProvider());
        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(inValidKey, true);
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(validKey, true);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateDataSourceAfterFailedGetIpDomain() throws Exception{
        String inValidKey = "name1";
        String validKey = "dalservice2db_w";
        DataSourceConfigureManager.getInstance().setIPDomainStatusProvider(new FailedQConfigIPDomainStatusProvider());
        Map<String, String> settings = new HashMap<>();
        settings.put("ignoreExternalException", "true");
        DataSourceConfigureManager.getInstance().initialize(settings);
        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(inValidKey, true);
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(validKey, true);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCreateDataSourceAfterFailedGetIpDomain2() throws Exception{
        String inValidKey = "name1";
        String validKey = "dalservice2db_w";
        DataSourceConfigureManager.getInstance().setIPDomainStatusProvider(new FailedQConfigIPDomainStatusProvider());
        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(inValidKey, true);
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            DalDataSourceFactory factory = new DalDataSourceFactory();
            factory.createDataSource(validKey, true);
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
