package rda.jms.client;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.remoting.JmsInvokerProxyFactoryBean;
import org.springframework.util.FileSystemUtils;
import rda.jms.server.SimpleService;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import java.io.File;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@SpringBootApplication
@EnableJms
public class ClientBoot {
    public static final Logger logger = Logger.getLogger(ClientBoot.class);

    @Bean
    public JmsInvokerProxyFactoryBean jmsInvokerProxyFactoryBean() {
        JmsInvokerProxyFactoryBean producerProxy = new JmsInvokerProxyFactoryBean();
        producerProxy.setServiceInterface(SimpleService.class);
        producerProxy.setConnectionFactory(connectionFactory());
        producerProxy.setQueue(jmsQueue());
        return producerProxy;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory baseConnectionFactory =
                new ActiveMQConnectionFactory();
        baseConnectionFactory.setBrokerURL("tcp://localhost:61616");
        return baseConnectionFactory;
    }

    @Bean
    public Queue jmsQueue() {
        return new ActiveMQQueue("remoting");
    }

    @Bean
    public SimpleService simpleService(JmsInvokerProxyFactoryBean jmsInvokerProxyFactoryBean) {
        return (SimpleService) jmsInvokerProxyFactoryBean.getObject();
    }

    public static void main(String[] args) throws InterruptedException {
        // Clean out any ActiveMQ data from a previous run
        FileSystemUtils.deleteRecursively(new File("activemq-data"));

        ConfigurableApplicationContext context = SpringApplication.run(ClientBoot.class, args);
        SimpleService service = context.getBean("simpleService", SimpleService.class);

        logger.info("Start Client App");

        ExecutorCompletionService<String> executorService = new ExecutorCompletionService(ForkJoinPool.commonPool());
        executorService.submit(() -> {
            try {
                logger.info("try first");
                String message = service.hello("Rda-0");
                logger.info(message);
                message = service.hello("Rda-1");
                logger.info(message);
                message = service.hello("Rda-2");
                logger.info(message);
                return "complete";
            } catch (Exception e) {
                logger.error("error", e);
                return "problem";
            }
        });

//        executorService.poll()
        Future<String> take = executorService.take();
        context.stop();
    }

}