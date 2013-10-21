package com.ctrip.sysdev.das.console;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.ctrip.sysdev.das.console.resource.DbResource;

@ApplicationPath("/")
public class DasConsoleApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register root resource
        classes.add(DbResource.class);
        return classes;
    }
}
