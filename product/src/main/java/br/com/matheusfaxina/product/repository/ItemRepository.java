package br.com.matheusfaxina.product.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import br.com.matheusfaxina.product.document.Item;
import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

    Flux<Item> findByDescription(String description);

}
