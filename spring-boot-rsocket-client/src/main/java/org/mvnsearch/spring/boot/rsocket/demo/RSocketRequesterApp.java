package org.mvnsearch.spring.boot.rsocket.demo;

import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.client.LoadBalancedRSocketMono;
import io.rsocket.client.filter.RSocketSupplier;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.mvnsearch.spring.boot.rsocket.RSocketInvocationRequesterHandler;
import org.mvnsearch.user.UserService;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

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

    public Mono<RSocket> monoSocket() {
        Publisher<List<RSocketSupplier>> src =
                s -> {
                    s.onNext(Arrays.asList(new RSocketSupplier(
                            new Supplier<Mono<RSocket>>() {
                                @Override
                                public Mono<RSocket> get() {
                                    return Mono.just(connectSocket("localhost", 10000));
                                }
                            }
                    )));
                    s.onComplete();
                };
        return LoadBalancedRSocketMono.create(src);
    }

    @Bean(destroyMethod = "dispose")
    public RSocket rSocket() {
        return RSocketFactory
                .connect()
                .metadataMimeType("application/protobuf")
                .dataMimeType("application/hessian")
                .transport(TcpClientTransport.create("localhost", 42252))
                .start()
                .block();
    }

    @Bean
    public UserService userService(RSocket rSocket) {
        return (UserService) Proxy.newProxyInstance(
                UserService.class.getClassLoader(),
                new Class[]{UserService.class},
                new RSocketInvocationRequesterHandler(rSocket));
    }

    /**
     * create RSocket based on address and port
     *
     * @param address address
     * @param port    port
     * @return RSocket
     */
    private RSocket connectSocket(String address, int port) {
        return RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(address, port))
                .start()
                .block();
    }
}
