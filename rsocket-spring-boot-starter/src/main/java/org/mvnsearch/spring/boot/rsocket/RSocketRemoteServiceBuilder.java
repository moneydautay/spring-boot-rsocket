package org.mvnsearch.spring.boot.rsocket;

import io.rsocket.RSocket;
import reactor.core.publisher.Mono;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * RSocket remote service builder
 *
 * @author linux_china
 */
public class RSocketRemoteServiceBuilder {
    private String endpoint;
    private String service;
    private String version;
    private Class serviceInterface;
    private Mono<RSocket> rSocket;

    public static RSocketRemoteServiceBuilder client(Class serviceInterface) {
        RSocketRemoteServiceBuilder rSocketServiceBuilder = new RSocketRemoteServiceBuilder();
        rSocketServiceBuilder.serviceInterface = serviceInterface;
        rSocketServiceBuilder.service = serviceInterface.getCanonicalName();
        return rSocketServiceBuilder;
    }

    public RSocketRemoteServiceBuilder endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public RSocketRemoteServiceBuilder service(String service) {
        this.service = service;
        return this;
    }
    
    public RSocketRemoteServiceBuilder version(String version) {
        this.version = version;
        return this;
    }

    public RSocketRemoteServiceBuilder rSocket(Mono<RSocket> rSocket) {
        this.rSocket = rSocket;
        return this;
    }

    public RSocketRemoteServiceBuilder rSocket(Map<String, Mono<RSocket>> rSockets) {
        if (rSockets.containsKey(service)) {
            this.rSocket = rSockets.get(service);
        } else {
            this.rSocket = rSockets.get("broker");
        }
        return this;
    }

    public Object build() {
        return Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class[]{serviceInterface},
                new RSocketInvocationRequesterHandler(rSocket, "application/hessian"));
    }
}
