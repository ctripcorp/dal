package com.ctrip.platform.dal.dao.datasource.tomcat;

/**
 * @author c7ch23en
 */
public class MockException extends RuntimeException {

    public MockException() {
        super();
    }

    public MockException(String message) {
        super(message);
    }

    public MockException(String message, Throwable cause) {
        super(message, cause);
    }

    public MockException(Throwable cause) {
        super(cause);
    }

}
