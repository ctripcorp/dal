package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.common.enums.SqlType;
import com.mysql.jdbc.StringUtils;

public class SqlUtils {

    public static boolean isReadOperation(String sql) {
        return isReadOperation(firstAlphaCharUc(sql));
    }

    public static boolean isReadOperation(char firstAlphaCharUc) {
        return firstAlphaCharUc == 'S';
    }

    public static boolean isInsertOperation(char firstAlphaCharUc) {
        return firstAlphaCharUc == 'I';
    }

    public static char firstAlphaCharUc(String sql) {
        return StringUtils.firstAlphaCharUc(sql, findStartOfStatement(sql));
    }

    public static int findStartOfStatement(String sql) {
        int statementStartPos = 0;

        if (StringUtils.startsWithIgnoreCaseAndWs(sql, "/*")) {
            statementStartPos = sql.indexOf("*/");

            if (statementStartPos == -1) {
                statementStartPos = 0;
            } else {
                statementStartPos += 2;
            }
        } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "--") || StringUtils.startsWithIgnoreCaseAndWs(sql, "#")) {
            statementStartPos = sql.indexOf('\n');

            if (statementStartPos == -1) {
                statementStartPos = sql.indexOf('\r');

                if (statementStartPos == -1) {
                    statementStartPos = 0;
                }
            }
        }

        return statementStartPos;
    }

    public static SqlType getSqlType(String sql) {
        // todo-lhj 需要对比两种解析sql的实现方式的效率，根据压测情况对比

        return null;
    }

}
