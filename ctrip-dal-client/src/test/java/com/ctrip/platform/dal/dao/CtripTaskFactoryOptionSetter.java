package com.ctrip.platform.dal.dao;

public class CtripTaskFactoryOptionSetter {
    public static void callSpByIndex() {
        CtripTaskFactory.callSpbySqlServerSyntax = false;
        CtripTaskFactory.callSpbyName = false;
        CtripTaskFactory.callSpt = false;
    }
    
    public static void callSpByName() {
        CtripTaskFactory.callSpbySqlServerSyntax = false;
        CtripTaskFactory.callSpbyName = true;
        CtripTaskFactory.callSpt = false;
    }
    
    public static void callSpBySpt() {
        CtripTaskFactory.callSpbySqlServerSyntax = false;
        CtripTaskFactory.callSpbyName = false;
        CtripTaskFactory.callSpt = true;
    }
    
    public static void callSpByNativeSyntax() {
        CtripTaskFactory.callSpbySqlServerSyntax = true;
        CtripTaskFactory.callSpbyName = false;
        CtripTaskFactory.callSpt = false;
    }
    
}