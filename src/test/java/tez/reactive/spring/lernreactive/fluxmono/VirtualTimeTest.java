package tez.reactive.spring.lernreactive.fluxmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {
    @Test
    public void withoutVirtualTime() {
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .log();

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }
    @Test
    public void withVirtualTime() {
        //Virtual clock ticking to make sure that tests do not wait so long in real world
        //so reduced build time
        VirtualTimeScheduler.getOrSet();
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3)
                .log();

        StepVerifier.withVirtualTime(()-> longFlux)
                .expectSubscription()
                //This is required to wait till virtual time meets 3 seconds
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }
}
