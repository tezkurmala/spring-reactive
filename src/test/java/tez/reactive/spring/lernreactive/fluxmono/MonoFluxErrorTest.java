package tez.reactive.spring.lernreactive.fluxmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class MonoFluxErrorTest {

    @Test
    public void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred!!")))
                .concatWith(Flux.just("D"))
                //Instead of stopping transmission when a error occurs, we handle it and lets the FLux continue emitting events
                .onErrorResume(e -> {
                   System.out.println("Exception being handled: " + e);
                   return Flux.just("default in place of exception");
                });
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                //.verify();
                .expectNext("default in place of exception")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingOnErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred!!")))
                .concatWith(Flux.just("D"))
                //Instead of stopping transmission when a error occurs, we mention default value and continue emitting events
                .onErrorReturn("default");
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                //.expectError(RuntimeException.class)
                //.verify();
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandlingOnErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred!!")))
                .concatWith(Flux.just("D"))
                //Wrapping exception into a custom exception
                .onErrorMap(e -> new CustomException(e));
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandlingOnErrorMapWithRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred!!")))
                .concatWith(Flux.just("D"))
                //Wrapping exception into a custom exception
                .onErrorMap(e -> new CustomException(e))
                //Retry twice from beginning - instead of giving up when an exception occurs
                .retry(2);
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandlingOnErrorMapWithRetryBackoff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred!!")))
                .concatWith(Flux.just("D"))
                //Wrapping exception into a custom exception
                .onErrorMap(e -> new CustomException(e))
                //Retry twice from beginning - instead of giving up when an exception occurs
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(5)));
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }
}
