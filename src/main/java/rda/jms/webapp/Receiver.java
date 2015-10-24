package rda.jms.webapp;

import org.apache.log4j.Logger;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    public static final Logger logger = Logger.getLogger(Receiver.class);

    public static final String receiveMessage = "mailbox-destination";

    @JmsListener(destination = Receiver.receiveMessage)
    public void receiveMessage(String message) {
        logger.info("Received <" + message + ">");
    }

}