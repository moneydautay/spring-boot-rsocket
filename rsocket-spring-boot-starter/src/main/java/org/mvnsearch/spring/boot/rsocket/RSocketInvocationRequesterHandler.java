package org.mvnsearch.spring.boot.rsocket;


import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import org.mvnsearch.rsocket.RSocketProtos;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mvnsearch.spring.boot.rsocket.HessianUtils.input;


/**
 * RSocket invocation requester handler for proxy interface
 *
 * @author linux_china
 */
public class RSocketInvocationRequesterHandler implements InvocationHandler {
    private Mono<RSocket> rSocket;
    private String dataType;
    private Map<Method, JavaMethodMetadata> methodMetadataMap = new ConcurrentHashMap<>();

    public RSocketInvocationRequesterHandler(Mono<RSocket> rSocket, String dataType) {
        this.rSocket = rSocket;
        this.dataType = dataType;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //performance, cache method
        if (!methodMetadataMap.containsKey(method)) {
            methodMetadataMap.put(method, new JavaMethodMetadata(method));
        }
        JavaMethodMetadata methodMetadata = methodMetadataMap.get(method);
        //payload metadata
        RSocketProtos.PayloadMetadata.Builder metaData = RSocketProtos.PayloadMetadata.newBuilder();
        metaData.setService(methodMetadata.getClassFullName());
        metaData.setMethod(methodMetadata.getName());
        metaData.setEncoding(methodMetadata.getEncoding());
        //metadata data content
        ByteBuffer metadataBuffer = ByteBuffer.wrap(metaData.build().toByteArray());
        //body content
        ByteBuffer bodyBuffer = methodMetadata.encodingBody(args);
        //----- return type deal------
        if (methodMetadata.isBiDirectional()) { //bi directional, channel
            Flux<Object> source = (Flux<Object>) args[0];
            Payload routePayload = DefaultPayload.create(bodyBuffer, metadataBuffer);
            //todo not finished yet
            Flux<Payload> newFlux = Flux.just(routePayload).mergeWith(source.map(t -> DefaultPayload.create(HessianUtils.output(t))));
            return rSocket.map(rs -> {
                return rs.requestChannel(newFlux);
            });
        } else {
            if (method.getReturnType().equals(Void.TYPE)) {
                rSocket.flatMap(rs -> {
                    return rs.fireAndForget(DefaultPayload.create(bodyBuffer, metadataBuffer));
                }).subscribe();
                return null;
            } else if (method.getReturnType().equals(Flux.class)) {
                Flux<Payload> flux = rSocket.flatMapMany(rs -> {
                    return rs.requestStream(DefaultPayload.create(bodyBuffer, metadataBuffer));
                });
                return flux.map(payload -> {
                    try {
                        return input(payload.getData());
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
            } else {
                Mono<Payload> payloadMono = rSocket.flatMap(rs -> {
                    return rs.requestResponse(DefaultPayload.create(bodyBuffer, metadataBuffer));
                });
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
