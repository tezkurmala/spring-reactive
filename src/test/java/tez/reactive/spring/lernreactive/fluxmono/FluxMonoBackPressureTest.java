package tez.reactive.spring.lernreactive.fluxmono;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxMonoBackPressureTest {
    @Test
    public void backPressureTest(){
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                //Not asking for default unbounded stream
                //Instead requesting one by one
                //Consumer controls how many records, a producer will have to emit
                //Hence consumer will not be swamped and can control the pressure
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressureWithSubscribe(){
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();
        finiteFlux.subscribe(element -> {
            //on each element
            System.out.println("Element is " + element);
        }, exception -> {
            //on exception
            System.err.println("Element is " + exception);
        }, () -> {
            //on complete
            System.err.println("Done ");
        }, subscription -> {
            //on initialization, sever sends subscription
            //time to request for 2 elements
            subscription.request(5);
            //follows with auto cancel as we requested only for 2 elements
            //for explicit cancelling, we have to invoke cancel
            subscription.cancel();
        });
    }

    @Test
    public void customizedBackPressureWithSubscribe(){
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();
        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            //By using BaseSubscriber, though we initially request for unbounded stream,
            //we control the speed of events by using request(1) - one by one
            //and still have control on when to cancel
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("Value received is: " + value);
                if(value == 4) {
                    cancel();
                }
                super.hookOnNext(value);
            }
        });
    }
}
