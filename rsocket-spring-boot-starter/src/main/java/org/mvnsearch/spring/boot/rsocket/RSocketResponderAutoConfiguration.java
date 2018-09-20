package org.mvnsearch.spring.boot.rsocket;

import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * RSocket Responder auto configuration
 *
 * @author linux_china
 */
@Configuration
@EnableConfigurationProperties(RSocketProperties.class)
@ConditionalOnBean(annotation = RSocketService.class)
public class RSocketResponderAutoConfiguration {
    private Logger log = LoggerFactory.getLogger(RSocketResponderAutoConfiguration.class);
    @Autowired
    RSocketProperties properties;

    @Bean
    public RSocketServiceAnnotationProcessor rSocketServiceAnnotationProcessor() {
        return new RSocketServiceAnnotationProcessor();
    }

    /**
     * rsocket responder, it's a Subscriber
     *
     * @param serviceCall service call
     * @return Subscriber
     */
    @Bean(destroyMethod = "dispose")
    public Disposable rsocketResponder(ReactiveServiceCaller serviceCall) {
        SocketAcceptor socketAcceptor = (setupPayload, reactiveSocket) -> Mono.just(new RSocketRequestResponderHandler(serviceCall, setupPayload));
        //@see LambdaMonoSubscriber
        Disposable subscriber = RSocketFactory
                .receive()
                .acceptor(socketAcceptor)
                .transport(TcpServerTransport.create("0.0.0.0", properties.getPort()))
                .start()
                .onTerminateDetach()
                .subscribe();
        log.info("RSocket started on port " + properties.getPort());
        return subscriber;
    }
}
