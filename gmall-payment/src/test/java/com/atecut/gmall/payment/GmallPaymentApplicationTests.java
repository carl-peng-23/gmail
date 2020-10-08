package com.atecut.gmall.payment;

import com.atecut.gmall.config.ActiveMQUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPaymentApplicationTests {

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Test
    public void contextLoads() {
    }

    @Test
    public void mqTest() throws JMSException {
        Connection connection = activeMQUtil.getConnection();
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
    }

}
