package rda.jms.server;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.remoting.JmsInvokerServiceExporter;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableJms
public class ServerBoot {
    public static final Logger logger = Logger.getLogger(ServerBoot.class);

    static CompletableFuture<Boolean> completableFuture = new CompletableFuture();
    @Bean
    public BrokerService  brokerService() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        return broker;
    }

    @Bean
    public AbstractMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDestination(jmsQueue());
        container.setMessageListener(jmsInvokerServiceExporter());
        container.setConcurrentConsumers(3);
        return container;
    }

    @Bean
    public JmsInvokerServiceExporter jmsInvokerServiceExporter() {
        JmsInvokerServiceExporter serviceExporter =
                new JmsInvokerServiceExporter();
        serviceExporter.setService(simpleService());
        serviceExporter.setServiceInterface(SimpleService.class);
        return serviceExporter;
    }

    @Bean
    public Queue jmsQueue() {
        return new ActiveMQQueue("remoting");
    }

    @Bean
    public SimpleService simpleService() {
        return new SimpleService() {
            AtomicInteger iteration = new AtomicInteger(0);

            @Override
            public String hello(String name) {
                int i = iteration.getAndIncrement();
                if (i == 2) {
                    completableFuture.complete(true);
                }
                logger.info(name + " call me");
                return "Hello " + name;
            }
        };
    }

    public static void main(String[] args) throws Exception {

        ConfigurableApplicationContext context = SpringApplication.run(ServerBoot.class, args);

        logger.info("Start Server App");

        completableFuture.runAfterBoth(completableFuture, () -> {
            logger.info("By - by");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException ignore) {
            }
            context.stop();
        });
    }

}