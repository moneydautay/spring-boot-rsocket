package org.mvnsearch.spring.boot.rsocket.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * RSocket App Server
 *
 * @author linux_china
 */
@SpringBootApplication
public class RSocketAppServer {
    public static void main(String[] args) {
        SpringApplication.run(RSocketAppServer.class, args);
    }
}
