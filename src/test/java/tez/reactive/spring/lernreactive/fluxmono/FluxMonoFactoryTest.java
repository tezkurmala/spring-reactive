package tez.reactive.spring.lernreactive.fluxmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxMonoFactoryTest {

    List<String> names = Arrays.asList("Tez", "Spring", "Learn", "Reactive");

    @Test
    public void fluxUsingIterable(){
        Flux<String> namesFlux = Flux.fromIterable(names)
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("Tez", "Spring", "Learn", "Reactive")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray(){
        String[] names = new String[]{"Tez", "Spring", "Learn", "Reactive"};
        Flux<String> namesFlux = Flux.fromArray(names)
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("Tez", "Spring", "Learn", "Reactive")
                .verifyComplete();
    }

    @Test
    public void fluxUsingStream(){
        Flux<String> namesFlux = Flux.fromStream(names.stream())
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("Tez", "Spring", "Learn", "Reactive")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty(){
        Mono<String> mono = Mono.justOrEmpty(null); //Mono.Empty()
        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void monoUsingSupplier(){
        Supplier<String> stringSupplier = () -> "Tez";
        Mono<String> mono = Mono.fromSupplier(stringSupplier);
        System.out.println(stringSupplier.get());
        StepVerifier.create(mono.log())
                .expectNext("Tez")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange(){
        Flux<Integer> integerFlux = Flux.range(10, 5);
        StepVerifier.create(integerFlux.log())
                .expectNextCount(5)
                .verifyComplete();
    }
}
