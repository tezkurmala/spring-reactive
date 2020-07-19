package tez.reactive.spring.lernreactive.repo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tez.reactive.spring.lernreactive.entities.Item;

import java.util.Arrays;
import java.util.List;

//@AutoConfigureWebTestClient
@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext //Gives a brand new app context for each test
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemReactiveRepository;
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
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    public void getItemById(){
        StepVerifier.create(itemReactiveRepository.findById("LTV"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("LG TV"))
                .verifyComplete();
    }

    @Test
    public void findItemByDescription(){
        StepVerifier.create(itemReactiveRepository.findByDescription("LG TV").log("findItemByDescription -> "))
                .expectSubscription()
                .expectNextMatches(item -> item.getId().equals("LTV"))
                .verifyComplete();
    }

    @Test
    public void saveItem(){
        Item item = new Item("PIRO", "Pepperidge Farm Creme Filled", 4.39).just();
        Mono<Item> savedItem = itemReactiveRepository.save(item);
        StepVerifier.create(savedItem.log("Saved Item: "))
                .expectSubscription()
                .expectNextMatches(item1 -> item.getDescription().contains("Pepperidge"))
                .verifyComplete();
    }

    @Test
    public void updateItemPrice(){
        Flux<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                        .map(item -> {
                            item.setPrice(295.55);
                            return item;
                        })
                        .flatMap(item -> itemReactiveRepository.save(item));
        StepVerifier.create(updatedItem.log("Updated Item: "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getPrice().equals(295.55))
                .verifyComplete();
    }

    @Test
    public void deleteItemById(){
        //void mono after deletion
        Mono<Void> deletedItem = itemReactiveRepository.findById("LTV")
                .map(Item::getId)
                .flatMap(itemId -> itemReactiveRepository.deleteById(itemId));
        StepVerifier.create(deletedItem.log())
                //No data and hence
                //NO onNext calls expected from server
                .verifyComplete();
        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void deleteItemsWithDescription(){
        //void flux after deletion
        Flux<Void> deletedItems = itemReactiveRepository.findByDescription("LG TV")
                .flatMap(item -> itemReactiveRepository.delete(item));
        StepVerifier.create(deletedItems.log())
                //No data and hence
                //NO onNext calls expected from server
                .verifyComplete();
        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectNextCount(2)
                .verifyComplete();
    }
}
