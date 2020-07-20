package tez.reactive.spring.lernreactive.controller.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tez.reactive.spring.lernreactive.entities.Item;
import tez.reactive.spring.lernreactive.repo.ItemReactiveRepository;
import static tez.reactive.spring.lernreactive.controller.ItemConstants.ITEM_ENDPOINT_V1;
import static tez.reactive.spring.lernreactive.controller.ItemConstants.ITEM_STREAM_ENDPOINT_V1;

@RestController
@Slf4j
public class ItemController {
    //Following is moved to ControllerExceptionHandler using @ControllerAdvice
    // that allows generic or common logic across multiple containers.
//    @ExceptionHandler(RuntimeException.class)
//    public ResponseEntity<String> handleRuntimeException(RuntimeException ex){
//        log.error("Exception caught in handleException: {}", ex);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ex.getMessage());
//    }
//
    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_ENDPOINT_V1)
    public Flux<Item> getAllItems(){
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id){
        return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_ENDPOINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){
        item.just();
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_ENDPOINT_V1 + "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteOneItem(@PathVariable String id){
        return itemReactiveRepository.deleteById(id);
    }

    @PutMapping(ITEM_ENDPOINT_V1 + "/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Item>> updateItem(@RequestBody Item requestItem, @PathVariable String id){
        return itemReactiveRepository.findById(id)
                .flatMap(existingItem -> {
                    existingItem.setPrice(requestItem.getPrice());
                    existingItem.setDescription(requestItem.getDescription());
                    return itemReactiveRepository.save(existingItem);
                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(ITEM_ENDPOINT_V1 + "/runtimeException")
    public Flux<Item> runtimeException(){
        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("Intentionally injected exception")));
    }

    @GetMapping(value = ITEM_STREAM_ENDPOINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Item> getItemsStream(){
        //Mongo supports Tailable but not sure for RDBMS
        //Tailable allows realize new data added and stream onto channel opened in a continuous fashion.
        return itemReactiveRepository.findAll();
    }

}
