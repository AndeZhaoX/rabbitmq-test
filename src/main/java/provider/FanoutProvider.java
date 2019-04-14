package provider;

import com.rabbitmq.client.*;
import utils.RabbitUtil;

/**
 *@Author zhaoxuan
 *@Data 2019/4/11 11:39
 *@ClassName RabbitSend
 *@Description TODO
 *@Version 1.0
 */
public class FanoutProvider {

    public static void main(String[] args) throws Exception {
        // 创建一个连接
        Connection connection = RabbitUtil.GetRabbitConnection();
        // 创建通道
        Channel channel = connection.createChannel();
        //声明exchange
        //exchange:exchange名称
        //type:exchange类型
        //durable:exchange是否持久化(不代表消息持久化)
        //autoDelete:已经没有消费者了,服务器是否可以删除该Exchange
        //exchangeDeclare(String exchange, String type, boolean durable, boolean autoDelete,Map<String, Object> arguments)
        channel.exchangeDeclare("zx_fanout", BuiltinExchangeType.FANOUT, true,false,null);

        //声明queue
        //queue:queue名称
        //durable:queue是否持久化
        //exclusive:是否为当前连接的专用队列，在连接断开后，会自动删除该队列
        //autodelete：当没有任何消费者使用时，自动删除该队列
        //queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments)
        channel.queueDeclare("zx_queue_f1", true, false, false, null);
        channel.queueDeclare("zx_queue_f2", true, false, false, null);
        channel.queueDeclare("zx_queue_f3", true, false, false, null);

        //queue:queue名称
        //exchange:exchange名称
        //routingKey:路由键;用来绑定queue和exchange
        //queueBind(String queue, String exchange, String routingKey)
        channel.queueBind("zx_queue_f1","zx_fanout","");
        channel.queueBind("zx_queue_f2","zx_fanout","");
        channel.queueBind("zx_queue_f3","zx_fanout","");

        //开启confirm机制
        // (即rabbitmq-server收到生产端的消息,会给生产端发送一个确认,如果没法送确认,生产端重新发送消息到server中)
        channel.confirmSelect();

        for (int i = 1 ; i <= 100 ;i++){
            String message = "fanout:广播的第 "+ i +" 条消息";

            //exchange
            //routingKey：路由键
            //mandatory：true=如果exchange根据自身类型和消息routeKey无法找到一个符合条件的queue，那么会调用basic.return方法将消息返还给生产者。false=出现上述情形broker会直接将消息扔掉
            //immediate：true=如果exchange在将消息route到queue(s)时发现对应的queue上没有消费者，那么这条消息不会放入队列中。false=当与消息routeKey关联的所有queue(一个或多个)都没有消费者时，该消息会通过basic.return方法返还给生产者。
            //BasicProperties：消息的基本属性，例如路由头等(MessageProperties.PERSISTENT_TEXT_PLAIN:消息的持久化)
            //basicPublish(String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body)
            channel.basicPublish("zx_fanout","",false,false,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("utf-8"));
            System.out.println(message);
        }

        // 关闭连接
        channel.close();
        connection.close();
    }
}
