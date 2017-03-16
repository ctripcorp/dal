package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ActivityCorrelator;
import com.microsoft.sqlserver.jdbc.DataTypes;
import com.microsoft.sqlserver.jdbc.DriverJDBCVersion;
import com.microsoft.sqlserver.jdbc.ISQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.JDBCCallSyntaxTranslator;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.JavaType;
import com.microsoft.sqlserver.jdbc.Parameter;
import com.microsoft.sqlserver.jdbc.ParameterUtils;
import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerParameterMetaData;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import com.microsoft.sqlserver.jdbc.SQLServerStatement;
import com.microsoft.sqlserver.jdbc.SSType;
import com.microsoft.sqlserver.jdbc.StreamSetterArgs;
import com.microsoft.sqlserver.jdbc.StreamType;
import com.microsoft.sqlserver.jdbc.TDSCommand;
import com.microsoft.sqlserver.jdbc.TDSParser;
import com.microsoft.sqlserver.jdbc.TDSReader;
import com.microsoft.sqlserver.jdbc.TDSWriter;
import com.microsoft.sqlserver.jdbc.UninterruptableTDSCommand;
import com.microsoft.sqlserver.jdbc.Util;
import com.microsoft.sqlserver.jdbc.SQLServerStatement.StmtExecOutParamHandler;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.BatchUpdateException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

import microsoft.sql.DateTimeOffset;

import java.lang.reflect.Field;

public class SQLServerPreparedStatement extends SQLServerStatement implements ISQLServerPreparedStatement {
    private static final int BATCH_STATEMENT_DELIMITER_TDS_71 = 128;
    private static final int BATCH_STATEMENT_DELIMITER_TDS_72 = 255;
    final int nBatchStatementDelimiter = 255;
    private String sqlCommand;
    private String preparedTypeDefinitions;
    private final String userSQL;
    private String preparedSQL;
    final boolean bReturnValueSyntax;
    int outParamIndexAdjustment;
    ArrayList<Parameter[]> batchParamValues;
    private int prepStmtHandle = 0;
    private boolean expectPrepStmtHandle = false;

    //hacked
    private static final String VARCHAR_8K = "varchar(8000)";
    private static final String NVARCHAR_4K = "nvarchar(4000)";
    private static Field inputDTVField = null;

    static {
        try {
            inputDTVField = Parameter.class.getDeclaredField("inputDTV");
            inputDTVField.setAccessible(true);
        } catch (Throwable e) {
        }
    }
    //end

    String getClassNameInternal() {
        return "SQLServerPreparedStatement";
    }

    SQLServerPreparedStatement(SQLServerConnection var1, String var2, int var3, int var4) throws SQLServerException {
        super(var1, var3, var4);
        this.stmtPoolable = true;
        this.sqlCommand = var2;
        JDBCCallSyntaxTranslator var5 = new JDBCCallSyntaxTranslator();
        var2 = var5.translate(var2);
        this.procedureName = var5.getProcedureName();
        this.bReturnValueSyntax = var5.hasReturnValueSyntax();
        this.userSQL = var2;
        this.initParams(this.userSQL);
    }

    boolean isCloseOnCompletion = false;

    public boolean isCloseOnCompletion() throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();

