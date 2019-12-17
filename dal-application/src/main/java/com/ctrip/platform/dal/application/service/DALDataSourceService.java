package com.ctrip.platform.dal.application.service;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author c7ch23en
 */
@Service
public class DALDataSourceService {

    private static final String COUNT_SQL = "select count(*) from dalservicetable";
    private static final String INSERT_SQL = "insert into dalservicetable (name) values (?)";

    private DalDataSourceFactory factory = new DalDataSourceFactory();

    public String count(String dsKey) throws Exception {
        try (Connection conn = getDataSource(dsKey).getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(COUNT_SQL)) {
                statement.execute();
                try (ResultSet rs = statement.getResultSet()) {
                    rs.next();
                    return String.format("DS: %s; Result: %d", getDataSource(dsKey), rs.getInt(1));
                }
            }
        }
    }

    public String insert(String dsKey, String nameValue) throws Exception {
        try (Connection conn = getDataSource(dsKey).getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(INSERT_SQL)) {
                statement.setString(1, nameValue);
                statement.execute();
                return String.format("DS: %s; Result: %d", getDataSource(dsKey), statement.getUpdateCount());
            }
        }
    }

    private DataSource getDataSource(String dsKey) throws Exception {
        return factory.createDataSource(dsKey);
    }

}
