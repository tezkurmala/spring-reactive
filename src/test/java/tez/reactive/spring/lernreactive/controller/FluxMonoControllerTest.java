package tez.reactive.spring.lernreactive.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@WebFluxTest
@DirtiesContext
public class FluxMonoControllerTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach() {
        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() //invokes endpoint
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void flux_approach2() {
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() //invokes endpoint
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void flux_approach3() {
        EntityExchangeResult<List<Integer>> listEntityExchangeResult = webTestClient
                .get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() //invokes endpoint
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        List<Integer> expectedIntegerList = Arrays.asList(1,2,3,4);
        Assertions.assertEquals(expectedIntegerList, listEntityExchangeResult.getResponseBody());
    }

    @Test
    public void flux_approach4() {
        List<Integer> expectedIntegerList = Arrays.asList(1,2,3,4);
        webTestClient
                .get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() //invokes endpoint
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith(response -> {
                    //DefaultWebTestClient$DefaultListBodySpec cannot be cast to class EntityExchangeResult
                    Assertions.assertEquals(true, response.getResponseBody().containsAll(expectedIntegerList));
                });
    }

    @Test
    public void fluxStream(){
        Flux<Long> longStreamFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange() //invokes endpoint
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longStreamFlux)
                .expectNext(1l)
                .expectNext(2l)
                .expectNext(3l)
                .thenCancel()
                .verify();
    }

    @Test
    public void mono(){
        Integer expectedValue = new Integer(1);
        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> Assert.assertEquals(expectedValue, response.getResponseBody()));
    }
}

