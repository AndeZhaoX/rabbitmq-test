package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *@Author zhaoxuan
 *@Data 2019/4/11 15:46
 *@ClassName Config
 *@Description TODO
 *@Version 1.0
 */
public class RabbitConfig {
    public static String host ;
    public static int port ;
    public static String username;
    public static String password ;

    static{
        try {
            ClassLoader classLoader=RabbitConfig.class.getClassLoader();
            InputStream is=classLoader.getResourceAsStream("rabbitmq.properties");
            Properties properties=new Properties();
            properties.load(is);
            host = properties.getProperty("host");
            port = Integer.parseInt(properties.getProperty("port"));
            username =properties.getProperty("username");
            password =properties.getProperty("password");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
