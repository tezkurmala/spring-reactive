package tez.reactive.spring.lernreactive.fluxmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxMonoTimeTest {
    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)).log(); // starts from 0 till infinite - emits every 200ms
        infiniteFlux.subscribe(element -> System.out.println(element));
        //Without following sleep, the program will terminate as it is asynchronous behaviour
        Thread.sleep(3000);
    }

    @Test
    public void infiniteSequenceTest() throws InterruptedException {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200)) // starts from 0 till 2 - emits every 200ms
                .take(3) //take only 3 value into flux and emit
                .log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap() throws InterruptedException {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200)) // starts from 0 till 2 - emits every 200ms
                //Extra delay
                .delayElements(Duration.ofSeconds(1))
                .map(l -> l.intValue())
                .take(3) //take only 3 value into flux and emit
                .log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }

}