        loggerExternal.entering(getClassNameLogging(), "isCloseOnCompletion");
        checkClosed();
        loggerExternal.exiting(getClassNameLogging(), "isCloseOnCompletion", isCloseOnCompletion);
        return isCloseOnCompletion;
    }

    public void closeOnCompletion() throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();

        loggerExternal.entering(getClassNameLogging(), "closeOnCompletion");

        checkClosed();

        // enable closeOnCompletion feature
        isCloseOnCompletion = true;

        loggerExternal.exiting(getClassNameLogging(), "closeOnCompletion");
    }

    private void closePreparedHandle() {
        if (0 != this.prepStmtHandle) {
            if (this.connection.isSessionUnAvailable()) {
                if (this.getStatementLogger().isLoggable(Level.FINER)) {
                    this.getStatementLogger().finer(this + ": Not closing PreparedHandle:" + this.prepStmtHandle + "; connection is already closed.");
                }
            } else {
                if (this.getStatementLogger().isLoggable(Level.FINER)) {
                    this.getStatementLogger().finer(this + ": Closing PreparedHandle:" + this.prepStmtHandle);
                }

                try {
                    final class PreparedHandleClose extends UninterruptableTDSCommand {
                        PreparedHandleClose() {
                            super("closePreparedHandle");
                        }

                        final boolean doExecute() throws SQLServerException {
                            TDSWriter var1 = this.startRequest(TDS.PKT_RPC);
                            var1.writeShort((short) 0xFFFF);
                            var1.writeShort(executedSqlDirectly ? TDS.PROCID_SP_UNPREPARE : TDS.PROCID_SP_CURSORUNPREPARE);
                            var1.writeByte((byte) 0);
                            var1.writeByte((byte) 0);
                            var1.writeRPCInt((String) null, new Integer(SQLServerPreparedStatement.this.prepStmtHandle), false);
                            SQLServerPreparedStatement.this.prepStmtHandle = 0;
                            TDSParser.parse(this.startResponse(), this.getLogContext());
                            return true;
                        }
                    }

                    this.executeCommand(new PreparedHandleClose());
                } catch (SQLServerException var2) {
                    if (this.getStatementLogger().isLoggable(Level.FINER)) {
                        this.getStatementLogger().log(Level.FINER, this + ": Error (ignored) closing PreparedHandle:" + this.prepStmtHandle, var2);
                    }
                }

                if (this.getStatementLogger().isLoggable(Level.FINER)) {
                    this.getStatementLogger().finer(this + ": Closed PreparedHandle:" + this.prepStmtHandle);
                }
            }

        }
    }

    final void closeInternal() {
        super.closeInternal();
        this.closePreparedHandle();
        this.batchParamValues = null;
    }

    final void initParams(String var1) {
        int var2 = 0;
        int var3 = -1;

        while (true) {
            ++var3;
            if ((var3 = ParameterUtils.scanSQLForChar('?', var1, var3)) >= var1.length()) {
                this.inOutParam = new Parameter[var2];

                for (int var4 = 0; var4 < var2; ++var4) {
                    this.inOutParam[var4] = new Parameter();
                }

                return;
            }

            ++var2;
        }
    }

    public final void clearParameters() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "clearParameters");
        this.checkClosed();
        if (this.inOutParam != null) {
            for (int var1 = 0; var1 < this.inOutParam.length; ++var1) {
                this.inOutParam[var1].clearInputValue();
            }

            loggerExternal.exiting(this.getClassNameLogging(), "clearParameters");
        }
    }

    private final boolean buildPreparedStrings(Parameter[] var1) throws SQLServerException {
        String var2 = this.buildParamTypeDefinitions(var1);
        if (null != this.preparedTypeDefinitions && var2.equals(this.preparedTypeDefinitions)) {
            return false;
        } else {
            this.preparedTypeDefinitions = var2;
            this.preparedSQL = this.connection.replaceParameterMarkers(this.userSQL, var1, this.bReturnValueSyntax);
            if (this.bRequestedGeneratedKeys) {
                this.preparedSQL = this.preparedSQL + " select SCOPE_IDENTITY() AS GENERATED_KEYS";
            }

            return true;
        }
    }

    private String buildParamTypeDefinitions(Parameter[] var1) throws SQLServerException {
        StringBuilder var2 = new StringBuilder();
        int var3 = var1.length;
        char[] var4 = new char[10];

        for (int var5 = 0; var5 < var3; ++var5) {
            if (var5 > 0) {
                var2.append(',');
            }

            SQLServerConnection var10000 = this.connection;
            int var6 = SQLServerConnection.makeParamName(var5, var4, 0);

            for (int var7 = 0; var7 < var6; ++var7) {
                var2.append(var4[var7]);
            }

            var2.append(' ');
            String var10 = var1[var5].getTypeDefinition(this.connection, this.resultsReader());
            if (null == var10) {
                MessageFormat var8 = new MessageFormat(SQLServerException.getErrString("R_valueNotSetForParameter"));
                Object[] var9 = new Object[]{new Integer(var5 + 1)};
                SQLServerException.makeFromDriverError(this.connection, this, var8.format(var9), (String) null, false);
            }

            //hacked
            if (null != inputDTVField) {
                try {
                    if (VARCHAR_8K.equals(var10)) {
                        Object o = inputDTVField.get(var1[var5]);
                        if (null != o) {
                            DTV dtv = (DTV) o;
                            int length = ((byte[]) dtv.getSetterValue()).length;
                            if (length > -1 && length < 8000) {
                                var10 = "varchar(" + length + ")";
                            }
                        }
                    } else if (NVARCHAR_4K.equals(var10)) {
                        Object o = inputDTVField.get(var1[var5]);
                        if (null != o) {
                            DTV dtv = (DTV) o;
                            int length = ((byte[]) dtv.getSetterValue()).length;
                            if (length > -1 && length < 4000) {
                                var10 = "nvarchar(" + length + ")";
                            }
                        }
                    }
                } catch (Throwable e) {
                }
            }
            //end

            var2.append(var10);
            if (var1[var5].isOutput()) {
                var2.append(" OUTPUT");
            }
        }

        return var2.toString();
    }

    public ResultSet executeQuery() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "executeQuery");
        if (loggerExternal.isLoggable(Level.FINER) && Util.IsActivityTraceOn()) {
            loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }

        this.checkClosed();
        this.executeStatement(new SQLServerPreparedStatement.PrepStmtExecCmd(this, 1));
        loggerExternal.exiting(this.getClassNameLogging(), "executeQuery");
        return this.resultSet;
    }

    final ResultSet executeQueryInternal() throws SQLServerException {
        this.checkClosed();
        this.executeStatement(new SQLServerPreparedStatement.PrepStmtExecCmd(this, 5));
        return this.resultSet;
    }

    public int executeUpdate() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "executeUpdate");
        if (loggerExternal.isLoggable(Level.FINER) && Util.IsActivityTraceOn()) {
            loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }

        this.checkClosed();
        this.executeStatement(new SQLServerPreparedStatement.PrepStmtExecCmd(this, 2));
        loggerExternal.exiting(this.getClassNameLogging(), "executeUpdate", new Integer(this.updateCount));
        return this.updateCount;
    }

    public boolean execute() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "execute");
        if (loggerExternal.isLoggable(Level.FINER) && Util.IsActivityTraceOn()) {
            loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }

        this.checkClosed();
        this.executeStatement(new SQLServerPreparedStatement.PrepStmtExecCmd(this, 3));
        loggerExternal.exiting(this.getClassNameLogging(), "execute", Boolean.valueOf(null != this.resultSet));
        return null != this.resultSet;
    }

    final void doExecutePreparedStatement(SQLServerPreparedStatement.PrepStmtExecCmd var1) throws SQLServerException {
        this.resetForReexecute();
        if (1 != this.executeMethod && 3 != this.executeMethod) {
            assert 2 == this.executeMethod || 4 == this.executeMethod || 5 == this.executeMethod;

            this.connection.setMaxRows(0);
        } else {
            this.connection.setMaxRows(this.maxRows);
            this.connection.setMaxFieldSize(this.maxFieldSize);
        }

        if (loggerExternal.isLoggable(Level.FINER) && Util.IsActivityTraceOn()) {
            loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }

        TDSWriter var2 = var1.startRequest(TDS.PKT_RPC);
        this.doPrepExec(var2, this.inOutParam);
        this.ensureExecuteResultsReader(var1.startResponse(this.getIsResponseBufferingAdaptive()));
        this.startResults();
        this.getNextResult();
        if (1 == this.executeMethod && null == this.resultSet) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_noResultset"), (String) null, true);
        } else if (2 == this.executeMethod && null != this.resultSet) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_resultsetGeneratedForUpdate"), (String) null, false);
        }

    }

    boolean consumeExecOutParam(TDSReader var1) throws SQLServerException {
        if (!this.expectPrepStmtHandle && !this.expectCursorOutParams) {
            return false;
        } else {
            final class PrepStmtExecOutParamHandler extends StmtExecOutParamHandler {
                boolean onRetValue(TDSReader var1) throws SQLServerException {
                    if (!SQLServerPreparedStatement.this.expectPrepStmtHandle) {
                        return super.onRetValue(var1);
                    } else {
                        SQLServerPreparedStatement.this.expectPrepStmtHandle = false;
                        Parameter var2 = new Parameter();
                        var2.skipRetValStatus(var1);
                        SQLServerPreparedStatement.this.prepStmtHandle = var2.getInt(var1);
                        var2.skipValue(var1, true);
                        if (SQLServerPreparedStatement.this.getStatementLogger().isLoggable(Level.FINER)) {
                            SQLServerPreparedStatement.this.getStatementLogger().finer(this.toString() + ": Setting PreparedHandle:" + SQLServerPreparedStatement.this.prepStmtHandle);
                        }

                        return true;
                    }
                }
            }

            TDSParser.parse(var1, new PrepStmtExecOutParamHandler());
            return true;
        }
    }

    void sendParamsByRPC(TDSWriter var1, Parameter[] var2) throws SQLServerException {
        for (int var3 = 0; var3 < this.inOutParam.length; ++var3) {
            var2[var3].sendByRPC(var1, this.connection);
        }

    }

    private final void buildServerCursorPrepExecParams(TDSWriter var1) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_cursorprepexec: PreparedHandle:" + this.prepStmtHandle + ", SQL:" + this.preparedSQL);
        }

        this.expectPrepStmtHandle = true;
        this.executedSqlDirectly = false;
        this.expectCursorOutParams = true;
        this.outParamIndexAdjustment = 7;
        var1.writeShort((short) 0xFFFF);
        var1.writeShort(TDS.PROCID_SP_CURSORPREPEXEC);
        var1.writeByte((byte) 0);
        var1.writeByte((byte) 0);
        var1.writeRPCInt((String) null, new Integer(this.prepStmtHandle), true);
        this.prepStmtHandle = 0;
        var1.writeRPCInt((String) null, new Integer(0), true);
        var1.writeRPCStringUnicode(this.preparedTypeDefinitions.length() > 0 ? this.preparedTypeDefinitions : null);
        var1.writeRPCStringUnicode(this.preparedSQL);
        var1.writeRPCInt((String) null, new Integer(this.getResultSetScrollOpt() & ~(0 == this.preparedTypeDefinitions.length() ? 4096 : 0)), false);
        var1.writeRPCInt((String) null, new Integer(this.getResultSetCCOpt()), false);
        var1.writeRPCInt((String) null, new Integer(0), true);
    }

    private final void buildPrepExecParams(TDSWriter var1) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_prepexec: PreparedHandle:" + this.prepStmtHandle + ", SQL:" + this.preparedSQL);
        }

        this.expectPrepStmtHandle = true;
        this.executedSqlDirectly = true;
        this.expectCursorOutParams = false;
        this.outParamIndexAdjustment = 3;
        var1.writeShort((short) 0xFFFF);
        var1.writeShort(TDS.PROCID_SP_PREPEXEC);
        var1.writeByte((byte) 0);
        var1.writeByte((byte) 0);
        var1.writeRPCInt((String) null, new Integer(this.prepStmtHandle), true);
        this.prepStmtHandle = 0;
        var1.writeRPCStringUnicode(this.preparedTypeDefinitions.length() > 0 ? this.preparedTypeDefinitions : null);
        var1.writeRPCStringUnicode(this.preparedSQL);
    }

    private final void buildServerCursorExecParams(TDSWriter var1) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_cursorexecute: PreparedHandle:" + this.prepStmtHandle + ", SQL:" + this.preparedSQL);
        }

        this.expectPrepStmtHandle = false;
        this.executedSqlDirectly = false;
        this.expectCursorOutParams = true;
        this.outParamIndexAdjustment = 5;
        var1.writeShort((short) 0xFFFF);
        var1.writeShort(TDS.PROCID_SP_CURSOREXECUTE);
        var1.writeByte((byte) 0);
        var1.writeByte((byte) 0);

        assert 0 != this.prepStmtHandle;

        var1.writeRPCInt((String) null, new Integer(this.prepStmtHandle), false);
        var1.writeRPCInt((String) null, new Integer(0), true);
        var1.writeRPCInt((String) null, new Integer(this.getResultSetScrollOpt() & -4097), false);
        var1.writeRPCInt((String) null, new Integer(this.getResultSetCCOpt()), false);
        var1.writeRPCInt((String) null, new Integer(0), true);
    }

    private final void buildExecParams(TDSWriter var1) throws SQLServerException {
        if (this.getStatementLogger().isLoggable(Level.FINE)) {
            this.getStatementLogger().fine(this.toString() + ": calling sp_execute: PreparedHandle:" + this.prepStmtHandle + ", SQL:" + this.preparedSQL);
        }

        this.expectPrepStmtHandle = false;
        this.executedSqlDirectly = true;
        this.expectCursorOutParams = false;
        this.outParamIndexAdjustment = 1;
        var1.writeShort((short) 0xFFFF);
        var1.writeShort(TDS.PROCID_SP_EXECUTE);
        var1.writeByte((byte) 0);
        var1.writeByte((byte) 0);

        assert 0 != this.prepStmtHandle;

        var1.writeRPCInt((String) null, new Integer(this.prepStmtHandle), false);
    }

    private final boolean doPrepExec(TDSWriter var1, Parameter[] var2) throws SQLServerException {
        boolean var3 = this.buildPreparedStrings(var2) || 0 == this.prepStmtHandle;
        if (var3) {
            if (this.isCursorable(this.executeMethod)) {
                this.buildServerCursorPrepExecParams(var1);
            } else {
                this.buildPrepExecParams(var1);
            }
        } else if (this.isCursorable(this.executeMethod)) {
            this.buildServerCursorExecParams(var1);
        } else {
            this.buildExecParams(var1);
        }

        this.sendParamsByRPC(var1, var2);
        return var3;
    }

    public final ResultSetMetaData getMetaData() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getMetaData");
        this.checkClosed();
        boolean var1 = false;
        ResultSetMetaData var2 = null;

        try {
            if (this.resultSet != null) {
                this.resultSet.checkClosed();
            }
        } catch (SQLServerException var4) {
            var1 = true;
        }

        if (this.resultSet != null && !var1) {
            if (this.resultSet != null) {
                var2 = this.resultSet.getMetaData();
            }
        } else {
            SQLServerResultSet var3 = (SQLServerResultSet) this.buildExecuteMetaData();
            if (null != var3) {
                var2 = var3.getMetaData();
            }
        }

        loggerExternal.exiting(this.getClassNameLogging(), "getMetaData", var2);
        return var2;
    }

    private ResultSet buildExecuteMetaData() throws SQLServerException {
        String var1 = this.sqlCommand;
        if (var1.indexOf(123) >= 0) {
            var1 = (new JDBCCallSyntaxTranslator()).translate(var1);
        }

        SQLServerResultSet var2 = null;

        try {
            var1 = replaceMarkerWithNull(var1);
            SQLServerStatement var3 = (SQLServerStatement) this.connection.createStatement();
            var2 = var3.executeQueryInternal("set fmtonly on " + var1 + "\nset fmtonly off");
        } catch (SQLException var6) {
            if (!var6.getMessage().equals(SQLServerException.getErrString("R_noResultset"))) {
                MessageFormat var4 = new MessageFormat(SQLServerException.getErrString("R_processingError"));
                Object[] var5 = new Object[]{new String(var6.getMessage())};
                SQLServerException.makeFromDriverError(this.connection, this, var4.format(var5), (String) null, true);
            }
        }

        return var2;
    }

    final Parameter setterGetParam(int var1) throws SQLServerException {
        if (var1 < 1 || var1 > this.inOutParam.length) {
            MessageFormat var2 = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
            Object[] var3 = new Object[]{new Integer(var1)};
            SQLServerException.makeFromDriverError(this.connection, this, var2.format(var3), "07009", false);
        }

        return this.inOutParam[var1 - 1];
    }

    final void setValue(int var1, JDBCType var2, Object var3, JavaType var4) throws SQLServerException {
        this.setterGetParam(var1).setValue(var2, var3, var4, (StreamSetterArgs) null, (Calendar) null, (Integer) null, this.connection);
    }

    final void setValue(int var1, JDBCType var2, Object var3, JavaType var4, Calendar var5) throws SQLServerException {
        this.setterGetParam(var1).setValue(var2, var3, var4, (StreamSetterArgs) null, var5, (Integer) null, this.connection);
    }

    final void setStream(int var1, StreamType var2, Object var3, JavaType var4, long var5) throws SQLServerException {
        this.setterGetParam(var1).setValue(var2.getJDBCType(), var3, var4, new StreamSetterArgs(var2, var5), (Calendar) null, (Integer) null, this.connection);
    }

    final void setSQLXMLInternal(int var1, SQLXML var2) throws SQLServerException {
        this.setterGetParam(var1).setValue(JDBCType.SQLXML, var2, JavaType.SQLXML, new StreamSetterArgs(StreamType.SQLXML, -1L), (Calendar) null, (Integer) null, this.connection);
    }

    public final void setAsciiStream(int var1, InputStream var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.ASCII, var2, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }

    public final void setAsciiStream(int var1, InputStream var2, int var3) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[]{Integer.valueOf(var1), var2, Integer.valueOf(var3)});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.ASCII, var2, JavaType.INPUTSTREAM, (long) var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }

    public final void setAsciiStream(int var1, InputStream var2, long var3) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setAsciiStream", new Object[]{Integer.valueOf(var1), var2, Long.valueOf(var3)});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.ASCII, var2, JavaType.INPUTSTREAM, var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setAsciiStream");
    }

    private final Parameter getParam(int var1) throws SQLServerException {
        --var1;
        if (var1 < 0 || var1 >= this.inOutParam.length) {
            MessageFormat var2 = new MessageFormat(SQLServerException.getErrString("R_indexOutOfRange"));
            Object[] var3 = new Object[]{new Integer(var1 + 1)};
            SQLServerException.makeFromDriverError(this.connection, this, var2.format(var3), "07009", false);
        }

        return this.inOutParam[var1];
    }

    public final void setBigDecimal(int var1, BigDecimal var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBigDecimal", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.DECIMAL, var2, JavaType.BIGDECIMAL);
        loggerExternal.exiting(this.getClassNameLogging(), "setBigDecimal");
    }

    public final void setBinaryStream(int var1, InputStream var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBinaryStreaml", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.BINARY, var2, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }

    public final void setBinaryStream(int var1, InputStream var2, int var3) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[]{Integer.valueOf(var1), var2, Integer.valueOf(var3)});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.BINARY, var2, JavaType.INPUTSTREAM, (long) var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }

    public final void setBinaryStream(int var1, InputStream var2, long var3) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBinaryStream", new Object[]{Integer.valueOf(var1), var2, Long.valueOf(var3)});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.BINARY, var2, JavaType.INPUTSTREAM, var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setBinaryStream");
    }

    public final void setBoolean(int var1, boolean var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBoolean", new Object[]{Integer.valueOf(var1), Boolean.valueOf(var2)});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.BIT, Boolean.valueOf(var2), JavaType.BOOLEAN);
        loggerExternal.exiting(this.getClassNameLogging(), "setBoolean");
    }

    public final void setByte(int var1, byte var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setByte", new Object[]{Integer.valueOf(var1), Byte.valueOf(var2)});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.TINYINT, Byte.valueOf(var2), JavaType.BYTE);
        loggerExternal.exiting(this.getClassNameLogging(), "setByte");
    }

    public final void setBytes(int var1, byte[] var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBytes", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.BINARY, var2, JavaType.BYTEARRAY);
        loggerExternal.exiting(this.getClassNameLogging(), "setBytes");
    }

    public final void setDouble(int var1, double var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDouble", new Object[]{Integer.valueOf(var1), Double.valueOf(var2)});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.DOUBLE, Double.valueOf(var2), JavaType.DOUBLE);
        loggerExternal.exiting(this.getClassNameLogging(), "setDouble");
    }

    public final void setFloat(int var1, float var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setFloat", new Object[]{Integer.valueOf(var1), Float.valueOf(var2)});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.REAL, Float.valueOf(var2), JavaType.FLOAT);
        loggerExternal.exiting(this.getClassNameLogging(), "setFloat");
    }

    public final void setInt(int var1, int var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setInt", new Object[]{Integer.valueOf(var1), Integer.valueOf(var2)});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.INTEGER, Integer.valueOf(var2), JavaType.INTEGER);
        loggerExternal.exiting(this.getClassNameLogging(), "setInt");
    }

    public final void setLong(int var1, long var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setLong", new Object[]{Integer.valueOf(var1), Long.valueOf(var2)});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.BIGINT, Long.valueOf(var2), JavaType.LONG);
        loggerExternal.exiting(this.getClassNameLogging(), "setLong");
    }

    public final void setNull(int var1, int var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNull", new Object[]{Integer.valueOf(var1), Integer.valueOf(var2)});
        }

        this.checkClosed();
        this.setObject(this.setterGetParam(var1), (Object) null, JavaType.OBJECT, JDBCType.of(var2), (Integer) null);
        loggerExternal.exiting(this.getClassNameLogging(), "setNull");
    }

    final void setObjectNoType(int var1, Object var2) throws SQLServerException {
        Parameter var3 = this.setterGetParam(var1);
        JDBCType var4 = var3.getJdbcType();
        if (null == var2) {
            if (JDBCType.UNKNOWN == var4) {
                var4 = JDBCType.CHAR;
            }

            this.setObject(var3, (Object) null, JavaType.OBJECT, var4, (Integer) null);
        } else {
            JavaType var5 = JavaType.of(var2);
            var4 = var5.getJDBCType(SSType.UNKNOWN, var4);
            this.setObject(var3, var2, var5, var4, (Integer) null);
        }

    }

    public final void setObject(int var1, Object var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setObjectNoType(var1, var2);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    public final void setObject(int var1, Object var2, int var3) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{Integer.valueOf(var1), var2, Integer.valueOf(var3)});
        }

        this.checkClosed();
        this.setObject(this.setterGetParam(var1), var2, JavaType.of(var2), JDBCType.of(var3), (Integer) null);
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    public final void setObject(int var1, Object var2, int var3, int var4) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setObject", new Object[]{Integer.valueOf(var1), var2, Integer.valueOf(var3), Integer.valueOf(var4)});
        }

        this.checkClosed();
        this.setObject(this.setterGetParam(var1), var2, JavaType.of(var2), JDBCType.of(var3), 2 != var3 && 3 != var3 && !InputStream.class.isInstance(var2) && !Reader.class.isInstance(var2) ? null : Integer.valueOf(var4));
        loggerExternal.exiting(this.getClassNameLogging(), "setObject");
    }

    final void setObject(Parameter var1, Object var2, JavaType var3, JDBCType var4, Integer var5) throws SQLServerException {
        assert JDBCType.UNKNOWN != var4;

        if (null != var2) {
            JDBCType var6 = var3.getJDBCType(SSType.UNKNOWN, var4);
            if (!var6.convertsTo(var4)) {
                DataTypes.throwConversionError(var6.toString(), var4.toString());
            }

            StreamSetterArgs var7 = null;
            switch (var3) {
                case READER:
                    var7 = new StreamSetterArgs(StreamType.CHARACTER, -1L);
                    break;
                case INPUTSTREAM:
                    var7 = new StreamSetterArgs(var4.isTextual() ? StreamType.CHARACTER : StreamType.BINARY, -1L);
                    break;
                case SQLXML:
                    var7 = new StreamSetterArgs(StreamType.SQLXML, -1L);
            }

            var1.setValue(var4, var2, var3, var7, (Calendar) null, var5, this.connection);
        } else {
            assert JavaType.OBJECT == var3;

            if (var4.isUnsupported()) {
                var4 = JDBCType.BINARY;
            }

            var1.setValue(var4, (Object) null, JavaType.OBJECT, (StreamSetterArgs) null, (Calendar) null, var5, this.connection);
        }

    }

    public final void setShort(int var1, short var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setShort", new Object[]{Integer.valueOf(var1), Short.valueOf(var2)});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.SMALLINT, Short.valueOf(var2), JavaType.SHORT);
        loggerExternal.exiting(this.getClassNameLogging(), "setShort");
    }

    public final void setString(int var1, String var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setString", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.VARCHAR, var2, JavaType.STRING);
        loggerExternal.exiting(this.getClassNameLogging(), "setString");
    }

    public final void setNString(int var1, String var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNString", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.NVARCHAR, var2, JavaType.STRING);
        loggerExternal.exiting(this.getClassNameLogging(), "setNString");
    }

    public final void setTime(int var1, Time var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.TIME, var2, JavaType.TIME);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    public final void setTimestamp(int var1, Timestamp var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.TIMESTAMP, var2, JavaType.TIMESTAMP);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }

    public final void setDateTimeOffset(int var1, DateTimeOffset var2) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDateTimeOffset", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.DATETIMEOFFSET, var2, JavaType.DATETIMEOFFSET);
        loggerExternal.exiting(this.getClassNameLogging(), "setDateTimeOffset");
    }

    public final void setDate(int var1, Date var2) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.DATE, var2, JavaType.DATE);
        loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }

    /** @deprecated */
    @Deprecated
    public final void setUnicodeStream(int var1, InputStream var2, int var3) throws SQLException {
        this.NotImplemented();
    }

    public final void addBatch() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "addBatch");
        this.checkClosed();
        if (this.batchParamValues == null) {
            this.batchParamValues = new ArrayList();
        }

        int var1 = this.inOutParam.length;
        Parameter[] var2 = new Parameter[var1];

        for (int var3 = 0; var3 < var1; ++var3) {
            var2[var3] = this.inOutParam[var3].cloneForBatch();
        }

        this.batchParamValues.add(var2);
        loggerExternal.exiting(this.getClassNameLogging(), "addBatch");
    }

    public final void clearBatch() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "clearBatch");
        this.checkClosed();
        this.batchParamValues = null;
        loggerExternal.exiting(this.getClassNameLogging(), "clearBatch");
    }

    public int[] executeBatch() throws SQLServerException, BatchUpdateException {
        loggerExternal.entering(this.getClassNameLogging(), "executeBatch");
        if (loggerExternal.isLoggable(Level.FINER) && Util.IsActivityTraceOn()) {
            loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }

        this.checkClosed();
        this.discardLastExecutionResults();
        int[] var1;
        if (this.batchParamValues == null) {
            var1 = new int[0];
        } else {
            try {
                for (int var2 = 0; var2 < this.batchParamValues.size(); ++var2) {
                    Parameter[] var3 = (Parameter[]) this.batchParamValues.get(var2);

                    for (int var4 = 0; var4 < var3.length; ++var4) {
                        if (var3[var4].isOutput()) {
                            throw new BatchUpdateException(SQLServerException.getErrString("R_outParamsNotPermittedinBatch"), (String) null, 0, (int[]) null);
                        }
                    }
                }

                SQLServerPreparedStatement.PrepStmtBatchExecCmd var8 = new SQLServerPreparedStatement.PrepStmtBatchExecCmd(this);
                this.executeStatement(var8);
                if (null != var8.batchException) {
                    throw new BatchUpdateException(var8.batchException.getMessage(), var8.batchException.getSQLState(), var8.batchException.getErrorCode(), var8.updateCounts);
                }

                var1 = var8.updateCounts;
            } finally {
                this.batchParamValues = null;
            }
        }

        loggerExternal.exiting(this.getClassNameLogging(), "executeBatch", var1);
        return var1;
    }

    final void doExecutePreparedStatementBatch(SQLServerPreparedStatement.PrepStmtBatchExecCmd var1) throws SQLServerException {
        this.executeMethod = 4;
        var1.batchException = null;
        int var2 = this.batchParamValues.size();
        var1.updateCounts = new int[var2];

        int var3;
        for (var3 = 0; var3 < var2; ++var3) {
            var1.updateCounts[var3] = -3;
        }

        var3 = 0;
        int var4 = 0;
        if (this.isSelect(this.userSQL)) {
            SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_selectNotPermittedinBatch"), (String) null, true);
        }

        this.connection.setMaxRows(0);
        if (loggerExternal.isLoggable(Level.FINER) && Util.IsActivityTraceOn()) {
            loggerExternal.finer(this.toString() + " ActivityId: " + ActivityCorrelator.getNext().toString());
        }

        Parameter[] var5 = new Parameter[this.inOutParam.length];
        TDSWriter var6 = null;

        do {
            do {
                if (var4 >= var2) {
                    return;
                }

                Parameter[] var7 = (Parameter[]) this.batchParamValues.get(var3);

                assert var7.length == var5.length;

                for (int var8 = 0; var8 < var7.length; ++var8) {
                    var5[var8] = var7[var8];
                }

                if (var4 < var3) {
                    var6.writeByte((byte) nBatchStatementDelimiter);
                } else {
                    this.resetForReexecute();
                    var6 = var1.startRequest(TDS.PKT_RPC);
                }

                ++var3;
            } while (!this.doPrepExec(var6, var5) && var3 != var2);

            this.ensureExecuteResultsReader(var1.startResponse(this.getIsResponseBufferingAdaptive()));

            while (var4 < var3) {
                this.startResults();

                try {
                    if (!this.getNextResult()) {
                        return;
                    }

                    if (null != this.resultSet) {
                        SQLServerException.makeFromDriverError(this.connection, this, SQLServerException.getErrString("R_resultsetGeneratedForUpdate"), (String) null, false);
                    }
                } catch (SQLServerException var9) {
                    if (this.connection.isSessionUnAvailable() || this.connection.rolledBackTransaction()) {
                        throw var9;
                    }

                    this.updateCount = -3;
                    if (null == var1.batchException) {
                        var1.batchException = var9;
                    }
                }

                var1.updateCounts[var4++] = -1 == this.updateCount ? -2 : this.updateCount;
                this.processBatch();
            }
        } while (doPrepExec(var6, var5) || var4 == var3);

        throw new AssertionError();
    }

    public final void setCharacterStream(int var1, Reader var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.CHARACTER, var2, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }

    public final void setCharacterStream(int var1, Reader var2, int var3) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[]{Integer.valueOf(var1), var2, Integer.valueOf(var3)});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.CHARACTER, var2, JavaType.READER, (long) var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }

    public final void setCharacterStream(int var1, Reader var2, long var3) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setCharacterStream", new Object[]{Integer.valueOf(var1), var2, Long.valueOf(var3)});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.CHARACTER, var2, JavaType.READER, var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setCharacterStream");
    }

    public final void setNCharacterStream(int var1, Reader var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNCharacterStream", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.NCHARACTER, var2, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setNCharacterStream");
    }

    public final void setNCharacterStream(int var1, Reader var2, long var3) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNCharacterStream", new Object[]{Integer.valueOf(var1), var2, Long.valueOf(var3)});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.NCHARACTER, var2, JavaType.READER, var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setNCharacterStream");
    }

    public final void setRef(int var1, Ref var2) throws SQLServerException {
        this.NotImplemented();
    }

    public final void setBlob(int var1, Blob var2) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.BLOB, var2, JavaType.BLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }

    public final void setBlob(int var1, InputStream var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.BINARY, var2, JavaType.INPUTSTREAM, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }

    public final void setBlob(int var1, InputStream var2, long var3) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setBlob", new Object[]{Integer.valueOf(var1), var2, Long.valueOf(var3)});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.BINARY, var2, JavaType.INPUTSTREAM, var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setBlob");
    }

    public final void setClob(int var1, Clob var2) throws SQLException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.CLOB, var2, JavaType.CLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }

    public final void setClob(int var1, Reader var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.CHARACTER, var2, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }

    public final void setClob(int var1, Reader var2, long var3) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setClob", new Object[]{Integer.valueOf(var1), var2, Long.valueOf(var3)});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.CHARACTER, var2, JavaType.READER, var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setClob");
    }

    public final void setNClob(int var1, NClob var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.NCLOB, var2, JavaType.NCLOB);
        loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }

    public final void setNClob(int var1, Reader var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.NCHARACTER, var2, JavaType.READER, -1L);
        loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }

    public final void setNClob(int var1, Reader var2, long var3) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNClob", new Object[]{Integer.valueOf(var1), var2, Long.valueOf(var3)});
        }

        this.checkClosed();
        this.setStream(var1, StreamType.NCHARACTER, var2, JavaType.READER, var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setNClob");
    }

    public final void setArray(int var1, Array var2) throws SQLServerException {
        this.NotImplemented();
    }

    public final void setDate(int var1, Date var2, Calendar var3) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setDate", new Object[]{Integer.valueOf(var1), var2, var3});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.DATE, var2, JavaType.DATE, var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setDate");
    }

    public final void setTime(int var1, Time var2, Calendar var3) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTime", new Object[]{Integer.valueOf(var1), var2, var3});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.TIME, var2, JavaType.TIME, var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setTime");
    }

    public final void setTimestamp(int var1, Timestamp var2, Calendar var3) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setTimestamp", new Object[]{Integer.valueOf(var1), var2, var3});
        }

        this.checkClosed();
        this.setValue(var1, JDBCType.TIMESTAMP, var2, JavaType.TIMESTAMP, var3);
        loggerExternal.exiting(this.getClassNameLogging(), "setTimestamp");
    }

    public final void setNull(int var1, int var2, String var3) throws SQLServerException {
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setNull", new Object[]{Integer.valueOf(var1), Integer.valueOf(var2), var3});
        }

        this.checkClosed();
        this.setObject(this.setterGetParam(var1), (Object) null, JavaType.OBJECT, JDBCType.of(var2), (Integer) null);
        loggerExternal.exiting(this.getClassNameLogging(), "setNull");
    }

    public final ParameterMetaData getParameterMetaData() throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "getParameterMetaData");
        this.checkClosed();
        SQLServerParameterMetaData var1 = new SQLServerParameterMetaData(this, this.userSQL);
        loggerExternal.exiting(this.getClassNameLogging(), "getParameterMetaData", var1);
        return var1;
    }

    public final void setURL(int var1, URL var2) throws SQLServerException {
        this.NotImplemented();
    }

    public final void setRowId(int var1, RowId var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        throw new SQLFeatureNotSupportedException(SQLServerException.getErrString("R_notSupported"));
    }

    public final void setSQLXML(int var1, SQLXML var2) throws SQLException {
        DriverJDBCVersion.checkSupportsJDBC4();
        if (loggerExternal.isLoggable(Level.FINER)) {
            loggerExternal.entering(this.getClassNameLogging(), "setSQLXML", new Object[]{Integer.valueOf(var1), var2});
        }

        this.checkClosed();
        this.setSQLXMLInternal(var1, var2);
        loggerExternal.exiting(this.getClassNameLogging(), "setSQLXML");
    }

    public final int executeUpdate(String var1) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "executeUpdate", var1);
        MessageFormat var2 = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        Object[] var3 = new Object[]{new String("executeUpdate()")};
        throw new SQLServerException(this, var2.format(var3), (String) null, 0, false);
    }

    public final boolean execute(String var1) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "execute", var1);
        MessageFormat var2 = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        Object[] var3 = new Object[]{new String("execute()")};
        throw new SQLServerException(this, var2.format(var3), (String) null, 0, false);
    }

    public final ResultSet executeQuery(String var1) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "executeQuery", var1);
        MessageFormat var2 = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        Object[] var3 = new Object[]{new String("executeQuery()")};
        throw new SQLServerException(this, var2.format(var3), (String) null, 0, false);
    }

    public void addBatch(String var1) throws SQLServerException {
        loggerExternal.entering(this.getClassNameLogging(), "addBatch", var1);
        MessageFormat var2 = new MessageFormat(SQLServerException.getErrString("R_cannotTakeArgumentsPreparedOrCallable"));
        Object[] var3 = new Object[]{new String("addBatch()")};
        throw new SQLServerException(this, var2.format(var3), (String) null, 0, false);
    }

    private final class PrepStmtBatchExecCmd extends TDSCommand {
        private final SQLServerPreparedStatement stmt;
        SQLServerException batchException;
        int[] updateCounts;

        PrepStmtBatchExecCmd(SQLServerPreparedStatement var2) {
            super(var2.toString() + " executeBatch", SQLServerPreparedStatement.this.queryTimeout);
            this.stmt = var2;
        }

        final boolean doExecute() throws SQLServerException {
            this.stmt.doExecutePreparedStatementBatch(this);
            return true;
        }

        final void processResponse(TDSReader var1) throws SQLServerException {
            SQLServerPreparedStatement.this.ensureExecuteResultsReader(var1);
            SQLServerPreparedStatement.this.processExecuteResults();
        }
    }

    private final class PrepStmtExecCmd extends TDSCommand {
        private final SQLServerPreparedStatement stmt;

        PrepStmtExecCmd(SQLServerPreparedStatement var2, int var3) {
            super(var2.toString() + " executeXXX", SQLServerPreparedStatement.this.queryTimeout);
            this.stmt = var2;
            var2.executeMethod = var3;
        }

        final boolean doExecute() throws SQLServerException {
            this.stmt.doExecutePreparedStatement(this);
            return false;
        }

        final void processResponse(TDSReader var1) throws SQLServerException {
            SQLServerPreparedStatement.this.ensureExecuteResultsReader(var1);
            SQLServerPreparedStatement.this.processExecuteResults();
        }
    }
}
