package IDAutoGenerator;//package IDAutoGenerator;
//
//import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorFactory;
//import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
//
//public class MyIdGeneratorFactory implements IIdGeneratorFactory {
//    private IdGenerator myIdGenerator = new MyIdGeneratorFactory.MyIdGeneratorImpl();
//
//    public MyIdGeneratorFactory() {
//    }
//
//    public IdGenerator getIdGenerator(String sequenceName) {
//        return this.myIdGenerator;
//    }
//
//    public int getOrder() {
//        return 150;
//    }
//
//    class MyIdGeneratorImpl implements MyIdGenerator {
//        MyIdGeneratorImpl() {
//        }
//
//        public Number nextId() {
//            return 50;
//        }
//    }
//}
