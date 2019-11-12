package com.ctrip.framework.dal.dbconfig.plugin.entity.titan;


/**
 * Created by lzyan on 2017/09/11.
 */
public class ConnectionCheckOutputEntity {
    protected boolean success;
    protected String message;

    //constructor
    public ConnectionCheckOutputEntity() {
    }
    public ConnectionCheckOutputEntity(boolean success, String message) {
        this.success = success;
        this.message = message;
    }



    //setter/getter
    public boolean getSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ConnectionCheckOutputEntity{");
        sb.append("success='").append(success).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append('}');
        return sb.toString();
    }

    protected boolean equals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else if (o2 == null) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConnectionCheckOutputEntity) {
            ConnectionCheckOutputEntity _o = (ConnectionCheckOutputEntity) obj;

            if (!equals(success, _o.getSuccess())) {
                return false;
            }
            if (!equals(message, _o.getMessage())) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash * 31 + (success ? 1 : 0);
        hash = hash * 31 + (message == null ? 0 : message.hashCode());
        return hash;
    }



}
