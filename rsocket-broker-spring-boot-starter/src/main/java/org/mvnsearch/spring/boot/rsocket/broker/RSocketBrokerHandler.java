package org.mvnsearch.spring.boot.rsocket.broker;

import io.rsocket.AbstractRSocket;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.Payload;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * RSocket broker handler
 *
 * @author linux_china
 */
@SuppressWarnings("Duplicates")
public class RSocketBrokerHandler extends AbstractRSocket {
    private String metadataMimeType;
    private String dataMimeType;
    private RSocketUpstreamManager upstreamManager;

    public RSocketBrokerHandler(ConnectionSetupPayload setupPayload, RSocketUpstreamManager upstreamManager) {
        this.metadataMimeType = setupPayload.metadataMimeType();
        this.dataMimeType = setupPayload.dataMimeType();
        this.upstreamManager = upstreamManager;
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {
        String metadataUtf8 = payload.getMetadataUtf8();
        //todo decoding metadata for route
        return upstreamManager.getRSocket(metadataUtf8).flatMap(rSocket -> rSocket.requestResponse(payload));
    }

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        String metadataUtf8 = payload.getMetadataUtf8();
        return upstreamManager.getRSocket(metadataUtf8).flatMap(rSocket -> rSocket.fireAndForget(payload));
    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        String metadataUtf8 = payload.getMetadataUtf8();
        return upstreamManager.getRSocket(metadataUtf8).flatMapMany(rSocket -> rSocket.requestStream(payload));

    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
        return Flux.error(new UnsupportedOperationException("Request-Channel not implemented."));
    }

    @Override
    public Mono<Void> metadataPush(Payload payload) {
        String metadataUtf8 = payload.getMetadataUtf8();
        return upstreamManager.getRSocket(metadataUtf8).flatMap(rSocket -> rSocket.metadataPush(payload));
    }
}
