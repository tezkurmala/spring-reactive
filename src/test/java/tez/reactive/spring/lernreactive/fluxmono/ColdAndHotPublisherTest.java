package tez.reactive.spring.lernreactive.fluxmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {
    //cold publisher will start emitting from beginning for each subscriber
    //hot publisher is like a one time stream. live feed for subscriber bit not from beginning. But all subscribers get the live feed.
    @Test
    public void coldPublisher() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));
        stringFlux.subscribe(s -> System.out.println("Subscriber 1: " + s));
        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("Subscriber 2: " + s));
        Thread.sleep(4000);
    }

    @Test
    public void hotPublisher() throws InterruptedException {
        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));
        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();
        
        connectableFlux.subscribe(s -> System.out.println("Subscriber 1: " + s));
        Thread.sleep(3000);

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2: " + s));
        Thread.sleep(4000);
    }

}
