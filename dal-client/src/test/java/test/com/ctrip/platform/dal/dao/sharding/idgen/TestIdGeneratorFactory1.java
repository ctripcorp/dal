package test.com.ctrip.platform.dal.dao.sharding.idgen;

import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorFactory;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import com.ctrip.platform.dal.sharding.idgen.NullIdGenerator;

public class TestIdGeneratorFactory1 implements IIdGeneratorFactory {

    public IdGenerator getIdGenerator(String sequenceName) {
        return new TestNullIdGenerator();
    }

    class TestNullIdGenerator implements NullIdGenerator {
        @Override
        public Number nextId() {
            return null;
        }
    }

    public int getOrder() {
        return -1;
    }

}
