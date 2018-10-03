package org.mvnsearch.spring.boot.rsocket.demo;

import io.rsocket.RSocket;
import org.mvnsearch.spring.boot.rsocket.RSocketRemoteServiceBuilder;
import org.mvnsearch.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * RSocket requester app
 *
 * @author linux_china
 */
@SpringBootApplication
public class RSocketRequesterApp {

    public static void main(String[] args) {
        SpringApplication.run(RSocketRequesterApp.class, args);
    }

    @Bean
    public UserService userService(@Qualifier("rsockets") Map<String, Mono<RSocket>> rsockets) {
        return RSocketRemoteServiceBuilder
                .client(UserService.class)
                .endpoint("user-service")
                .rSocket(rsockets)
                .build();
    }
}
