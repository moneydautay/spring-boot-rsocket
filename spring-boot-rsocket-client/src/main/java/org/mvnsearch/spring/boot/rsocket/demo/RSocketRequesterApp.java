package org.mvnsearch.spring.boot.rsocket.demo;

import io.rsocket.AbstractRSocket;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Bean()
    public Mono<RSocket> rSocket() {
        List<RSocketSupplier> suppliers = Stream.of("localhost:42252").map(url -> {
            String[] parts = url.split(":");
            return new RSocketSupplier(() -> Mono.just(rSocket(parts[0], Integer.valueOf(parts[1]))));
        }).collect(Collectors.toList());
        Publisher<List<RSocketSupplier>> src =
                s -> {
                    s.onNext(suppliers);
                    s.onComplete();
                };
        return LoadBalancedRSocketMono.create(src);
    }

    @Bean
    public UserService userService(Mono<RSocket> rSocket) {
        return (UserService) Proxy.newProxyInstance(
                UserService.class.getClassLoader(),
                new Class[]{UserService.class},
                new RSocketInvocationRequesterHandler(rSocket, "application/hessian"));
    }

    private RSocket rSocket(String host, int address) {
        try {
            return RSocketFactory
                    .connect()
                    .metadataMimeType("application/protobuf")
                    .dataMimeType("application/hessian")
                    .transport(TcpClientTransport.create(host, address))
                    .start()
                    .block();
        } catch (Exception e) {
            return new AbstractRSocket() {
                @Override
                public double availability() {
                    return 0.0;
                }
            };
        }
    }

}
