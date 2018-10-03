package org.mvnsearch.spring.boot.rsocket.broker;

import io.rsocket.AbstractRSocket;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.client.LoadBalancedRSocketMono;
import io.rsocket.client.filter.RSocketSupplier;
import io.rsocket.uri.UriTransportRegistry;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * RSocket stream manager implementation, support by endpoint & service name
 *
 * @author linux_china
 */
public class RSocketUpstreamManagerImpl implements RSocketUpstreamManager {
    private Logger log = LoggerFactory.getLogger(RSocketUpstreamManagerImpl.class);
    private RSocketBrokerProperties brokerProperties;
    private List<LoadBalancedRSocketMono> rSockets = new ArrayList<>();
    private Map<String, LoadBalancedRSocketMono> rsocketUpstreams = new ConcurrentHashMap<>();

    public RSocketUpstreamManagerImpl(RSocketBrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
    }

    @PostConstruct
    public void init() {
        List<RSocketRoute> routes = brokerProperties.getRoutes();
        for (RSocketRoute route : routes) {
            List<RSocketSupplier> suppliers = route.getUris().stream().map(uri -> new RSocketSupplier(() -> Mono.just(rSocket(uri)))).collect(Collectors.toList());
            Publisher<List<RSocketSupplier>> src =
                    s -> {
                        s.onNext(suppliers);
                        s.onComplete();
                    };
            LoadBalancedRSocketMono loadBalancedRSocketMono = LoadBalancedRSocketMono.create(src);
            rSockets.add(loadBalancedRSocketMono);
            if (route.getEndpoint() != null && !route.getEndpoint().isEmpty()) {
                rsocketUpstreams.put(route.getEndpoint(), loadBalancedRSocketMono);
            }
            if (route.getServices() != null && !route.getServices().isEmpty()) {
                for (String service : route.getServices()) {
                    rsocketUpstreams.put(service, loadBalancedRSocketMono);
                }
            }
        }
    }

    @PreDestroy
    public void destroy() {
        for (LoadBalancedRSocketMono rSocket : rSockets) {
            try {
                rSocket.dispose();
            } catch (Exception ignore) {

            }
        }
    }

    @Override
    public Mono<RSocket> getRSocket(String serviceName) {
        return rsocketUpstreams.get(serviceName);
    }


    private RSocket rSocket(String uri) {
        try {
            return RSocketFactory
                    .connect()
                    .metadataMimeType("application/protobuf")
                    .dataMimeType("application/hessian")
                    .transport(UriTransportRegistry.clientForUri(uri))
                    .start()
                    .block();
        } catch (Exception e) {
            log.error("Failed to create RSocket Connection: " + uri, e);
            return new AbstractRSocket() {
                @Override
                public double availability() {
                    return 0.0;
                }
            };
        }
    }


}
