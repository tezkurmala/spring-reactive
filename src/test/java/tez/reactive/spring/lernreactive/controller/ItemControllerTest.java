package tez.reactive.spring.lernreactive.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tez.reactive.spring.lernreactive.entities.Item;
import tez.reactive.spring.lernreactive.repo.ItemReactiveRepository;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
//@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test") //Just to avoid data to be injected using command line runner (defined in main class LernreactiveApplication) for these tests
public class ItemControllerTest {
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> items = Arrays.asList(
            new Item("STV", "Samsung TV", 300.00).just(),
            new Item("LTV", "LG TV", 289.90).just(),
            new Item("SWTCH", "Samsung Watch", 276.00).just()
    );

    @Before
    public void setUp(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    public void getAllItems(){
        webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(3);
    }

    @Test
    public void getAllItemsConsumeWith(){
        webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(3)
                .consumeWith(response -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(item -> {
                        Assert.assertTrue(item.getDescription() != null);
                    });
                });
    }

    @Test
    public void getAllItemsReturnResult(){
        Flux<Item> itemFlux = webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log("Server over network: "))
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void getOneItem(){
        webTestClient.get()
                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "LTV")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 289.90);
    }

    @Test
    public void getOneItemInvalidId(){
        webTestClient.get()
                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "LTV1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem() {
        Item item = new Item("PHD", "Portable HDD", 110.25).just();
        webTestClient.post()
                .uri(ItemConstants.ITEM_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.description").isEqualTo("Portable HDD")
                .jsonPath("$.price").isEqualTo(110.25);

    }

    @Test
    public void deleteItem(){
        webTestClient.delete()
                .uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "PHD")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem(){
        double newPrice = 256.55;
        Item requestItem = new Item("SWTCH", "Samsung Watch", newPrice);

        webTestClient.put().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "SWTCH")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestItem), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", newPrice);
    }

    @Test
    public void updateItemInvalidId(){
        double newPrice = 256.55;
        Item requestItem = new Item("SWTCH", "Samsung Watch", newPrice);

        webTestClient.put().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "SWTC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestItem), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void runtimeException(){
        webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/runtimeException"))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Intentionally injected exception");
    }
}
