package org.mvnsearch.spring.boot.rsocket.broker.demo;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.client.LoadBalancedRSocketMono;
import io.rsocket.client.filter.RSocketSupplier;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * rsocket client test
 *
 * @author linux_china
 */
public class RSocketClientTest {

    @Test
    public void testCheck() throws Exception {
        Mono<RSocket> rSocket = lbRSocket();
        Mono<Payload> demo = rSocket.flatMap(rSocket1 -> {
            //return rSocket1.requestResponse(DefaultPayload.create("body", "org.mvnsearch.user.UserService"));
            return rSocket1.requestResponse(DefaultPayload.create("body", "user-service"));
        });
        demo.subscribe(payload -> {
            System.out.println(payload.getDataUtf8());
        });
        Thread.sleep(1000);
    }


    public Mono<RSocket> lbRSocket() {
        List<RSocketSupplier> suppliers = Stream.of("localhost:9999").map(url -> {
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

    public RSocket rSocket(String host, int address) {
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
