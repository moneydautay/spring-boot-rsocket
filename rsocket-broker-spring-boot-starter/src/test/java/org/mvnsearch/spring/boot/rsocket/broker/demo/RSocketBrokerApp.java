package org.mvnsearch.spring.boot.rsocket.broker.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RSocket broker app
 *
 * @author linux_china
 */
@SpringBootApplication
public class RSocketBrokerApp {
    public static void main(String[] args) {
        SpringApplication.run(RSocketBrokerApp.class, args);
    }
}
