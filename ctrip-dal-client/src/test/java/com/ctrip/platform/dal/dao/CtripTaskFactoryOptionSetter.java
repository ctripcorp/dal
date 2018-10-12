package com.ctrip.platform.dal.dao;

public class CtripTaskFactoryOptionSetter {
    public static void callSpByIndex() {
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpbySqlServerSyntax(false);
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpByName(false);
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpt(false);
    }
    
    public static void callSpByName() {
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpbySqlServerSyntax(false);
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpByName(true);
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpt(false);
    }
    
    public static void callSpBySpt() {
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpbySqlServerSyntax(false);
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpByName(false);
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpt(true);
    }
    
    public static void callSpByNativeSyntax() {
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpbySqlServerSyntax(true);
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpByName(false);
        ((CtripTaskFactory)DalClientFactory.getTaskFactory()).setCallSpt(false);
    }
    
}