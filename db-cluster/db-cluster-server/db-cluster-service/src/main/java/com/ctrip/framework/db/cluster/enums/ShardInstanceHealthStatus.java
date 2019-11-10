package com.ctrip.framework.db.cluster.enums;

public enum ShardInstanceHealthStatus {

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

    ShardInstanceHealthStatus(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }

    public abstract boolean convertToBoolean();

    public static ShardInstanceHealthStatus getShardInstanceHealthStatus(final int code) {
        for (ShardInstanceHealthStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException(String.format("shardInstanceHealthStatus code can't match, code parameter is %d.", code));
    }
}
