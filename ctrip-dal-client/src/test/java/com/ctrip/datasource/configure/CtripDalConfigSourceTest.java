package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DalConfigConstants;
import com.ctrip.platform.dal.dao.configure.DalConfigureFactory;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import org.junit.Test;

import java.util.Map;

public class CtripDalConfigSourceTest {
  @Test
  public void testGetDatabaseSets() throws Exception {

    CtripDalConfigSource configSource = new CtripDalConfigSource();
    try {
      Map<String, DatabaseSet> map = configSource.getDatabaseSets(null);
    } catch (Throwable e) {
    }

    /*
     * System.setProperty(DalConfigConstants.USE_LOCAL_DAL_CONFIG, "true"); DalConfigureFactory.load();
     */

  }
}
