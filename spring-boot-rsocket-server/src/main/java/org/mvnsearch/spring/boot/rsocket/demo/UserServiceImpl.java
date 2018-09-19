package org.mvnsearch.spring.boot.rsocket.demo;

import org.mvnsearch.spring.boot.rsocket.RSocketService;
import org.mvnsearch.user.User;
import org.mvnsearch.user.UserService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

/**
 * user service implementation
 *
 * @author linux_china
 */
@RSocketService(serviceInterface = UserService.class)
public class UserServiceImpl implements UserService {
    @Override
    public Mono<User> findById(Integer id) {
        return Mono.just(new User(1, "nick:" + id));
    }

    @Override
    public void flush(String name) {
        System.out.println("flush");
    }

    @Override
    public Mono<String> getAppName() {
        return Mono.just("UserService");
    }

    @Override
    public Mono<Void> job1() {
        System.out.println("job1");
        return Mono.empty();
    }

    @Override
    public Flux<User> findAllPeople(String type) {
        return Flux.interval(Duration.ofMillis(1000))
                .map(timestamp -> new User((int) (timestamp % 1000), "nick:" + type));
    }

    @Override
    public Flux<User> recent(Flux<Date> point) {
        point.subscribe(t -> {
            System.out.println("time:" + point);
        });
        return Flux.interval(Duration.ofMillis(1000))
                .map(timestamp -> new User((int) (timestamp % 1000), "nick"));
    }
}
