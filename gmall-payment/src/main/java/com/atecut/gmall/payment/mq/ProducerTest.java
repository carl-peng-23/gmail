package com.atecut.gmall.payment.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class ProducerTest {
    public static void main(String[] args) throws JMSException {
        /*
            1、创建连接工厂
            2、创建连接
            3、打开连接
            4、创建session
            5、创建队列
            6、创建消息提供者
            7、发送消息
            8、关闭
         */
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory("tcp://192.168.119.3:61616");
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        // 第一个参数，是否开启事务
        // 第二个参数，表示开启/关闭事务的相应配置参数
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("queueName");
        MessageProducer producer = session.createProducer(queue);
        ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();
        activeMQTextMessage.setText("Hello World!");
        producer.send(activeMQTextMessage);
        producer.close();
        session.close();
        connection.close();
    }
}
