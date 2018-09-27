package com.ctrip.platform.dal.sharding.idgen;

public class NullIdGeneratorFactory implements IIdGeneratorFactory {

    private IdGenerator nullIdGenerator = new NullIdGeneratorImpl();

    public IdGenerator getIdGenerator(String sequenceName) {
        return nullIdGenerator;
    }

    class NullIdGeneratorImpl implements NullIdGenerator {
        @Override
        public Number nextId() {
            return null;
        }
    }

}
