package rda.jms;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class SimpleApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    public static final Logger logger = Logger.getLogger(SimpleApplicationListener.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent applicationEvent) {
        send();
    }

    private void send() {
//        // Send a message
//        MessageCreator messageCreator = new MessageCreator() {
//            @Override
//            public Message createMessage(Session session) throws JMSException {
//                return session.createTextMessage("ping!");
//            }
//        };
//        jmsTemplate.send("mailbox-destination", messageCreator);

        logger.info("Sending a new message.");
        jmsTemplate.convertAndSend("mailbox-destination", "Welcome to the family.");
    }

}
