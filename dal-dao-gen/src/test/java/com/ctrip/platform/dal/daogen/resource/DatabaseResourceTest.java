package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.dao.DalGroupDBDao;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import com.ctrip.platform.dal.daogen.entity.DbInfos;
import com.ctrip.platform.dal.daogen.utils.BeanGetter;
import com.ctrip.platform.dal.daogen.utils.DBInfoApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({BeanGetter.class, CustomizedResource.class})
public class DatabaseResourceTest {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @InjectMocks
    private DatabaseResource databaseResource;

    @Mock
    private DalGroupDBDao dalGroupDBDao;

    @Mock
    private DBInfoApi dbInfoApi;

    @Mock
    private CustomizedResource customizedResource;

    private static ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getAllDB() {

    }

    private List<DalGroupDB> genDalGroupDB() {
        DalGroupDB db1 = new DalGroupDB();
        db1.setDbname("test1");
        DalGroupDB db2 = new DalGroupDB();
        db2.setDbname("test2");
        DalGroupDB db3 = new DalGroupDB();
        db3.setDbname("test3");
        return Lists.newArrayList(db1, db2, db3);
    }

    @Test
    public void getBasesetNameListTest() throws Exception {
        PowerMockito.mockStatic(BeanGetter.class);
        PowerMockito.when(BeanGetter.getDaoOfDalGroupDB()).thenReturn(dalGroupDBDao);
        Mockito.when(dalGroupDBDao.getDalClusterGroupDbsByGroupId(12, "dalcluster"))
                .thenReturn(genDalGroupDB())
                .thenThrow(new RuntimeException());
        Status status = databaseResource.getBasesetNameList(12);

        List<String> expect = Lists.newArrayList("test1", "test2", "test3");
        assertEquals(mapper.writeValueAsString(expect), status.getInfo());

        Status status2 = databaseResource.getBasesetNameList(12);
        assertEquals("Error", status2.getCode());
    }

    private List<DbInfos> genDbInfos() {
        DbInfos dbInfo1 = new DbInfos();
        dbInfo1.setDbNameBase("test1");
        DbInfos dbInfo2 = new DbInfos();
        dbInfo2.setDbNameBase("test2");
        DbInfos dbInfo3 = new DbInfos();
        dbInfo3.setDbNameBase("test3");
        return Lists.newArrayList(dbInfo1, dbInfo2, dbInfo3);
    }

    @Test
    public void getAllNameBasesTest() throws Exception {
        PowerMockito.mockStatic(CustomizedResource.class);
        PowerMockito.when(CustomizedResource.getInstance()).thenReturn(customizedResource);
        Mockito.when(customizedResource.getDBLevelInfoApiClassName()).thenReturn("com.ctrip.platform.dal.daogen.utils.DBInfoApi");
        Mockito.when(dbInfoApi.getAllDbInfos("test")).thenReturn(genDbInfos());
        Status status = databaseResource.getAllNameBases();

        List<String> expect = Lists.newArrayList("test1", "test2", "test3");

        assertEquals("Error", status.getCode());
    }

    @Test
    public void hasDalClusterTest() throws Exception {
        Status status1 = databaseResource.hasDalCluster(null);
        assertEquals("false", status1.getInfo());

        DatabaseResource resource = Mockito.spy(DatabaseResource.class);
        Mockito.doReturn(false).when(resource).checkValidDalCluster("test");
        assertEquals("false", resource.hasDalCluster("test").getInfo());

        Mockito.doThrow(new RuntimeException()).when(resource).checkValidDalCluster(Mockito.anyString());
        assertEquals("Error", resource.hasDalCluster("test").getCode());
    }

    @Test
    public void checkCLusterValidTest() throws Exception {
        DatabaseResource resource = Mockito.spy(DatabaseResource.class);
        Mockito.doReturn(false).when(resource).checkValidDalCluster(Mockito.anyString());
        assertEquals("this db hasn't applied dalcluster", resource.checkCLusterValid("test").getInfo());

        Mockito.doReturn(true).when(resource).checkValidDalCluster(Mockito.anyString());
        assertEquals("OK", resource.checkCLusterValid("test").getCode());

        Mockito.doThrow(new RuntimeException()).when(resource).checkValidDalCluster(Mockito.anyString());
        assertEquals("Error", resource.checkCLusterValid("test").getCode());
    }
}