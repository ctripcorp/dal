package com.ctrip.framework.dal.dbconfig.plugin.exception;

/**
 * @author c7ch23en
 */
public class DbConfigPluginException extends RuntimeException {

    public DbConfigPluginException() {
        super();
    }

    public DbConfigPluginException(String message) {
        super(message);
    }

    public DbConfigPluginException(Throwable cause) {
        super(cause);
    }

    public DbConfigPluginException(String message, Throwable cause) {
        super(message, cause);
    }

}
