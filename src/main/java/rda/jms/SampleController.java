package rda.jms;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Message;
import java.io.File;

@RestController
public class SampleController {

    public static final Logger logger = Logger.getLogger(SampleController.class);

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private JmsTemplate jmsTemplate;

    @RequestMapping("/send/{msg}")
    private String send(@PathVariable String msg) {
        jmsTemplate.convertAndSend(Receiver.receiveMessage, msg);
        return "ok";
    }


    @RequestMapping("/stop")
    private void stop() {
        logger.info("Stop application");
        context.close();
        FileSystemUtils.deleteRecursively(new File("activemq-data"));
    }
}
