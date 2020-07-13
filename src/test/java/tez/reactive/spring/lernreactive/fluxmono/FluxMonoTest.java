package tez.reactive.spring.lernreactive.fluxmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxMonoTest {
    @Test
    public void fluxTest(){
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                //Adding an error into the flux stream during 4th result transmission
                .concatWith(Flux.error(new RuntimeException("Exception occurred during transmitting flux")))
                //To check if flux emits event after an error event. Nope. FLux does not transmit after error.
                .concatWith(Flux.just("After Error"))
                //Logging the Flux transmission to understand if 'onNext' multiple times
                // and 'onError' are really being invoked
                .log();

        stringFlux.subscribe(System.out::println,
                            (e) -> System.err.println("Exception is " + e),
                            //Following is run only when Flux successfully finishes transmitting all data
                            //Not if an exception occurs during transmission
                            () -> System.out.println("Completed"));


    }

    @Test
    public void fluxTestWithoutErrors(){
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                //To fail the order of transmission
                //.expectNext("Spring")
                .expectNext("Reactive Spring")
                //All data checks of events in single line
                //.expectNext("Spring", "Spring Boot", "Reactive Spring")
                //This is the one that acts like a subscribe and makes flux starts emitting events
                //verify does subscribe and verifyComplete does bth subscribe and would check completion event
                //.verify();
                .verifyComplete();
    }

    @Test
    public void fluxTestWithErrors(){
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred during transmitting flux")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Exception occurred during transmitting flux")
                .verify();
    }

    @Test
    public void fluxTestElementsCountWithErrors(){
        Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred during transmitting flux")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception occurred during transmitting flux")
                .verify();
    }

    @Test
    public void monoTest(){
        Mono<String> stringMono = Mono.just("Spring")
                .log();
        StepVerifier.create(stringMono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTestWithError(){
        StepVerifier.create(Flux.error(new RuntimeException("Exception occurred during transmitting flux")).log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
