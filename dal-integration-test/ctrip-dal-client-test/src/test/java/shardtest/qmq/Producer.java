package shardtest.qmq;

import org.junit.Assert;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.MessageSendStateListener;
import qunar.tc.qmq.TransactionProvider;
import qunar.tc.qmq.dal.DalTransactionProvider;
import qunar.tc.qmq.producer.MessageProducerProvider;

public class Producer {
    private static final String topic = "fx.dal.integrationtest";
    private static final String keyname = "SimpleShardByDBTableOnMysql";

    public static class MyMessageSendStateListener implements MessageSendStateListener {
        public static MyMessageSendStateListener instance = new MyMessageSendStateListener();

        @Override
        public void onSuccess(Message message) {
            System.out.println("onSuccess:" + message.getMessageId());
            Assert.assertTrue(true);
        }

        // I change table name of qmq_msg_queue in my specified shard to make it go here,proving that qmq will find the
        // correct shard.
        @Override
        public void onFailed(Message message) {
            System.out.println("onFailed:" + message.getMessageId());
            Assert.fail();
        }
    }

    private static MessageProducerProvider provider = null;
    private static final Object OBJ = new Object();

    public Producer() throws Exception {
        initializeProvider();
    }

    private static void initializeProvider() {
        if (provider == null) {
            synchronized (OBJ) {
                if (provider == null) {
                    provider = new MessageProducerProvider();
                    provider.init();
                    TransactionProvider transactionProvider;

                    transactionProvider = new DalTransactionProvider(keyname);
                    provider.setTransactionProvider(transactionProvider);
                }
            }
        }
    }

    public void senMessage() throws Exception {
        Message message = provider.generateMessage(topic);
        provider.sendMessage(message, MyMessageSendStateListener.instance);
    }

}
