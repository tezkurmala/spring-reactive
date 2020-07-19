package tez.reactive.spring.lernreactive.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import tez.reactive.spring.lernreactive.entities.Item;

@Component
public interface ItemRepository extends ReactiveCrudRepository<Item, String> {
    Flux<Item> findByDescription(String description);

}
