package qunar.tc.qmq.dal;

import org.junit.Test;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.MessageStore;
import qunar.tc.qmq.ProduceMessage;

public class DatabaseSetAdaptTest {

    @Test
    public void doTest() {
        DalMessageStore store = new DalMessageStore("SimpleShardByTableOnMySql");
        store.insert(new MockProduceMessage(), "test");
    }

    static class MockProduceMessage implements ProduceMessage {
        @Override
        public String getMessageId() {
            return "test" + System.currentTimeMillis();
        }

        @Override
        public String getSubject() {
            return null;
        }

        @Override
        public void save() {}

        @Override
        public void send() {}

        @Override
        public void error(Exception e) {}

        @Override
        public void failed() {}

        @Override
        public void block() {}

        @Override
        public void finish() {}

        @Override
        public void setStoreKey(Object o) {}

        @Override
        public Object getStoreKey() {
            return null;
        }

        @Override
        public void setDsIndex(String s) {}

        @Override
        public String getDsIndex() {
            return null;
        }

        @Override
        public Message getBase() {
            return null;
        }

        @Override
        public void setStore(MessageStore messageStore) {}

        /*@Override
        public void startSendTrace() {}*/

        @Override
        public int getTableShardingId() {
            return 0;
        }
    }

}
