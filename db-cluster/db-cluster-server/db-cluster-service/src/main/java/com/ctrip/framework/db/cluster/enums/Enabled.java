package com.ctrip.framework.db.cluster.enums;

public enum Enabled {

    un_enabled(0) {
        @Override
        public boolean convertToBoolean() {
            return false;
        }
    },

    enabled(1) {
        @Override
        public boolean convertToBoolean() {
            return true;
        }
    };

    Enabled(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }

    public abstract boolean convertToBoolean();

    public static Enabled getEnabled(final int code) {
        for (Enabled e : values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        throw new IllegalArgumentException(String.format("enabled code can't match, code parameter is %d.", code));
    }
}
