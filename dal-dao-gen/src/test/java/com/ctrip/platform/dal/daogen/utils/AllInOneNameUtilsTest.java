package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.dao.DaoOfDatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import com.ctrip.platform.dal.daogen.resource.CustomizedResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BeanGetter.class, CustomizedResource.class})
public class AllInOneNameUtilsTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    private DaoOfDatabaseSet daoOfDatabaseSet;

    @Test
    public void getAllInOneNameTest() throws SQLException {
        String dbname = "abtest_dalcluster";
        assertEquals(dbname, AllInOneNameUtils.getAllInOneName(dbname, "dalcluster"));

        PowerMockito.mockStatic(BeanGetter.class);
        PowerMockito.when(BeanGetter.getDaoOfDatabaseSet()).thenReturn(daoOfDatabaseSet);

        DatabaseSetEntry databaseSetEntry = new DatabaseSetEntry();
        databaseSetEntry.setConnectionString("connection");
        Mockito.when(daoOfDatabaseSet.getMasterDatabaseSetEntryByDatabaseSetName(dbname)).thenReturn(databaseSetEntry);
        assertEquals("connection", AllInOneNameUtils.getAllInOneName(dbname, "titankey"));
    }

    @Test
    public void getAllInOneNameByNameOnlyTest() throws SQLException {
        String dbname = "abtest_dalcluster";
        assertEquals(dbname, AllInOneNameUtils.getAllInOneNameByNameOnly(dbname));

        PowerMockito.mockStatic(BeanGetter.class);
        PowerMockito.when(BeanGetter.getDaoOfDatabaseSet()).thenReturn(daoOfDatabaseSet);

        DatabaseSetEntry databaseSetEntry = new DatabaseSetEntry();
        databaseSetEntry.setConnectionString("connection");
        Mockito.when(daoOfDatabaseSet.getMasterDatabaseSetEntryByDatabaseSetName("titankey")).thenReturn(databaseSetEntry);
        assertEquals("connection", AllInOneNameUtils.getAllInOneNameByNameOnly("titankey"));
    }
}