package org.mvnsearch.spring.boot.rsocket;

import io.rsocket.AbstractRSocket;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.client.LoadBalancedRSocketMono;
import io.rsocket.client.filter.RSocketSupplier;
import io.rsocket.uri.UriTransportRegistry;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RSocket requester auto configuration
 *
 * @author linux_china
 */
@Configuration
@EnableConfigurationProperties(RSocketProperties.class)
@ConditionalOnProperty("rsocket.brokers")
public class RSocketRequesterAutoConfiguration implements ApplicationContextAware {
    private Logger log = LoggerFactory.getLogger(RSocketRequesterAutoConfiguration.class);
    @Autowired
    private RSocketProperties properties;
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * create RSockets bean
     *
     * @return RSocket map
     */
    @Bean
    public Map<String, Mono<RSocket>> rsockets() {
        Map<String, Mono<RSocket>> rsockets = new HashMap<>();
        if (properties.getBrokers() != null && !properties.getBrokers().isEmpty()) {
            rsockets.put("broker", lbRSocket(properties.getBrokers()));
        }
        if (properties.getEndpoints() != null && !properties.getEndpoints().isEmpty()) {
            for (Map.Entry<String, List<String>> entry : properties.getEndpoints().entrySet()) {
                String serviceName = entry.getKey();
                List<String> endpoints = entry.getValue();
                rsockets.put(serviceName, lbRSocket(endpoints));
            }
        }
        return rsockets;
    }

    @PreDestroy
    public void destroy() {
        log.info("Destroy RSocket connections");
        Map<String, Mono<RSocket>> rsockets = (Map<String, Mono<RSocket>>) applicationContext.getBean("rsockets");
        for (Mono<RSocket> rSocket : rsockets.values()) {
            rSocket.subscribe(Disposable::dispose);
        }
    }


    /**
     * create load balance RSocket
     *
     * @param endpoints endpoints, such as tcp://xxx:42252
     * @return load balanced RSocket
     */
    private Mono<RSocket> lbRSocket(List<String> endpoints) {
        List<RSocketSupplier> suppliers = endpoints.stream().map(uri -> {
            return new RSocketSupplier(() -> Mono.just(rSocket(uri)));
        }).collect(Collectors.toList());
        Publisher<List<RSocketSupplier>> src =
                s -> {
                    s.onNext(suppliers);
                    s.onComplete();
                };
        return LoadBalancedRSocketMono.create(src);
    }

    /**
     * create Rsocket from uri
     *
     * @param uri RSocket uri
     * @return RSocket object
     */
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
            log.error("Failed create RSocket connection with " + uri, e);
            return new AbstractRSocket() {
                @Override
                public double availability() {
                    return 0.0;
                }
            };
        }
    }

}
