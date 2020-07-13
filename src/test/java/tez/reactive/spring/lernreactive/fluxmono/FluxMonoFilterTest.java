package tez.reactive.spring.lernreactive.fluxmono;

import com.sun.jdi.request.StepRequest;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxMonoFilterTest {
    List<String> names = Arrays.asList("Tez", "Spring", "Learn", "TReactive");
    @Test
    public void filterTest(){
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.startsWith("T"))
                .log(); //Only Tez and TReactive are transmitted
        StepVerifier.create(namesFlux)
                .expectNext("Tez", "TReactive")
                .verifyComplete();
    }
}
