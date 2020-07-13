package tez.reactive.spring.lernreactive.fluxmono;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxMonoTransformTest {
    List<String> names = Arrays.asList("Tez", "Spring", "Learn", "Reactive");
    @Test
    public void transformUsingMapUpperCaseTest(){
        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();
        StepVerifier.create(namesFlux)
                .expectNext("TEZ", "SPRING", "LEARN", "REACTIVE")
                .verifyComplete();
    }

    @Test
    public void transformUsingMapLengthTest(){
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();
        StepVerifier.create(namesFlux)
                .expectNext(3, 6, 5, 8)
                .verifyComplete();
    }

    @Test
    public void transformUsingMapLengthRepeatTest(){
        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)//Same list of values is repeated again in transmission
                .log();
        StepVerifier.create(namesFlux)
                .expectNext(3, 6, 5, 8, 3, 6, 5, 8)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap(){
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                //Need to merge multiple Fluxes
                //A -> [A, ANew], B -> [B, BNew], C - [C, CNew] into [A, ANew, B, BNew, C, CNew, .....]
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                }) //db or external service call that returns a flux -> s -> Flux<String>
                .log();
        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapParallel(){
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                //Need to merge multiple Fluxes
                //A -> [A, ANew], B -> [B, BNew], C - [C, CNew] into [A, ANew, B, BNew, C, CNew, .....]
                //Splits the flux into multiple fluxes
                .window(2) //Flux<Flux<String>> -> Flux 1(A, B), Flux2(C, D), Flux3(E, F)
                .flatMap(s ->
                    s.map(this::convertToList) //Mapping each element inside Flux 1(A, B) into List A -> [A, ANew], B-> [B, BNew]
                            //We ask to process in parallel from the following step to subscribeOn(Schedulers.parallel())
                            .subscribeOn(Schedulers.parallel()) //Flux<List<String>> Flux([A,ANew], [B,BNew])
                            .flatMap(t -> Flux.fromIterable(t)) //Flux<String>  Flux[A, ANew B, BNew]
                )
                .log();
        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMapParallelMaintainOrder(){
        Flux<String> namesFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                //Need to merge multiple Fluxes
                //A -> [A, ANew], B -> [B, BNew], C - [C, CNew] into [A, ANew, B, BNew, C, CNew, .....]
                //Splits the flux into multiple fluxes
                .window(2) //Flux<Flux<String>> -> Flux 1(A, B), Flux2(C, D), Flux3(E, F)
                /*.flatMap(s ->*/  //Jumbles the order of stream though takes < 2 secs
                /*.concatMap(s ->*/  //Killed the speed again takes 6 secs again
                .flatMapSequential(s -> // <2 secs and also retains the order
                        s.map(this::convertToList)
                                .subscribeOn(Schedulers.parallel()) //Flux<List<String>> Flux([A,ANew], [B,BNew])
                                .flatMap(t -> Flux.fromIterable(t)) //Flux<String>  Flux[A, ANew B, BNew]
                )
                .log();
        StepVerifier.create(namesFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, s+"New");
    }
}
