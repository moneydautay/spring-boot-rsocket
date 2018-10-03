package org.mvnsearch.spring.boot.rsocket.broker;

import io.rsocket.RSocketFactory;
import io.rsocket.SocketAcceptor;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * RSocket broker auto configuration
 *
 * @author linux_china
 */
@Configuration
@EnableConfigurationProperties(RSocketBrokerProperties.class)
public class RSocketBrokerAutoConfiguration {

    @Bean
    public RSocketUpstreamManagerImpl rSocketUpstreamManager(RSocketBrokerProperties brokerProperties) {
        return new RSocketUpstreamManagerImpl(brokerProperties);
    }

    /**
     * rsocket responder, it's a Subscriber
     *
     * @return Subscriber
     */
    @Bean(destroyMethod = "dispose")
    public Disposable rsocketResponder(RSocketUpstreamManagerImpl upstreamManager) {
        SocketAcceptor socketAcceptor = (setupPayload, reactiveSocket) -> Mono.just(new RSocketBrokerHandler(setupPayload, upstreamManager));
        //@see LambdaMonoSubscriber
        Disposable subscriber = RSocketFactory
                .receive()
                .acceptor(socketAcceptor)
                .transport(TcpServerTransport.create("0.0.0.0", 9999))
                .start()
                .subscribe();
        System.out.println("Proxy started at 9999");
        return subscriber;
    }

}
