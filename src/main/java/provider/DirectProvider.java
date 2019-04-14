package provider;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import config.RabbitConfig;
import utils.RabbitUtil;

/**
 *@Author zhaoxuan
 *@Data 2019/4/11 11:39
 *@ClassName RabbitSend
 *@Description TODO
 *@Version 1.0
 */
public class DirectProvider {

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
        channel.exchangeDeclare("zx_direct", BuiltinExchangeType.DIRECT, true);

        //声明queue
        //queue:queue名称
        //durable:queue是否持久化
        //exclusive:是否为当前连接的专用队列，在连接断开后，会自动删除该队列
        //autodelete：当没有任何消费者使用时，自动删除该队列
        //queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete,Map<String, Object> arguments)
        channel.queueDeclare("zx_queue_d1", false, false, false, null);
        channel.queueDeclare("zx_queue_d2", false, false, false, null);
        channel.queueDeclare("zx_queue_d3", false, false, false, null);

        //queue:queue名称
        //exchange:exchange名称
        //routingKey:路由键;用来绑定queue和exchange
        //queueBind(String queue, String exchange, String routingKey)
        channel.queueBind("zx_queue_d1","zx_direct","zx_routkey1");
        channel.queueBind("zx_queue_d2","zx_direct","zx_routkey2");
        channel.queueBind("zx_queue_d3","zx_direct","zx_routkey3");

        //开启confirm机制
        channel.confirmSelect();

        for (int i = 1 ; i <= 100 ;i++){
            String message = "direct 直连的第 "+ i +" 条消息";

            //exchange
            //routingKey：路由键
            //mandatory：true=如果exchange根据自身类型和消息routeKey无法找到一个符合条件的queue，那么会调用basic.return方法将消息返还给生产者。false=出现上述情形broker会直接将消息扔掉
            //immediate：true=如果exchange在将消息route到queue(s)时发现对应的queue上没有消费者，那么这条消息不会放入队列中。false=当与消息routeKey关联的所有queue(一个或多个)都没有消费者时，该消息会通过basic.return方法返还给生产者。
            //BasicProperties：消息的基本属性，例如路由头等
            //basicPublish(String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body)
            channel.basicPublish("zx_direct","zx_routkey2",null,message.getBytes("utf-8"));
            System.out.println(message);
        }

        // 关闭连接
        channel.close();
        connection.close();
    }
}
