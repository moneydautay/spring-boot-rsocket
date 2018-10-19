package org.mvnsearch.spring.boot.rsocket;

import com.google.common.base.Joiner;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.EventListener;
import java.util.List;

/**
 * load balance endpoints test
 *
 * @author linux_china
 */
public class LoadBalanceEndpointsTest {
    @Test
    public void testDynamicEndpointsPublisher() throws Exception {
        EventBus eventBus = new EventBus();
        final Flux<List<String>> endpointsFlux = Flux.create(sink -> {
            eventBus.register(new EventListener() {
                @Subscribe
                public void newEndpoints(List<String> event) {
                    sink.next(event);
                }
            });
        });
        Publisher<List<String>> publisher = new Publisher<List<String>>() {
            @Override
            public void subscribe(Subscriber<? super List<String>> subscriber) {
                endpointsFlux.subscribe(s -> {
                    subscriber.onNext(s);
                });
            }
        };
        publisher.subscribe(new Subscriber<List<String>>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(List<String> strings) {
                System.out.println(Joiner.on(',').join(strings));
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
        eventBus.post(Arrays.asList("first"));
        Thread.sleep(1000);
        eventBus.post(Arrays.asList("second"));
        Thread.sleep(1000);
        eventBus.post(Arrays.asList("third"));
        Thread.sleep(10000);

    }
}
