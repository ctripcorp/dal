package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.dao.DalGroupDBDao;
import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.easymock.classextension.EasyMock.*;

public class DatabaseResourceTest {

    private static volatile DatabaseResource databaseResource;

    static {
        databaseResource = new DatabaseResource();
    }

    @Test
    public void getAllDB() {

    }

    private List<DalGroupDB> genDalGroupDB() {
        return null;
    }

    @Test
    public void getBasesetNameListTest() throws SQLException {
        DalGroupDBDao mockDao = createMock(DalGroupDBDao.class);
        mockDao.getDalClusterGroupDbsByGroupId(anyInt(), anyLong() + "");
    }
}