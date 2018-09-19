package org.mvnsearch.spring.boot.rsocket.demo;


import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import org.mvnsearch.spring.boot.rsocket.HessianUtils;
import org.mvnsearch.spring.boot.rsocket.InvocationMetaData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.mvnsearch.spring.boot.rsocket.HessianUtils.input;


/**
 * RSocket invocation handler for proxy interface
 *
 * @author linux_china
 */
public class RSocketInvocationHandler implements InvocationHandler {
    private RSocket rSocket;

    public RSocketInvocationHandler(RSocket rSocket) {
        this.rSocket = rSocket;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InvocationMetaData metaData = new InvocationMetaData(method.getDeclaringClass().getCanonicalName(), method.getName());
        if (method.getParameterCount() > 0 && method.getParameterTypes()[0].equals(Flux.class)) {
            Flux<Object> source = (Flux<Object>) args[0];
            Payload routePayload = DefaultPayload.create(new byte[]{0}, HessianUtils.output(metaData));
            Flux<Payload> newFlux = Flux.just(routePayload).mergeWith(source.map(t -> DefaultPayload.create(HessianUtils.output(t))));
            return rSocket.requestChannel(newFlux);
        } else {
            byte[] content = HessianUtils.output(args);
            if (method.getReturnType().equals(Void.TYPE)) {
                rSocket.fireAndForget(DefaultPayload.create(content, HessianUtils.output(metaData)))
                        .doOnSuccess(s -> {
                        })
                        .subscribe();
                return null;
            } else if (method.getReturnType().equals(Flux.class)) {
                Flux<Payload> flux = rSocket.requestStream(DefaultPayload.create(content, HessianUtils.output(metaData)));
                return flux.map(payload -> {
                    try {
                        return input(payload.getData());
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
            } else {
                Mono<Payload> payloadMono = rSocket.requestResponse(DefaultPayload.create(content, HessianUtils.output(metaData)));
                return payloadMono.map(payload -> {
                    try {
                        return input(payload.getData());
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
            }
        }

    }

}
