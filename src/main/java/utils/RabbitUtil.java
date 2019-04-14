package utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import config.RabbitConfig;

/**
 *@Author zhaoxuan
 *@Data 2019/4/11 16:03
 *@ClassName RabbitUtil
 *@Description TODO
 *@Version 1.0
 */
public class RabbitUtil {
    public static Connection GetRabbitConnection() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RabbitConfig.host);
        factory.setPort(RabbitConfig.port);
        factory.setUsername(RabbitConfig.username);
        factory.setPassword(RabbitConfig.password);
        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}

