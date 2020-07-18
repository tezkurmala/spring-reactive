package tez.reactive.spring.lernreactive.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import tez.reactive.spring.lernreactive.entities.Item;

public interface ItemRepository extends ReactiveCrudRepository<Item, String> {
}
