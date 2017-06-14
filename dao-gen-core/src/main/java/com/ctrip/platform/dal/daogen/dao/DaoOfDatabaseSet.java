package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.DatabaseSet;
import com.ctrip.platform.dal.daogen.entity.DatabaseSetEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DaoOfDatabaseSet {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public DatabaseSet getAllDatabaseSetById(Integer id) {
        try {
            List<DatabaseSet> dbset = jdbcTemplate.query(
                    "SELECT id, name, provider, shardingStrategy, groupId, update_user_no, update_time FROM databaseset WHERE id = ?",
                    new Object[] {id}, new RowMapper<DatabaseSet>() {
                        public DatabaseSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return DatabaseSet.visitRow(rs);
                        }
                    });
            return dbset != null && dbset.size() > 0 ? dbset.get(0) : null;
        } catch (Throwable e) {
            throw e;
        }
    }

    public List<DatabaseSet> getAllDatabaseSetByName(String name) {
        try {
            List<DatabaseSet> dbset = jdbcTemplate.query(
                    "SELECT id, name, provider, shardingStrategy, groupId, update_user_no, update_time FROM databaseset WHERE name = ?",
                    new Object[] {name}, new RowMapper<DatabaseSet>() {
                        public DatabaseSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return DatabaseSet.visitRow(rs);
                        }
                    });
            return dbset;
        } catch (Throwable e) {
            throw e;
        }
    }

    public List<DatabaseSet> getAllDatabaseSetByGroupId(Integer groupId) {
        try {
            List<DatabaseSet> dbset = jdbcTemplate.query(
                    "SELECT id, name, provider, shardingStrategy, groupId, update_user_no, update_time FROM databaseset WHERE groupId = ?",
                    new Object[] {groupId}, new RowMapper<DatabaseSet>() {
                        public DatabaseSet mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return DatabaseSet.visitRow(rs);
                        }
                    });
            return dbset;
        } catch (Throwable e) {
            throw e;
        }
    }

    public List<DatabaseSetEntry> getAllDatabaseSetEntryByDbsetid(Integer databaseSet_Id) {
        try {
            List<DatabaseSetEntry> dbset = jdbcTemplate.query(
                    "SELECT id, name, databaseType, sharding, connectionString, databaseSet_Id, update_user_no, update_time FROM databasesetentry WHERE databaseSet_Id = ?",
                    new Object[] {databaseSet_Id}, new RowMapper<DatabaseSetEntry>() {
                        public DatabaseSetEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return DatabaseSetEntry.visitRow(rs);
                        }
                    });
            return dbset;
        } catch (Throwable e) {
            throw e;
        }
    }

    public DatabaseSetEntry getMasterDatabaseSetEntryByDatabaseSetName(String dbName) {
        try {
            List<DatabaseSetEntry> list = jdbcTemplate.query(
                    "select en.id, en.name, en.databaseType, en.sharding, en.connectionString, en.databaseSet_Id, en.update_user_no, en.update_time "
                            + "from databasesetentry as en " + "join databaseset as se on en.databaseSet_Id = se.id "
                            + "where se.name = '" + dbName + "' and en.databaseType = 'Master' limit 1;",
                    new RowMapper<DatabaseSetEntry>() {
                        @Override
                        public DatabaseSetEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return DatabaseSetEntry.visitRow(rs);
                        }
                    });
            return null != list && list.size() > 0 ? list.get(0) : null;
        } catch (Throwable e) {
            throw e;
        }
    }

    public int insertDatabaseSet(DatabaseSet dbset) {
        try {
            return jdbcTemplate.update(
                    "INSERT INTO databaseset(name, provider, shardingStrategy, groupId, update_user_no, update_time) VALUE(?,?,?,?,?,?)",
                    dbset.getName(), dbset.getProvider(), dbset.getShardingStrategy(), dbset.getGroupId(),
                    dbset.getUpdate_user_no(), dbset.getUpdate_time());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int insertDatabaseSetEntry(DatabaseSetEntry dbsetEntry) {
        try {
            return jdbcTemplate.update(
                    "INSERT INTO databasesetentry(name, databaseType, sharding, connectionString, databaseSet_Id, update_user_no, update_time) VALUE(?,?,?,?,?,?,?)",
                    dbsetEntry.getName(), dbsetEntry.getDatabaseType(), dbsetEntry.getSharding(),
                    dbsetEntry.getConnectionString(), dbsetEntry.getDatabaseSet_Id(), dbsetEntry.getUpdate_user_no(),
                    dbsetEntry.getUpdate_time());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int updateDatabaseSet(DatabaseSet dbset) {
        try {
            return jdbcTemplate.update(
                    "UPDATE databaseset SET name=?, provider=?, shardingStrategy=?, groupId=?, update_user_no=?, update_time=? WHERE id=?",
                    dbset.getName(), dbset.getProvider(), dbset.getShardingStrategy(), dbset.getGroupId(),
                    dbset.getUpdate_user_no(), dbset.getUpdate_time(), dbset.getId());
        } catch (Throwable e) {
            throw e;
        }
    }

    public int updateDatabaseSetEntry(DatabaseSetEntry dbsetEntry) {
        try {
            return jdbcTemplate.update(
                    "UPDATE databasesetentry SET name=?, databaseType=?, sharding=?, connectionString=?, databaseSet_Id=?, update_user_no=?, update_time=? WHERE id=?",
                    dbsetEntry.getName(), dbsetEntry.getDatabaseType(), dbsetEntry.getSharding(),
                    dbsetEntry.getConnectionString(), dbsetEntry.getDatabaseSet_Id(), dbsetEntry.getUpdate_user_no(),
                    dbsetEntry.getUpdate_time(), dbsetEntry.getId());
        } catch (Throwable e) {
            throw e;
        }
    }

    /**
     * 依据外键databaseSet_Id删除entry
     *
     * @param dbsetId
     * @return
     */
    public int deleteDatabaseSetEntryByDbsetId(Integer dbsetId) {
        try {
            return jdbcTemplate.update("DELETE FROM databasesetentry WHERE databaseSet_Id=?", dbsetId);
        } catch (Throwable e) {
            throw e;
        }
    }

    /**
     * 根据主键id删除entry
     *
     * @param id
     * @return
     */
    public int deleteDatabaseSetEntryById(Integer id) {
        try {
            return jdbcTemplate.update("DELETE FROM databasesetentry WHERE id=?", id);
        } catch (Throwable e) {
            throw e;
        }
    }

    public int deleteDatabaseSetById(Integer dbsetId) {
        try {
            return jdbcTemplate.update("DELETE FROM databaseset WHERE id=?", dbsetId);
        } catch (Throwable e) {
            throw e;
        }
    }

}


