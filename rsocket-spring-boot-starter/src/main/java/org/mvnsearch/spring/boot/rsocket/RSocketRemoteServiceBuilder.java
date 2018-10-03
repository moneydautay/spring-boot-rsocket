package org.mvnsearch.spring.boot.rsocket;

import io.rsocket.RSocket;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * RSocket remote service builder
 *
 * @author linux_china
 */
public class RSocketRemoteServiceBuilder<T> {
    @Nullable
    private String endpoint;
    private String service;
    private String version;
    private Class<T> serviceInterface;
    private Mono<RSocket> rSocket;

    public static <T> RSocketRemoteServiceBuilder<T> client(Class<T> serviceInterface) {
        RSocketRemoteServiceBuilder<T> rSocketServiceBuilder = new RSocketRemoteServiceBuilder<T>();
        rSocketServiceBuilder.serviceInterface = serviceInterface;
        rSocketServiceBuilder.service = serviceInterface.getCanonicalName();
        return rSocketServiceBuilder;
    }

    public RSocketRemoteServiceBuilder<T> endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public RSocketRemoteServiceBuilder<T> service(String service) {
        this.service = service;
        return this;
    }

    public RSocketRemoteServiceBuilder<T> version(String version) {
        this.version = version;
        return this;
    }

    public RSocketRemoteServiceBuilder<T> rSocket(Mono<RSocket> rSocket) {
        this.rSocket = rSocket;
        return this;
    }

    public RSocketRemoteServiceBuilder<T> rSocket(Map<String, Mono<RSocket>> rSockets) {
        if (rSockets.containsKey(service)) {
            this.rSocket = rSockets.get(service);
        } else {
            this.rSocket = rSockets.get("broker");
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public T build() {
        return (T) Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class[]{serviceInterface},
                new RSocketInvocationRequesterHandler(rSocket, "application/hessian", endpoint, serviceInterface, service, version));
    }
}
