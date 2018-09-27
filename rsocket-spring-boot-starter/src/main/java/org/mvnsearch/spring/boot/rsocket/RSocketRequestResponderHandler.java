package org.mvnsearch.spring.boot.rsocket;

import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import io.rsocket.exceptions.InvalidException;
import io.rsocket.util.DefaultPayload;
import org.mvnsearch.rsocket.RSocketProtos;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * RSocket request responder handler
 *
 * @author linux_china
 */
@SuppressWarnings("Duplicates")
public class RSocketRequestResponderHandler extends AbstractRSocket {
    private ReactiveServiceCaller serviceCall;
    private String metadataMimeType;
    private String dataMimeType;

    public RSocketRequestResponderHandler(ReactiveServiceCaller serviceCall, ConnectionSetupPayload setupPayload) {
        this.serviceCall = serviceCall;
        this.metadataMimeType = setupPayload.metadataMimeType();
        this.dataMimeType = setupPayload.dataMimeType();
        //get metadata, such auth information
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        try {
            RSocketProtos.PayloadMetadata metaData = RSocketProtos.PayloadMetadata.parseFrom(payload.getMetadata());
            Object args = decodingData(metaData.getEncoding(), payload);
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
            Object args = decodingData(metaData.getEncoding(), payload);
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
            Object args = decodingData(metaData.getEncoding(), payload);
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
        Mono<Payload> routePayload = Mono.from(payloads);
        return routePayload
                .map(payload -> {
                    try {
                        return RSocketProtos.PayloadMetadata.parseFrom(payload.getMetadata());
                    } catch (Exception e) {
                        return null;
                    }
                }).flatMapMany(metaData -> {
                    //todo deal with metadata route information
                    try {
                        Flux<Payload> param = Flux.from(payloads)
                                .skip(1)
                                .map(o -> DefaultPayload.create(HessianUtils.output(o)));
                        Object result = serviceCall.invoke(metaData.getService(), metaData.getRpc(), param);
                        return ((Flux<Object>) result).map(o -> DefaultPayload.create(HessianUtils.output(o)));

                    } catch (Exception e) {
                        return Flux.error(new InvalidException(e.getMessage()));
                    }
                });
    }


    @Override
    public Mono<Void> metadataPush(Payload payload) {
        return super.metadataPush(payload);
    }

    private Object decodingData(RSocketProtos.PayloadMetadata.Encoding encoding, Payload payload) throws Exception {
        if (encoding.equals(RSocketProtos.PayloadMetadata.Encoding.VOID)) {
            return null;
        } else if (encoding.equals(RSocketProtos.PayloadMetadata.Encoding.INT)) {
            return payload.getData().getInt();
        } else if (encoding.equals(RSocketProtos.PayloadMetadata.Encoding.LONG)) {
            return payload.getData().getInt();
        } else if (encoding.equals(RSocketProtos.PayloadMetadata.Encoding.STRING)) {
            return payload.getDataUtf8();
        }
        return HessianUtils.input(payload.getData());
    }

}
