package org.mvnsearch.spring.boot.rsocket.demo;

import io.rsocket.RSocket;
import org.mvnsearch.user.User;
import org.mvnsearch.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * portal demo for test
 *
 * @author linux_china
 */
@RestController
public class PortalController {
    @Autowired
    UserService userService;

    @RequestMapping("/user")
    public Mono<User> user() {
        userService.flush("demo");
        return userService.findById(1);
    }

    @RequestMapping("/appName")
    public Mono<String> appName() {
        userService.job1().doOnSuccess(s -> {
        }).subscribe();
        return userService.getAppName();
    }

    @RequestMapping("/flux")
    public Mono<String> flux() {
        userService.findAllPeople("vip").subscribe(t -> {
            System.out.println(t.getNick());
        });
        return Mono.just("good");
    }


    @RequestMapping("/")
    public String index() {
        return "good";
    }
}
