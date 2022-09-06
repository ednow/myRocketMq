import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 *  <a href="https://github.com/apache/rocketmq/blob/master/docs/cn/RocketMQ_Example.md">代码来源</a>
 */
public class Main {
    public static class SyncProducer {
        public static void main() throws Exception {
            // 实例化消息生产者Producer
            DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
            // 设置NameServer的地址
            producer.setNamesrvAddr("ednow.com:49159");
            // 启动Producer实例
            producer.start();
            for (int i = 0; i < 100; i++) {
                // 创建消息，并指定Topic，Tag和消息体
                Message msg = new Message("TopicTest" /* Topic */,
                        "TagA" /* Tag */,
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
                );
                // 发送消息到一个Broker
                SendResult sendResult = producer.send(msg);
                // 通过sendResult返回消息是否成功送达
                System.out.printf("%s%n", sendResult);
            }
            // 如果不再发送消息，关闭Producer实例。
            producer.shutdown();
        }
    }

    public static class Consumer {

        public static void main() throws InterruptedException, MQClientException {

            // 实例化消费者
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");

            // 设置NameServer的地址
            consumer.setNamesrvAddr("ednow.com:49159");

            // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
            consumer.subscribe("TopicTest", "*");
            // 注册回调实现类来处理从broker拉取回来的消息
            consumer.registerMessageListener(new MessageListenerConcurrently() {
//                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                    // 标记该消息已经被成功消费
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            // 启动消费者实例
            consumer.start();
            System.out.printf("Consumer Started.%n");
        }
    }

    public static void main(String[] args) throws Exception{
        System.out.println("1");
        SyncProducer.main();
        System.out.println("2");
        Consumer.main();

    }
}
