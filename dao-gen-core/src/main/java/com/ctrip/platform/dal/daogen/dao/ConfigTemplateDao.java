package com.ctrip.platform.dal.daogen.dao;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;
import com.ctrip.platform.dal.dao.sqlbuilder.FreeSelectSqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.SelectSqlBuilder;
import com.ctrip.platform.dal.daogen.entity.ConfigTemplate;

import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class ConfigTemplateDao {
    private static final String DATA_BASE = "dao";
    private static final DatabaseCategory dbCategory = DatabaseCategory.MySql;

    private DalTableDao<ConfigTemplate> client;
    private DalQueryDao queryDao = null;
    private DalRowMapper<ConfigTemplate> configTemplateRowMapper = null;

    public ConfigTemplateDao() throws SQLException {
        client = new DalTableDao<>(new DalDefaultJpaParser<>(ConfigTemplate.class));
        queryDao = new DalQueryDao(DATA_BASE);
        configTemplateRowMapper = new DalDefaultJpaMapper<>(ConfigTemplate.class);
    }

    public List<ConfigTemplate> getAllConfigTemplates() throws SQLException {
        SelectSqlBuilder builder = new SelectSqlBuilder().selectAll();
        DalHints hints = DalHints.createIfAbsent(null);
        return client.query(builder, hints);
    }

    public ConfigTemplate getConfigTemplateById(int templateId) throws SQLException {
        DalHints hints = DalHints.createIfAbsent(null);
        return client.queryByPk(templateId, hints);
    }

    public ConfigTemplate getConfigTemplateByConditions(ConfigTemplate configTemplate) throws SQLException {
        if (configTemplate == null)
            return null;

        FreeSelectSqlBuilder<ConfigTemplate> builder = new FreeSelectSqlBuilder<>(dbCategory);
        builder.setTemplate(
                "SELECT ID, CONFIG_TYPE, LANG_TYPE, TEMPLATE FROM config_template WHERE CONFIG_TYPE=? AND LANG_TYPE=?");
        StatementParameters parameters = new StatementParameters();
        int i = 1;
        parameters.set(i++, "CONFIG_TYPE", Types.INTEGER, configTemplate.getConfigType());
        parameters.set(i++, "LANG_TYPE", Types.INTEGER, configTemplate.getLangType());
        builder.mapWith(configTemplateRowMapper).requireFirst().nullable();
        DalHints hints = DalHints.createIfAbsent(null);
        return queryDao.query(builder, parameters, hints);
    }

    public int insertConfigTemplate(ConfigTemplate configTemplate) throws SQLException {
        if (null == configTemplate)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.insert(hints, configTemplate);
    }

    public int updateConfigTemplate(ConfigTemplate configTemplate) throws SQLException {
        if (null == configTemplate)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.update(hints, configTemplate);
    }

    public int deleteConfigTemplate(ConfigTemplate configTemplate) throws SQLException {
        if (null == configTemplate)
            return 0;
        DalHints hints = DalHints.createIfAbsent(null);
        return client.delete(hints, configTemplate);
    }

}
