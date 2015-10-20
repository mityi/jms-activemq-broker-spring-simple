package rda.jms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.util.FileSystemUtils;

import java.io.File;

@EnableAutoConfiguration
@Import(ConfigurationJms.class)
public class Boot {

    public static void main(String[] args) {
        // Clean out any ActiveMQ data from a previous run
        FileSystemUtils.deleteRecursively(new File("activemq-data"));

        SpringApplication.run(Boot.class, args);

    }

}