package org.mvnsearch.spring.boot.rsocket.broker;

import io.rsocket.RSocket;
import reactor.core.publisher.Mono;

/**
 * RSocket upstream manager
 *
 * @author linux_china
 */
public interface RSocketUpstreamManager {

    public Mono<RSocket> getRSocket(String serviceName);

}
