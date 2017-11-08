package com.ctrip.platform.dal.dao.client;

import java.util.Map;

import com.ctrip.platform.dal.dao.markdown.MarkDownInfo;
import com.ctrip.platform.dal.dao.markdown.MarkupInfo;
import com.ctrip.platform.dal.dao.task.DalRequest;

/**
 * A sandbox that prevent customized logger's exception break main flow
 * 
 * TODO add async log capability
 * 
 * @author jhhe
 *
 */
public class DalSafeLogger implements DalLogger {
    private DalLogger logger;
    
    public DalSafeLogger(DalLogger logger) {
        this.logger = logger;
    }
    
    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        try{
            logger.initialize(settings);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void info(String msg) {
        try{
            logger.info(msg);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void warn(String msg) {
        try{
            logger.warn(msg);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void error(String msg, Throwable e) {
        try{
            logger.error(msg, e);
        }catch(Throwable e1){
            e1.printStackTrace();
        }
    }

    @Override
    public void getConnectionFailed(String dbName, Throwable e) {
        try{
            logger.getConnectionFailed(dbName, e);
        }catch(Throwable e1){
            e1.printStackTrace();
        }
    }

    @Override
    public <T> LogContext start(DalRequest<T> request) {
        try{
            return logger.start(request);
        }catch(Throwable e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void end(LogContext logContext, Throwable e) {
        try{
            logger.end(logContext, e);
        }catch(Throwable e1){
            e1.printStackTrace();
        }
    }

    @Override
    public void startCrossShardTasks(LogContext logContext, boolean isSequentialExecution) {
        try{
            logger.startCrossShardTasks(logContext, isSequentialExecution);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void endCrossShards(LogContext logContext, Throwable e) {
        try{
            logger.endCrossShards(logContext, e);
        }catch(Throwable e1){
            e1.printStackTrace();
        }
    }

    @Override
    public void startTask(LogContext logContext, String shard) {
        try{
            logger.startTask(logContext, shard);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void endTask(LogContext logContext, String shard, Throwable e) {
        try{
            logger.endTask(logContext, shard, e);
        }catch(Throwable e1){
            e1.printStackTrace();
        }
    }

    @Override
    public LogEntry createLogEntry() {
        try{
            return logger.createLogEntry();
        }catch(Throwable e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void start(LogEntry entry) {
        try{
            logger.start(entry);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void startStatement(LogEntry entry) {
        try{
            logger.startStatement(entry);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void endStatement(LogEntry entry, Throwable e) {
        try{
            logger.endStatement(entry, e);
        }catch(Throwable e1){
            e1.printStackTrace();
        }
    }

    @Override
    public void success(LogEntry entry, int count) {
        try{
            logger.success(entry, count);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void fail(LogEntry entry, Throwable e) {
        try{
            logger.fail(entry, e);
        }catch(Throwable e1){
            e1.printStackTrace();
        }
    }

    @Override
    public void markdown(MarkDownInfo markdown) {
        try{
            logger.markdown(markdown);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public void markup(MarkupInfo markup) {
        try{
            logger.markup(markup);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    @Override
    public String getAppID() {
        try{
            return logger.getAppID();
        }catch(Throwable e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void shutdown() {
        try{
            logger.shutdown();
        }catch(Throwable e){
            e.printStackTrace();
        }
    }
}
