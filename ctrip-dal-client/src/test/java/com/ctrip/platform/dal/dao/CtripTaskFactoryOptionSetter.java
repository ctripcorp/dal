package com.ctrip.platform.dal.dao;

public class CtripTaskFactoryOptionSetter {
    public static void callSpByIndex() throws Exception {
        CtripTaskFactory.callSpbySqlServerSyntax = false;
        CtripTaskFactory.callSpbyName = false;
        CtripTaskFactory.callSpt = false;
    }
    
    public static void callSpByName() throws Exception {
        CtripTaskFactory.callSpbySqlServerSyntax = false;
        CtripTaskFactory.callSpbyName = true;
        CtripTaskFactory.callSpt = false;
    }
    
    public static void callSpt() throws Exception {
        CtripTaskFactory.callSpbySqlServerSyntax = false;
        CtripTaskFactory.callSpbyName = false;
        CtripTaskFactory.callSpt = true;
    }
    
    public static void callSpByNativeSyntax() throws Exception {
        CtripTaskFactory.callSpbySqlServerSyntax = true;
        CtripTaskFactory.callSpbyName = false;
        CtripTaskFactory.callSpt = false;
    }
    
}