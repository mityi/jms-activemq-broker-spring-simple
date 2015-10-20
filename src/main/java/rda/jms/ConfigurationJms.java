package rda.jms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.remoting.JmsInvokerProxyFactoryBean;

import javax.jms.ConnectionFactory;

@EnableJms
@Configuration
@ComponentScan
public class ConfigurationJms {

    // Strictly speaking this bean is not necessary as boot creates a default
    @Bean
    public JmsListenerContainerFactory<?> myJmsContainerFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

}
