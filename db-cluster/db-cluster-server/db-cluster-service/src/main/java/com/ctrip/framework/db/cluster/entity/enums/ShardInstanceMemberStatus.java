package com.ctrip.framework.db.cluster.entity.enums;

public enum ShardInstanceMemberStatus {

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


    ShardInstanceMemberStatus(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }

    public abstract boolean convertToBoolean();

    public static ShardInstanceMemberStatus getShardInstanceMemberStatus(final int code) {
        for (ShardInstanceMemberStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException(String.format("shardInstanceMemberStatus code can't match, code parameter is %d.", code));
    }
}
