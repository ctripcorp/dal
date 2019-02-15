package com.ctrip.platform.dal.dao.helper.EntityManagerTest;

import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.helper.EntityManager;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Dao.ChildwithallfieldsDao;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.Inheritance.Child;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.Inheritance.ChildWithAllFields;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiAutoIncrementInDifferentClass.ChildWithAutoIncrement;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiAutoIncrementOnlyInGrandParentClass.ChildWithoutAutoIncrement;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiVersionInDifferentClass.ChildWithVersion;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiVersionOnlyInGrandParentClass.ChildWithOutVersion;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.SameColumnNameInDifferentClass.ChildWithNameValueForColumn;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.SameColumnNameInSameClass.ChildWithSameColumnName;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.Inheritance.GrandParent;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.Inheritance.Parent;
import com.ctrip.platform.dal.dao.helper.EntityManagerTest.Entity.MultiVersionInSameClass.ChildWithMultiVersion;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EntityManagerTest {
    private static EntityManagerInitializer initializer = new EntityManagerInitializer();

    private Class grandParentClass = GrandParent.class;

    private Class parentClass = Parent.class;

    private Class childClass = Child.class;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        initializer.setUpBeforeClass();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        initializer.tearDownAfterClass();
    }

    @Test
    public void testGrandParentDatabaseName() {
        try {
            String databaseName = getDatabaseName(grandParentClass);
            Assert.assertEquals(databaseName, "GrandParentDatabase");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGrandParentTableName() {
        try {
            String tableName = getTableName(grandParentClass);
            Assert.assertEquals(tableName, "GrandParentTable");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGrandParentColumnNames() {
        try {
            String[] columnNames = getAllColumnNames(grandParentClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfGrandParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGrandParentPrimaryKeyNames() {
        try {
            String[] columnNames = getPrimaryKeyNames(grandParentClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedPrimaryKeyNamesOfGrandParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGrandParentSensitiveColumnNames() {
        try {
            String[] columnNames = getSensitiveColumnNames(grandParentClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedSensitiveColumnNamesOfGrandParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGrandParentInsertableColumnNames() {
        try {
            String[] columnNames = getInsertableColumnNames(grandParentClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfGrandParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testGrandParentUpdatableColumnNames() {
        try {
            String[] columnNames = getUpdatableColumnNames(grandParentClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfGrandParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private List<String> getExpectedColumnNamesOfGrandParent() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.add("grandParentId");
        expectedNames.add("grandParentName");
        return expectedNames;
    }

    private List<String> getExpectedPrimaryKeyNamesOfGrandParent() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.add("grandParentId");
        return expectedNames;
    }

    private List<String> getExpectedSensitiveColumnNamesOfGrandParent() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.add("grandParentName");
        return expectedNames;
    }

    @Test
    public void testParentDatabaseName() {
        try {
            String databaseName = getDatabaseName(Parent.class);
            Assert.assertEquals(databaseName, "ParentDatabase");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testParentTableName() {
        try {
            String tableName = getTableName(parentClass);
            Assert.assertEquals(tableName, "ParentTable");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testParentColumnNames() {
        try {
            String[] columnNames = getAllColumnNames(Parent.class);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testParentPrimaryKeyNames() {
        try {
            String[] columnNames = getPrimaryKeyNames(parentClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedPrimaryKeyNamesOfParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testParentSensitiveColumnNames() {
        try {
            String[] columnNames = getSensitiveColumnNames(parentClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedSensitiveColumnNamesOfParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testParentInsertableColumnNames() {
        try {
            String[] columnNames = getInsertableColumnNames(parentClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testParentUpdatableColumnNames() {
        try {
            String[] columnNames = getUpdatableColumnNames(parentClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfParent();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private List<String> getExpectedPrimaryKeyNamesOfParent() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.addAll(getExpectedPrimaryKeyNamesOfGrandParent());

        expectedNames.add("parentId");
        return expectedNames;
    }

    private List<String> getExpectedColumnNamesOfParent() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.addAll(getExpectedColumnNamesOfGrandParent());

        expectedNames.add("parentId");
        expectedNames.add("parentName");
        return expectedNames;
    }

    private List<String> getExpectedSensitiveColumnNamesOfParent() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.addAll(getExpectedSensitiveColumnNamesOfGrandParent());
        expectedNames.add("parentName");
        return expectedNames;
    }

    @Test
    public void testChildDatabaseName() {
        try {
            String databaseName = getDatabaseName(Child.class);
            Assert.assertEquals(databaseName, "ChildDatabase");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testChildTableName() {
        try {
            String tableName = getTableName(childClass);
            Assert.assertEquals(tableName, "ChildTable");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testChildColumnNames() {
        try {
            String[] columnNames = getAllColumnNames(Child.class);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfChild();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testChildPrimaryKeyNames() {
        try {
            String[] columnNames = getPrimaryKeyNames(childClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedPrimaryKeyNamesOfChild();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testChildSensitiveColumnNames() {
        try {
            String[] columnNames = getSensitiveColumnNames(childClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedSensitiveColumnNamesOfChild();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testChildInsetableColumnNames() {
        try {
            String[] columnNames = getInsertableColumnNames(childClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfChild();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testChildUpdatableColumnNames() {
        try {
            String[] columnNames = getUpdatableColumnNames(childClass);
            List<String> actualColumnNames = Arrays.asList(columnNames);
            Collections.sort(actualColumnNames);

            List<String> expectedColumnNames = getExpectedColumnNamesOfChild();
            Collections.sort(expectedColumnNames);

            Assert.assertEquals(actualColumnNames, expectedColumnNames);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    private List<String> getExpectedPrimaryKeyNamesOfChild() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.addAll(getExpectedPrimaryKeyNamesOfParent());

        expectedNames.add("childId");
        return expectedNames;
    }

    private List<String> getExpectedColumnNamesOfChild() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.addAll(getExpectedColumnNamesOfParent());

        expectedNames.add("childId");
        expectedNames.add("childName");
        return expectedNames;
    }

    private List<String> getExpectedSensitiveColumnNamesOfChild() {
        List<String> expectedNames = new ArrayList<>();
        expectedNames.addAll(getExpectedSensitiveColumnNamesOfParent());

        expectedNames.add("childName");
        return expectedNames;
    }

    private String getDatabaseName(Class clazz) throws SQLException {
        EntityManager entity = EntityManager.getEntityManager(clazz);
        return entity.getDatabaseName();
    }

    private String getTableName(Class clazz) throws Exception {
        EntityManager entity = EntityManager.getEntityManager(clazz);
        return entity.getTableName();
    }

    private String[] getAllColumnNames(Class clazz) throws SQLException {
        EntityManager entity = EntityManager.getEntityManager(clazz);
        return entity.getColumnNames();
    }

    private String[] getPrimaryKeyNames(Class clazz) throws SQLException {
        EntityManager entity = EntityManager.getEntityManager(clazz);
        return entity.getPrimaryKeyNames();
    }

    private String[] getSensitiveColumnNames(Class clazz) throws SQLException {
        EntityManager entity = EntityManager.getEntityManager(clazz);
        return entity.getSensitiveColumnNames();
    }

    private String[] getInsertableColumnNames(Class clazz) throws SQLException {
        EntityManager entity = EntityManager.getEntityManager(clazz);
        return entity.getInsertableColumnNames();
    }

    private String[] getUpdatableColumnNames(Class clazz) throws SQLException {
        EntityManager entity = EntityManager.getEntityManager(clazz);
        return entity.getUpdatableColumnNames();
    }

    @Test
    public void testVersionColumnName() throws SQLException {
        try {
            EntityManager entity = EntityManager.getEntityManager(childClass);
            String version = entity.getVersionColumn();

            Assert.assertEquals(version, "grandParentName");
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testCRUDOnInheritTable() throws SQLException {
        try {
            ChildwithallfieldsDao dao = getDao();
            ChildWithAllFields pojo = getPojo();

            // insert
            KeyHolder holder = new KeyHolder();
            dao.insertWithKeyHolder(holder, pojo);
            Number key = holder.getKey();

            Assert.assertTrue(key != null);

            // query by pk
            ChildWithAllFields result = dao.queryByPk(key);

            Assert.assertTrue(result != null);
            Assert.assertTrue(result.getGrandParentName().equals("GrandParentName"));
            Assert.assertTrue(result.getParentId() == 666);
            Assert.assertTrue(result.getParentName().equals("ParentName"));
            Assert.assertTrue(result.getChildId() == 888);
            Assert.assertTrue(result.getChildName().equals("ChildName"));

            // update then query again
            result.setGrandParentName("GrandParentNameUpdate");
            result.setParentId(333);
            result.setParentName("ParentNameUpdate");
            result.setChildId(444);
            result.setChildName("ChildNameUpdate");

            Assert.assertTrue(dao.update(result) == 1);

            ChildWithAllFields result1 = dao.queryByPk(key);

            Assert.assertTrue(result1 != null);
            Assert.assertTrue(result1.getGrandParentName().equals("GrandParentNameUpdate"));
            Assert.assertTrue(result1.getParentId() == 333);
            Assert.assertTrue(result1.getParentName().equals("ParentNameUpdate"));
            Assert.assertTrue(result1.getChildId() == 444);
            Assert.assertTrue(result1.getChildName().equals("ChildNameUpdate"));

            // delete
            Assert.assertTrue(dao.delete(result1) == 1);

            // check record
            ChildWithAllFields result2 = dao.queryByPk(key);
            Assert.assertTrue(result2 == null);
        } catch (Throwable e) {
            Assert.fail();
        }
    }

    private ChildwithallfieldsDao getDao() throws SQLException {
        return new ChildwithallfieldsDao();
    }

    private ChildWithAllFields getPojo() {
        ChildWithAllFields pojo = new ChildWithAllFields();
        pojo.setGrandParentName("GrandParentName");

        pojo.setParentId(666);
        pojo.setParentName("ParentName");

        pojo.setChildId(888);
        pojo.setChildName("ChildName");

        return pojo;
    }


    // 同一个类中的字段有相同的Column name，抛出异常
    @Test
    public void testSameColumnNameInSameClass() throws SQLException {
        try {
            EntityManager entity = EntityManager.getEntityManager(ChildWithSameColumnName.class);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().equals("Column name is already used by other field"));
        }
    }

    // 不同类中的字段有相同的Column name，取子类的字段
    @Test
    public void testSameColumnNameInDifferentClass() throws SQLException {
        try {
            EntityManager entity = EntityManager.getEntityManager(ChildWithNameValueForColumn.class);
            Map<String, Field> map = entity.getFieldMap();
            Assert.assertTrue(map.size() == 1);
            Assert.assertTrue(map.get("Name").getName().equals("childName"));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    // 同一个类中有多于一个Version 字段，抛出异常
    @Test
    public void testMultiVersionInSameClass() throws SQLException {
        try {
            EntityManager entity = EntityManager.getEntityManager(ChildWithMultiVersion.class);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().equals("The entity contains more than one version annotation"));
        }
    }

    // 子类，父类中都有Version字段，取子类的Version字段
    @Test
    public void testMultiVersionInBothClass() throws SQLException {
        try {
            EntityManager entity = EntityManager.getEntityManager(ChildWithVersion.class);
            String columnName = entity.getVersionColumn();
            Assert.assertTrue(columnName.equals("childName"));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    // 子类没有Version字段，父类有Version字段，取父类的Version字段
    @Test
    public void testVersionOnlyInParentClass() throws SQLException {
        try {
            EntityManager entity = EntityManager.getEntityManager(ChildWithOutVersion.class);
            String columnName = entity.getVersionColumn();
            Assert.assertTrue(columnName.equals("grandParentName"));
        } catch (Throwable e) {
            Assert.fail();
        }
    }

    // 子类，父类都有自增主键字段，取子类的自增主键字段
    @Test
    public void testMultiAutoIncrementInBothClass() throws SQLException {
        try {
            EntityManager entity = EntityManager.getEntityManager(ChildWithAutoIncrement.class);
            Field[] fields = entity.getIdentity();
            Assert.assertTrue(fields.length == 1);
            Assert.assertTrue(fields[0].getName().equals("childId"));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    //子类没有自增主键，父类有自增主键，去父类的自增主键
    @Test
    public void testAutoIncrementOnlyInParentClass() throws SQLException {
        try {
            EntityManager entity = EntityManager.getEntityManager(ChildWithoutAutoIncrement.class);
            Field[] fields = entity.getIdentity();
            Assert.assertTrue(fields.length == 1);
            Assert.assertTrue(fields[0].getName().equals("grandParentId"));
        } catch (Exception e) {
            Assert.fail();
        }
    }

}
