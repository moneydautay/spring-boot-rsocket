package org.mvnsearch.user;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * user service
 *
 * @author linux_china
 */
public interface UserService {

    /**
     * RPC call to get user
     *
     * @param id user
     * @return user
     */
    Mono<User> findById(Integer id);

    /**
     * RPC call without parameters
     *
     * @return result
     */
    Mono<String> getAppName();


    /**
     * rpc call, you want to deal success result: result.doOnSuccess(s -> { }).subscribe();
     *
     * @return Mono void
     */
    Mono<Void> job1();

    /**
     * fire & forgot operation
     *
     * @param name name
     */
    void flush(String name);

    /**
     * request/stream to get people by type
     *
     * @param type type
     * @return user stream
     */
    Flux<User> findAllPeople(String type);

    /**
     * channel(bi-direction stream)
     *
     * @param point point
     * @return user
     */
    Flux<User> recent(Flux<Date> point);

}
