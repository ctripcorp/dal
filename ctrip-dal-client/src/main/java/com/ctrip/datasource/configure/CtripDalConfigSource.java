package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DalConfigConstants;
import com.ctrip.platform.dal.dao.configure.DalConfigSource;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.configure.DefaultDalConfigSourceParser;
import com.ctrip.platform.dal.exceptions.DalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import qunar.tc.qconfig.client.TypedConfig;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CtripDalConfigSource extends DalConfigConstants implements DalConfigSource {
  private static final Logger logger = LoggerFactory.getLogger(CtripDalConfigSource.class);
  private static final String DAL_CONFIG_PROPERTIES = "dal.config.properties";
  private static final Charset charset = StandardCharsets.UTF_8;

  @Override
  public void initialize(Map<String, String> settings) throws Exception {}

  @Override
  public Map<String, DatabaseSet> getDatabaseSets(Node databaseSetsNode) throws Exception {
    Map<String, DatabaseSet> map = new HashMap<>();
    try {
      TypedConfig<String> config = TypedConfig.get(DAL_CONFIG_PROPERTIES, new TypedConfig.Parser<String>() {
        public String parse(String s) {
          return s;
        }
      });

      String content = config.current();
      if (content != null && content.length() > 0) {
        InputStream stream = new ByteArrayInputStream(content.getBytes(charset));
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
        Element root = doc.getDocumentElement();
        Node node = getChildNode(root, DATABASE_SETS);
        map = DefaultDalConfigSourceParser.readDatabaseSets(node);
      }
    } catch (Throwable e) {
      String msg = "从QConfig读取dal.config配置时发生异常:";
      logger.error(msg + e.getMessage(), e);
      throw new DalException(msg + e.getMessage());
      // Fallback to local dal.config?
      // return DefaultDalConfigSourceParser.readDatabaseSets(databaseSetsNode);
    }

    return map;
  }

}
