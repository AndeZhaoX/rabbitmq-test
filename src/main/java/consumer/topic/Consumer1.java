package consumer.topic;

import com.rabbitmq.client.*;
import utils.RabbitUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *@Author zhaoxuan
 *@Data 2019/4/11 13:21
 *@ClassName RabbitConsumer
 *@Description TODO
 *@Version 1.0
 */
public class Consumer1 {
    public static void main(String[] args) throws Exception {
        // 创建一个连接
        Connection conn = RabbitUtil.GetRabbitConnection();
        // 创建通道
        Channel channel = conn.createChannel();
        //声明exchange
        //exchange:exchange名称
        //type:exchange类型
        //durable:exchange是否持久化(不代表消息持久化)
        //autoDelete:已经没有消费者了,服务器是否可以删除该Exchange
        //exchangeDeclare(String exchange, String type, boolean durable, boolean autoDelete,Map<String, Object> arguments)
        channel.exchangeDeclare("zx_topic", BuiltinExchangeType.TOPIC,true,false,null);

        //声明queue
        //queue:queue名称
        //durable:queue是否持久化
        //exclusive:是否为当前连接的专用队列，在连接断开后，会自动删除该队列
        //autodelete：当没有任何消费者使用时，自动删除该队列
        //queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments)
        channel.queueDeclare("zx_queue_t1", true, false, false, null);

        //queue:queue名称
        //exchange:exchange名称
        //routingKey:路由键;用来绑定queue和exchange
        //queueBind(String queue, String exchange, String routingKey)
        channel.queueBind("zx_queue_t1","zx_topic","zx.*");

        //delegate.basicQos(prefetchSize, prefetchCount, global)
        channel.basicQos(200);

        //消息消费
        //queue:绑定队列名称
        //autoAck：是否自动ack，如果不自动ack，需要使用channel.ack、channel.nack、channel.basicReject 进行消息应答
        //basicConsume(String queue, boolean autoAck, Consumer callback)
        channel.basicConsume("zx_queue_t1",false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    String message = new String(body,"utf-8");
                    System.out.println(Consumer1.class.getSimpleName()+"--接收消息--"+message);
                } catch (UnsupportedEncodingException e) {

                    //deliveryTag:该消息的唯一ID
                    //multiple：是否批量. true:将一次性拒绝所有小于deliveryTag的消息。
                    //requeue：被拒绝的是否重新入队列
                    //channel.basicNack(long deliveryTag, boolean multiple, boolean requeue);
                    channel.basicNack(envelope.getDeliveryTag(),false,true);

                    //deliveryTag:该消息的index
                    //requeue：被拒绝的是否重新入队列
                    //channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
                    //channel.basicNack 与 channel.basicReject 的区别在于basicNack可以拒绝多条消息，而basicReject一次只能拒绝一条消息
                } finally {

                    //basicAck(long deliveryTag, boolean multiple)
                    //deliveryTag:该消息的唯一ID
                    //multiple：是否批量.true:将一次性ack所有小于deliveryTag的消息。
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        });
    }
}