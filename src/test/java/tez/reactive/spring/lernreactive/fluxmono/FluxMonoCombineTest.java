package tez.reactive.spring.lernreactive.fluxmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxMonoCombineTest {

    @Test
    public void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(flux1, flux2).log();

        StepVerifier.create(mergedFlux)
                .expectSubscription()//To verify the consumer received the Subscription
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
     }

    @Test
    public void combineUsingMergeWithDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1)); //Flux emits each element with 1 sec delay
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1)); //Flux emits each element with 1 sec delay

        Flux<String> mergedFlux = Flux.merge(flux1, flux2).log(); //Flux that consumes from both Flux1 and FLux2 whoever emits

        StepVerifier.create(mergedFlux)
                .expectSubscription()//To verify the consumer received the Subscription
                .expectNextCount(6)
                //Following is commented because the merged flux is not wait for first flux1 that is delaying by sec
                //The merged flux just stream event from any flux that is emitting in an unknown order
                //.expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> concatenatedFlux = Flux.concat(flux1, flux2).log();

        StepVerifier.create(concatenatedFlux)
                .expectSubscription()//To verify the consumer received the Subscription
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcatWithDelay() {
        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1)); //Flux emits each element with 1 sec delay
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1)); //Flux emits each element with 1 sec delay
        //Ordered sequential One flux is consumer after the other. Not by interleaving as merge does.
        Flux<String> concatenatedFlux = Flux.concat(flux1, flux2).log(); //Flux that consumes from Flux1 and then from Flux2

        StepVerifier.create(concatenatedFlux)
                .expectSubscription()//To verify the consumer received the Subscription
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingZip() {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");
        //One element from each flux is taken and processed together
        Flux<String> mergedFlux = Flux.zip(flux1, flux2, (t1, t2) -> t1.concat(t2)).log(); //A,D - B,E - C,F

        StepVerifier.create(mergedFlux)
                .expectSubscription()//To verify the consumer received the Subscription
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

}
