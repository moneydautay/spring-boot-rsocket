package org.mvnsearch.spring.boot.rsocket.broker;

import io.rsocket.AbstractRSocket;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.client.LoadBalancedRSocketMono;
import io.rsocket.client.filter.RSocketSupplier;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * RSocket stream manager implementation
 *
 * @author linux_china
 */
public class RSocketUpstreamManagerImpl implements RSocketUpstreamManager {
    private Map<String, Mono<RSocket>> rsocketUpstreams = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Stream.of("com.alibaba.UserService").forEach(service -> {
            //get metadata, such auth information
            List<RSocketSupplier> suppliers = Stream.of("localhost:11111", "localhost:22222").map(url -> {
                String[] parts = url.split(":");
                return new RSocketSupplier(() -> Mono.just(rSocket(parts[0], Integer.valueOf(parts[1]))));
            }).collect(Collectors.toList());
            Publisher<List<RSocketSupplier>> src =
                    s -> {
                        s.onNext(suppliers);
                        s.onComplete();
                    };
            LoadBalancedRSocketMono loadBalancedRSocketMono = LoadBalancedRSocketMono.create(src);
            rsocketUpstreams.put(service, loadBalancedRSocketMono);
        });
    }


    @Override
    public Mono<RSocket> getRSocket(String serviceName) {
        return rsocketUpstreams.get(serviceName);
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
