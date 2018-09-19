package org.mvnsearch.spring.boot.rsocket.demo;

import org.mvnsearch.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * subscribe runner, request/stream and channel etc
 *
 * @author linux_china
 */
@Component
public class SubscribeRunner implements ApplicationRunner {
    @Autowired
    UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /*  rSocket
                        .requestStream(DefaultPayload.create("Hello"))
                        .map(Payload::getDataUtf8)
                        .doFinally(signal -> rSocket.dispose())
                        .subscribe(name -> System.out.println("consuming " + name + "."));*/
        userService.findAllPeople("vip").subscribe(t -> {
            System.out.println(t.getNick());
        });
        userService.findAllPeople("loser").subscribe(t -> {
            System.out.println(t.getNick());
        });
    }
}
