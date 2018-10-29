package com.ctrip.platform.dal.sharding.idgen;

public class NullIdGeneratorFactory implements IIdGeneratorFactory {

    private IdGenerator nullIdGenerator = new NullIdGeneratorImpl();

    @Override
    public IdGenerator getIdGenerator(String sequenceName) {
        return nullIdGenerator;
    }

    @Override
    public int getOrder() {
        return 200;
    }

    class NullIdGeneratorImpl implements NullIdGenerator {
        @Override
        public Number nextId() {
            return null;
        }
    }

}
