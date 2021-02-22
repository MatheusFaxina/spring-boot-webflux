package br.com.matheusfaxina.product.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import br.com.matheusfaxina.product.document.Item;
import br.com.matheusfaxina.product.repository.ItemRepository;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ItemsHandler {

    @Autowired
    private ItemRepository itemReactiveRepository;

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findById(serverRequest.pathVariable("id")), Item.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
        Mono<Item> itemTobeInserted = serverRequest.bodyToMono(Item.class);

        return itemTobeInserted.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(itemReactiveRepository.save(item), Item.class)
        );
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
        Mono<Void> deleteItem = itemReactiveRepository.deleteById(serverRequest.pathVariable("id"));

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteItem, Void.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap(item -> {
                    Mono<Item> itemMono = itemReactiveRepository.findById(serverRequest.pathVariable("id"))
                            .flatMap(currentItem ->  {
                                currentItem.setDescription(item.getDescription());
                                currentItem.setPrice(item.getPrice());

                                return itemReactiveRepository.save(currentItem);
                            });

                    return itemMono;
                });

        return updatedItem.flatMap(item ->
            ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(fromObject(item))
                    .switchIfEmpty(ServerResponse.notFound().build()));
    }

    public Mono<ServerResponse> itemsException(ServerRequest serverRequest) {
        throw new RuntimeException("RuntimeExeception Ocured.");
    }

}
