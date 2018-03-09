package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.enums.ConditionType;
import com.ctrip.platform.dal.daogen.enums.CurrentLanguage;
import com.ctrip.platform.dal.daogen.enums.DatabaseCategory;
import com.ctrip.platform.dal.daogen.host.java.JavaParameterHost;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang.StringUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The SQL Re-build Utils
 *
 * @author wcyuan
 */
public class SqlBuilder {
    private static final String regInEx = "(?i)In *\\(?\\?\\)?";
    private static final String regEx = "\\?";
    private static Pattern inRegxPattern = null;
    private static Pattern regxPattern = null;

    static {
        inRegxPattern = Pattern.compile(regInEx);
        regxPattern = Pattern.compile(regEx);
    }

    private static final String mysqlPageClausePattern = " limit %s, %s";
    private static final String mysqlCSPageClausePattern = " limit {0}, {1}";

    private static final String sqlserverPagingClausePattern = " OFFSET %s ROWS FETCH NEXT %s ROWS ONLY";
    private static final String sqlseverCSPagingClausePattern = " OFFSET {0} ROWS FETCH NEXT {1} ROWS ONLY";

    private static CCJSqlParserManager parserManager = new CCJSqlParserManager();

    public static String net2Java(String sql) {
        return sql.replaceAll("@\\w+", "?");
    }

    /**
     * Re-build the query SQL to implement paging function. The new SQL Statement will contains limit if the database
     * type is MYSQL, CET wrapped if database type is SQL Server. Note: the final SQL will contain two %s, which should
     * be replaced in run time.
     *
     * @param sql The original SQL Statement
     * @param dbType The database type
     * @return Re-build SQL which contains limit if the database type is MYSQL, CET wrapped if database type is SQL
     *         Server.
     * @throws Exception
     */
    public static String pagingQuerySql(String sql, DatabaseCategory dbType, CurrentLanguage lang) throws Exception {
        String sql_content = sql.replace("@", ":");
        boolean withNolock = StringUtils.containsIgnoreCase(sql_content, "WITH (NOLOCK)");
        if (withNolock)
            sql_content = sql_content.replaceAll("(?i)WITH \\(NOLOCK\\)", "");
        StringBuilder sb = new StringBuilder();
        try {
            Select select = (Select) parserManager.parse(new StringReader(sql_content));
            PlainSelect plain = (PlainSelect) select.getSelectBody();
            if (dbType == DatabaseCategory.MySql) {
                sb.append(plain.toString());
                sb.append(lang == CurrentLanguage.Java ? mysqlPageClausePattern : mysqlCSPageClausePattern);
            } else if (dbType == DatabaseCategory.SqlServer) {
                sb.append(plain.toString());
                sb.append(lang == CurrentLanguage.Java ? sqlserverPagingClausePattern : sqlseverCSPagingClausePattern);
            } else {
                throw new Exception("Unknow database category.");
            }
        } catch (Throwable e) {
            throw e;
        }
        return sb.toString().replace(":", "@");
    }

    private static String plainSelectToStringAppendWithNoLock(PlainSelect plain) {
        StringBuilder sql = new StringBuilder("SELECT ");
        if (plain.getDistinct() != null)
            sql.append(plain.getDistinct()).append(" ");

        if (plain.getTop() != null)
            sql.append(plain.getTop()).append(" ");

        sql.append(PlainSelect.getStringList(plain.getSelectItems()));
        if (plain.getFromItem() != null) {
            sql.append(" FROM ").append(plain.getFromItem()).append(" WITH (NOLOCK) ");
            if (plain.getJoins() != null) {
                Iterator<Join> it = plain.getJoins().iterator();
                while (it.hasNext()) {
                    Join join = it.next();
                    if (join.isSimple()) {
                        sql.append(", ").append(join).append(" WITH (NOLOCK) ");
                    } else {
                        String temp = join.toString().replace(join.getRightItem().toString(),
                                join.getRightItem().toString() + " WITH (NOLOCK) ");
                        sql.append(" ").append(temp);
                    }
                }
            }

            if (plain.getWhere() != null)
                sql.append(" WHERE ").append(plain.getWhere());

            if (plain.getOracleHierarchical() != null)
                sql.append(plain.getOracleHierarchical().toString());

            sql.append(PlainSelect.getFormatedList(plain.getGroupByColumnReferences(), "GROUP BY"));
            if (plain.getHaving() != null)
                sql.append(" HAVING ").append(plain.getHaving());

            sql.append(PlainSelect.orderByToString(plain.isOracleSiblings(), plain.getOrderByElements()));
            if (plain.getLimit() != null)
                sql.append(plain.getLimit());

        }
        return sql.toString();
    }

    public static void rebuildJavaInClauseSQL(String sql, List<JavaParameterHost> parameters) throws Exception {
        List<Integer> inStartIndex = new ArrayList<>();
        List<Integer> inEndIndex = new ArrayList<>();
        Matcher m = inRegxPattern.matcher(sql);
        while (m.find()) {
            inStartIndex.add(m.start());
            inEndIndex.add(m.end());
        }
        if (inStartIndex.size() == 0)
            return;

        List<Integer> paramStartIndex = new ArrayList<>();
        List<Integer> paramEndIndex = new ArrayList<>();
        m = regxPattern.matcher(sql);
        while (m.find()) {
            paramStartIndex.add(m.start());
            paramEndIndex.add(m.end());
        }

        if (paramStartIndex.size() != parameters.size())
            throw new Exception("The count of parameters is not correct");

        for (int i = 0; i < parameters.size(); i++) {
            for (int j = 0; j < inStartIndex.size(); j++) {
                if (paramStartIndex.get(i) >= inStartIndex.get(j) && paramEndIndex.get(i) <= inEndIndex.get(j)) {
                    parameters.get(i).setConditionType(ConditionType.In);
                    break;
                }
            }
        }
    }

}
