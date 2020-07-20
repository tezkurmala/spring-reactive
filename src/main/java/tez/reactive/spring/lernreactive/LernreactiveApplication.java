package tez.reactive.spring.lernreactive;

import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.connectionfactory.init.CompositeDatabasePopulator;
import org.springframework.data.r2dbc.connectionfactory.init.ConnectionFactoryInitializer;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import reactor.core.publisher.Flux;
import tez.reactive.spring.lernreactive.entities.Item;
import tez.reactive.spring.lernreactive.repo.ItemReactiveRepository;

import java.time.Duration;
import java.util.Arrays;

@SpringBootApplication
public class LernreactiveApplication {

    private static final Logger log = LoggerFactory.getLogger(LernreactiveApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LernreactiveApplication.class, args);
	}

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));
        initializer.setDatabasePopulator(populator);

        return initializer;
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner demo(ItemReactiveRepository repository) {

        return (args) -> {
            // save a few customers
            repository.saveAll(Arrays.asList(new Item("IPAD2", "Smart Tablet", 300.00).just(),
                    new Item("SAMTAB", "Smart Samsung Tablet", 298.00).just(),
                    new Item("OnePlus", "One Tablet", 280.00).just()))
                    .blockLast(Duration.ofSeconds(10));

            // fetch all customers
            log.info("Items found with findAll():");
            log.info("-------------------------------");
            repository.findAll().doOnNext(item -> {
                log.info(item.toString());
            }).blockLast(Duration.ofSeconds(10));

            log.info("");

            // fetch an individual customer by ID
            repository.findById("IPAD").doOnNext(item -> {
                log.info("Item found with findById(IPAD):");
                log.info("--------------------------------");
                log.info(item.toString());
                log.info("");
            }).block(Duration.ofSeconds(10));

//            Flux<Item> itemsFluxStream = Flux.interval(Duration.ofSeconds(1))
//                    .map(i -> new Item("ITEM" + i, "Item Desc " + i, 100.0 + i).just())
//                    .take(50); //Limiting the insertions to 1000 items
//            //Following is supported for Mongo
//            //Repo listens/subscribes to Flux and inserts on every record/event
//            //repository.insert(Flux)
//
//            itemsFluxStream.subscribe(item -> {
//                log.info("Stream Inserting " + item);
//                repository.save(item);
//            });
        };
    }
}
