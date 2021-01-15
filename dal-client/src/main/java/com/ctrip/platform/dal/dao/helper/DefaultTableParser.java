package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang.StringUtils;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lilj on 2018/7/26.
 */
public class DefaultTableParser implements TableParser {
    private static ILogger logger = DalElementFactory.DEFAULT.getILogger();
    private static final String TABLEPARSE_ERROR = "TABLEPARSE::ERROR";
    private static final String TABLE_PARSER_CACHE_ADD = "tableParser::CacheAdd";
    private static final String CACHE_DEFAULT_SIZE = "1000";
    private static final float LOAD_FACTOR = 0.8f;
    private static final int KEY_OF_CACHE_MAX_BYTES = 2000;
    private static final String INSERT_SYMBOL = "value";
    private static final String CONDITION_SYMBOL = "where";
    private static final Set<String> callStringElements = new HashSet<String>() {
        {
            add("{call");
            add("exec");
        }
    };
    private static final Set<String> spElements = new HashSet<String>() {
        {
            add("spa");
            add("sp3");
            add("spt");
        }
    };
    private TablesNamesFinder finder = new TablesNamesFinder();
    private Lock finderLock = new ReentrantLock();
    private static final Pattern pattern = Pattern.compile("(with)*\\s*\\(nolock\\)");
    private static int cacheInitSize = Integer.valueOf(DalPropertiesManager.getInstance().getDalPropertiesLocator().getTableParserCacheInitSize(CACHE_DEFAULT_SIZE));

    protected static LinkedHashMap<String, List<String>> sqlToTables = new LinkedHashMap(cacheInitSize, LOAD_FACTOR, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() >= cacheInitSize * LOAD_FACTOR;
        }
    };

    @Override
    public Set<String> getTablesFromSqls(String... sqls) {
        Set<String> tableSet = new HashSet<>();
        if (sqls == null || sqls.length == 0)
            return tableSet;
        // parse batch sqls
        for (String sql : sqls) {
            if (StringUtils.isBlank(sql))
                continue;
            // parse multiple sqls in one sql string
            String[] subSqls = sql.trim().toLowerCase().split(";");
            for (String sqlString : subSqls) {
                if (StringUtils.isBlank(sqlString))
                    continue;

                if (isCallString(sqlString))
                    // parse call string
                    tableSet.addAll(extractTablesFromCallString(sqlString));
                else
                    // parse sql string
                    tableSet.addAll(extractTablesFromSql(sqlString));
            }
        }
        return tableSet;
    }

    private Boolean isCallString(String sqlString) {
        for (String element : callStringElements) {
            if (sqlString.contains(element))
                return true;
        }
        return false;
    }

    private Boolean isStandardSp(String spName) {
        for (String element : spElements) {
            if (spName.contains(element))
                return true;
        }
        return false;
    }

    private List<String> extractTablesFromSql(String sql) {
        List<String> tableList = new ArrayList<>();
        long startTime=System.currentTimeMillis();
        try {
            sql = ignoreUnsupportedSyntax(sql);
            finderLock.lock();
            try {
                //get from cache first
                tableList = getTablesFromCache(sql);
            } catch (Throwable e) {
                logger.logTransaction(DalLogTypes.DAL, TABLEPARSE_ERROR, e.getMessage(), startTime);
            } finally {
                finderLock.unlock();
            }
        } catch (Throwable e) {
            logger.logTransaction(DalLogTypes.DAL, TABLEPARSE_ERROR, e.getMessage(), startTime);
            return Collections.emptyList();
        }
        // remove mysql quote "``" or sqlserver quote "[]" and db prefix like "dbname.tablename"
        return removePrefixAndQuote(tableList);
    }

    protected List<String> getTablesFromCache(String sql) throws JSQLParserException {
        List<String> tables = null;
        String keySql = ignoreWhereAndValues(sql);
        if (KEY_OF_CACHE_MAX_BYTES < keySql.getBytes().length) {
            return finder.getTableList(CCJSqlParserUtil.parse(keySql));
        }

        tables = sqlToTables.get(keySql);
        if (tables == null) {
            tables = finder.getTableList(CCJSqlParserUtil.parse(keySql));
            synchronized (sqlToTables) {
                logger.logTransaction(DalLogTypes.DAL, TABLE_PARSER_CACHE_ADD, "size: " + sqlToTables.size(), System.currentTimeMillis());
                sqlToTables.put(sql, tables);
            }
        }

        return tables == null ? new ArrayList<>() : tables;
    }

    protected String ignoreWhereAndValues(String sql) {
        try {
            int statementStartPos = SqlUtils.findStartOfStatement(sql);
            int valueIndex = sql.lastIndexOf(INSERT_SYMBOL);
            if (valueIndex > -1) {
                return sql.substring(statementStartPos , valueIndex);
            }

            int whereIndex = sql.lastIndexOf(CONDITION_SYMBOL);

            if (whereIndex > 0) {
                return sql.substring(statementStartPos , whereIndex);
            }
        } catch (Exception e) {

        }

        return sql;
    }

    private String ignoreUnsupportedSyntax(String sql) {
        // the jsqlparser can not parse "with (nolock)" or "(nolock)"
        sql = ignoreWithNolock(sql);
        // the jsqlparser can not parse "isnull"
        sql = sql.replace("isnull", " ");
        return sql;
    }

    private String ignoreWithNolock(String sql) {
        // you can not simply remove "with" because of "with as" syntax
        String proccessedSql;

        // create matcher
        Matcher m = pattern.matcher(sql.trim());

        proccessedSql = m.replaceAll(" ");

        return proccessedSql;
    }

    private List<String> extractTablesFromCallString(String callString) {
        List<String> tables = new ArrayList<>();

        String[] sqlElements = callString.trim().split("\\s+");

        if (sqlElements.length < 2)
            return tables;

        // check begin with "call" or "exec"
        String firstElement = sqlElements[0];
        if (callStringElements.contains(firstElement)) {
            String spName = sqlElements[1];
            // check begin with "sp3" or "spt" or "spa"
            if (isStandardSp(spName)) {
                int startIndex = spName.indexOf("_");
                int endIndex = spName.lastIndexOf("_");
                if (startIndex != endIndex)
                    tables.add(spName.substring(startIndex + 1, endIndex));
            }
        }

        return tables;
    }


    private List<String> removePrefixAndQuote(List<String> quoteTables) {
        List<String> processedTables = new ArrayList<>();
        try {
            for (String quoteTable : quoteTables) {
                // remove db prefix
                if (quoteTable.contains(".")) {
                    String[] tableString = quoteTable.split("[.]");
                    quoteTable = tableString[tableString.length - 1];
                }
                // remove mysql quote
                if (quoteTable.startsWith("`")) {
                    int begin = quoteTable.indexOf("`");
                    int end = quoteTable.indexOf("`", begin + 1);
                    quoteTable = quoteTable.substring(begin + 1, end);
                }
                // remove sqlserver quote
                if (quoteTable.startsWith("[")) {
                    int begin = quoteTable.indexOf("[");
                    int end = quoteTable.indexOf("]", begin + 1);
                    quoteTable = quoteTable.substring(begin + 1, end);
                }
                processedTables.add(quoteTable);
            }
        } catch (Throwable e) {

        } finally {
            return processedTables;
        }
    }
}
