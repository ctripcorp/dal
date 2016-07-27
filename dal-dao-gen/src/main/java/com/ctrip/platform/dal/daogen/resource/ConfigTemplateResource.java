package com.ctrip.platform.dal.daogen.resource;

import com.ctrip.platform.dal.daogen.utils.Configuration;
import com.ctrip.platform.dal.daogen.domain.Status;
import com.ctrip.platform.dal.daogen.entity.Config;
import com.ctrip.platform.dal.daogen.entity.ConfigTemplate;
import com.ctrip.platform.dal.daogen.entity.Language;
import com.ctrip.platform.dal.daogen.enums.ConfigType;
import com.ctrip.platform.dal.daogen.enums.LanguageType;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Resource
@Singleton
@Path("configTemplate")
public class ConfigTemplateResource {
    private static ClassLoader classLoader = null;
    private static List<Config> configList = null;
    private static List<Language> languageList = null;

    static {
        classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = Configuration.class.getClassLoader();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getAllConfigTemplates")
    public List<ConfigTemplate> getAllConfigTemplates() throws Exception {
        List<ConfigTemplate> configTemplates = SpringBeanGetter.getConfigTemplateDao().getAllConfigTemplates();
        return configTemplates;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getConfigTemplateById")
    public ConfigTemplate getConfigTemplateById(@QueryParam("id") String id) {
        int templateId = -1;
        try {
            templateId = Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            return null;
        }

        ConfigTemplate configTemplate = SpringBeanGetter.getConfigTemplateDao().getConfigTemplateById(templateId);
        return configTemplate;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getConfigTemplateByConditions")
    public ConfigTemplate getConfigTemplateByConditions(@QueryParam("configType") String configType, @QueryParam("langType") String langType) {
        ConfigTemplate configTemplate = null;
        if (configType == null || langType == null) {
            return configTemplate;
        }

        int config_type = -1;
        try {
            config_type = Integer.parseInt(configType);
        } catch (NumberFormatException e) {
            return configTemplate;
        }

        int lang_type = -1;
        try {
            lang_type = Integer.parseInt(langType);
        } catch (NumberFormatException e) {
            return configTemplate;
        }

        ConfigTemplate temp = new ConfigTemplate();
        temp.setConfig_type(config_type);
        temp.setLang_type(lang_type);
        configTemplate = SpringBeanGetter.getConfigTemplateDao().getConfigTemplateByConditions(temp);
        return configTemplate;
    }

    @POST
    @Path("addConfigTemplate")
    public Status addConfigTemplate(@FormParam("configType") String configType, @FormParam("langType") String langType, @FormParam("template") String template) {
        Status status = Status.OK;
        if (configType == null || langType == null || template == null) {
            status = Status.ERROR;
            status.setInfo("Null parameters.");
            return status;
        }

        int config_type = -1;
        try {
            config_type = Integer.parseInt(configType);
        } catch (NumberFormatException e) {
            status = Status.ERROR;
            status.setInfo("Invalid config type.");
            return status;
        }

        int lang_type = -1;
        try {
            lang_type = Integer.parseInt(langType);
        } catch (NumberFormatException e) {
            status = Status.ERROR;
            status.setInfo("Invalid lang type.");
            return status;
        }

        ConfigTemplate configTemplate = new ConfigTemplate();
        configTemplate.setConfig_type(config_type);
        configTemplate.setLang_type(lang_type);
        configTemplate.setTemplate(template);
        SpringBeanGetter.getConfigTemplateDao().insertConfigTemplate(configTemplate);
        return status;
    }

    @POST
    @Path("updateConfigTemplate")
    public Status updateConfigTemplate(@FormParam("id") String id, @FormParam("configType") String configType, @FormParam("langType") String langType, @FormParam("template") String template) {
        Status status = Status.OK;
        if (id == null || configType == null || langType == null || template == null) {
            status = Status.ERROR;
            status.setInfo("Null parameters");
            return status;
        }

        int templateId = -1;
        try {
            templateId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            status = Status.ERROR;
            status.setInfo("Invalid id.");
            return status;
        }

        int config_type = -1;
        try {
            config_type = Integer.parseInt(configType);
        } catch (NumberFormatException e) {
            status = Status.ERROR;
            status.setInfo("Invalid config type.");
            return status;
        }

        int lang_type = -1;
        try {
            lang_type = Integer.parseInt(langType);
        } catch (NumberFormatException e) {
            status = Status.ERROR;
            status.setInfo("Invalid lang type.");
            return status;
        }

        ConfigTemplate configTemplate = new ConfigTemplate();
        configTemplate.setId(templateId);
        configTemplate.setConfig_type(config_type);
        configTemplate.setLang_type(lang_type);
        configTemplate.setTemplate(template);
        SpringBeanGetter.getConfigTemplateDao().updateConfigTemplate(configTemplate);
        return status;
    }

    @POST
    @Path("deleteConfigTemplate")
    public Status deleteConfigTemplate(@FormParam("id") String id) {
        Status status = Status.OK;
        if (id == null) {
            status = Status.ERROR;
            status.setInfo("Null parameters");
            return status;
        }

        int templateId = -1;
        try {
            templateId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            status = Status.ERROR;
            status.setInfo("Invalid id.");
            return status;
        }

        ConfigTemplate configTemplate = new ConfigTemplate();
        configTemplate.setId(templateId);
        SpringBeanGetter.getConfigTemplateDao().deleteConfigTemplate(configTemplate);
        return status;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getConfigList")
    public List<Config> getConfigList() {
        if (configList == null) {
            configList = getConfigs();
        }
        return configList;
    }

    private List<Config> getConfigs() {
        List<Config> list = new ArrayList<>();
        for (ConfigType configType : ConfigType.values()) {
            Config config = new Config();
            config.setId(configType.getValue());
            config.setName(configType.getDescription());
            list.add(config);
        }

        Collections.sort(list, new Comparator<Config>() {
            @Override
            public int compare(Config c1, Config c2) {
                return c1.getId().compareTo(c2.getId());
            }
        });
        return list;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("getLanguageList")
    public List<Language> getLanguageList() {
        if (languageList == null) {
            languageList = getLanguages();
        }
        return languageList;
    }

    private List<Language> getLanguages() {
        List<Language> list = new ArrayList<>();
        for (LanguageType languageType : LanguageType.values()) {
            Language language = new Language();
            language.setId(languageType.getValue());
            language.setName(languageType.toString());
            list.add(language);
        }

        Collections.sort(list, new Comparator<Language>() {
            @Override
            public int compare(Language l1, Language l2) {
                return l1.getId().compareTo(l2.getId());
            }
        });
        return list;
    }

}
