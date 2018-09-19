package org.mvnsearch.spring.boot.rsocket;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.exceptions.InvalidException;
import io.rsocket.util.DefaultPayload;
import org.mvnsearch.rsocket.RSocketProtos;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

/**
 * RSocket request responder handler
 *
 * @author linux_china
 */
@SuppressWarnings("Duplicates")
public class RSocketRequestResponderHandler extends AbstractRSocket {
    @Autowired
    private ReactiveServiceCaller serviceCall;

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        try {
            RSocketProtos.PayloadMetadata metaData = RSocketProtos.PayloadMetadata.parseFrom(payload.getMetadata());
            Object[] args = (Object[]) decodingData(metaData.getEncoding(), payload.getData());
            Object result = serviceCall.invoke(metaData.getService(), metaData.getRpc(), args);
            if (result instanceof Mono) {
                return ((Mono<Object>) result).map(o -> DefaultPayload.create(HessianUtils.output(o)));
            } else {
                return Mono.just(DefaultPayload.create(HessianUtils.output(result)));
            }
        } catch (Exception e) {
            return Mono.error(new InvalidException(e.getMessage()));
        }
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        try {
            RSocketProtos.PayloadMetadata metaData = RSocketProtos.PayloadMetadata.parseFrom(payload.getMetadata());
            Object[] args = (Object[]) decodingData(metaData.getEncoding(), payload.getData());
            serviceCall.invoke(metaData.getService(), metaData.getRpc(), args);
            return Mono.empty();
        } catch (Exception e) {
            return Mono.error(new InvalidException(e.getMessage()));
        }
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        try {
            RSocketProtos.PayloadMetadata metaData = RSocketProtos.PayloadMetadata.parseFrom(payload.getMetadata());
            Object[] args = (Object[]) decodingData(metaData.getEncoding(), payload.getData());
            Object result = serviceCall.invoke(metaData.getService(), metaData.getRpc(), args);
            if (result instanceof Flux) {
                return ((Flux<Object>) result).map(o -> DefaultPayload.create(HessianUtils.output(o)));
            } else {
                return Flux.just(DefaultPayload.create(HessianUtils.output(result)));
            }
        } catch (Exception e) {
            return Flux.error(new InvalidException(e.getMessage()));
        }
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        //SwitchTransform, first payload is route & auth information
        //after validate first payload, then deal with real flux
        return null;
    }


    @Override
    public Mono<Void> metadataPush(Payload payload) {
        return super.metadataPush(payload);
    }

    private Object decodingData(RSocketProtos.PayloadMetadata.Encoding encoding, ByteBuffer buffer) throws Exception {
        return HessianUtils.input(buffer);
    }

}