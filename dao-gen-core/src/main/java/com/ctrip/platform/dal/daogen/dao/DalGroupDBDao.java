package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.daogen.entity.DalGroupDB;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class DalGroupDBDao {
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<String> getAllDbAllinOneNames() {
        return this.jdbcTemplate.query("SELECT dbname FROM alldbs ",
                new RowMapper<String>() {
                    public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getString("dbname");
                    }
                });
    }

    public List<DalGroupDB> getAllGroupDbs() {
        return this.jdbcTemplate.query("SELECT t1.id AS id, t1.dbname AS dbname,t2.group_name AS comment,t1.dal_group_id AS dal_group_id,"
                        + "t1.db_address AS db_address,t1.db_port AS db_port,t1.db_user AS db_user,t1.db_password AS db_password,t1.db_catalog AS db_catalog,"
                        + "t1.db_providerName AS db_providerName FROM alldbs t1 LEFT JOIN dal_group t2 ON t1.dal_group_id=t2.id ",
                new RowMapper<DalGroupDB>() {
                    public DalGroupDB mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalGroupDB.visitRow(rs);
                    }
                });
    }

    public List<DalGroupDB> getGroupDBsByGroup(int groupId) {
        return this.jdbcTemplate.query("SELECT id, dbname, comment,dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_providerName FROM alldbs"
                        + " WHERE dal_group_id=?", new Object[]{groupId},
                new RowMapper<DalGroupDB>() {
                    public DalGroupDB mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalGroupDB.visitRow(rs);
                    }
                });
    }

    public DalGroupDB getGroupDBByDbName(String dbname) {
        List<DalGroupDB> dbs = this.jdbcTemplate.query("SELECT id, dbname, comment,dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_providerName FROM alldbs"
                + " WHERE dbname=?", new Object[]{dbname}, new RowMapper<DalGroupDB>() {
            public DalGroupDB mapRow(ResultSet rs, int rowNum) throws SQLException {
                return DalGroupDB.visitRow(rs);
            }
        });
        return dbs != null && dbs.size() > 0 ? dbs.get(0) : null;
    }

    public List<DalGroupDB> getGroupDbsByDbNames(Set<String> dbNames) {
        try {
            return this.jdbcTemplate.query(
                    String.format("select id, dbname, comment,dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_providerName from alldbs where dbname in (%s) ",
                            StringUtils.join(dbNames, ",")),
                    new RowMapper<DalGroupDB>() {
                        public DalGroupDB mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return DalGroupDB.visitRow(rs);
                        }
                    });
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public DalGroupDB getGroupDBByDbId(int id) {
        List<DalGroupDB> dbs = this.jdbcTemplate.query("SELECT id, dbname, comment,dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_providerName FROM alldbs WHERE id=?",
                new Object[]{id}, new RowMapper<DalGroupDB>() {
                    public DalGroupDB mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return DalGroupDB.visitRow(rs);
                    }
                });
        return dbs != null && dbs.size() > 0 ? dbs.get(0) : null;
    }

    public int insertDalGroupDB(DalGroupDB groupDb) {
        return this.jdbcTemplate.update("INSERT INTO alldbs(dbname, comment, dal_group_id, db_address, db_port, db_user, db_password, db_catalog, db_providerName)"
                        + " VALUE(?,?,?,?,?,?,?,?,?)", groupDb.getDbname(),
                groupDb.getComment(), groupDb.getDal_group_id(),
                groupDb.getDb_address(), groupDb.getDb_port(),
                groupDb.getDb_user(), groupDb.getDb_password(),
                groupDb.getDb_catalog(), groupDb.getDb_providerName());
    }

    public int updateGroupDB(int id, String dbname, String db_address, String db_port, String db_user, String db_password, String db_catalog, String db_providerName) {
        return this.jdbcTemplate.update("UPDATE alldbs SET dbname=?, db_address=?, db_port=?, db_user=?, db_password=?, db_catalog=?, db_providerName=? WHERE id=?",
                dbname, db_address, db_port, db_user, db_password, db_catalog, db_providerName, id);
    }

    public int updateGroupDB(int id, String comment) {
        return this.jdbcTemplate.update("UPDATE alldbs SET comment=? WHERE id=?", comment, id);
    }

    public int updateGroupDB(int id, Integer groupId) {
        return this.jdbcTemplate.update("UPDATE alldbs SET dal_group_id=? WHERE id=?", groupId, id);
    }

    public int deleteDalGroupDB(int dbId) {
        return this.jdbcTemplate.update("DELETE FROM alldbs WHERE id=?", dbId);
    }

}
