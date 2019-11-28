package com.ctrip.framework.db.cluster.service.repository;

import com.ctrip.framework.db.cluster.dao.TableConfigDao;
import com.ctrip.framework.db.cluster.entity.TableConfig;
import com.ctrip.framework.db.cluster.entity.enums.Deleted;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by @author zhuYongMing on 2019/11/26.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TableConfigService {

    private final TableConfigDao tableConfigDao;


    public void createTableConfigs(final List<TableConfig> addedTableConfigs) throws SQLException {
        tableConfigDao.insert(addedTableConfigs);
    }

    public List<TableConfig> findTableConfigs(final Integer clusterId, final Deleted deleted,
                                              final List<String> tableNames) throws SQLException {
        return tableConfigDao.find(clusterId, deleted, tableNames);
    }
}
