package br.com.matheusfaxina.product.handler;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import br.com.matheusfaxina.product.constants.ItemConstants;
import br.com.matheusfaxina.product.document.Item;
import br.com.matheusfaxina.product.repository.ItemRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
public class ItemHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ItemRepository itemReactiveRepository;

    @Before
    public void setUp() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserted item is: " + item);
                }))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        webTestClient.get().uri(ItemConstants.END_POINT_FUNCTIONAL_ITEM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItemsApproach() {
        webTestClient.get().uri(ItemConstants.END_POINT_FUNCTIONAL_ITEM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith((response) -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach(item -> {
                        Assert.assertTrue(item.getId() != null);
                    });
                });
    }

    @Test
    public void getAllItemsApproach2() {
        Flux<Item> itemsFlux = webTestClient.get().uri(ItemConstants.END_POINT_FUNCTIONAL_ITEM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemsFlux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {
        webTestClient.get().uri(ItemConstants.END_POINT_FUNCTIONAL_ITEM.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 149.99);
    }

    @Test
    public void getOneItemNotFound() {
        webTestClient.get().uri(ItemConstants.END_POINT_FUNCTIONAL_ITEM.concat("/{id}"), "NOT_FOUND")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem() {
        Item item = new Item(null, "Iphone X", 420.0);

        webTestClient.post().uri(ItemConstants.END_POINT_FUNCTIONAL_ITEM)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 420.0).isNotEmpty()
                .jsonPath("$.description").isEqualTo("Iphone X");
    }

    @Test
    public void deleteItem() {
        webTestClient.delete().uri(ItemConstants.END_POINT_FUNCTIONAL_ITEM.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem() {
        double newPrice = 129.99;
        Item item = new Item(null, "Beats Headphone", newPrice);

        webTestClient.put().uri(ItemConstants.END_POINT_FUNCTIONAL_ITEM.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", newPrice);
    }

    @Test
    public void updateItemNotFound() {
        double newPrice = 129.99;
        Item item = new Item(null, "Beats Headphone", newPrice);

        webTestClient.put().uri(ItemConstants.END_POINT_FUNCTIONAL_ITEM.concat("/{id}"), "NOT_FOUNDD")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    private List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 420.0),
                new Item(null, "Apple Watch", 300.0),
                new Item(null, "Beats Headphone", 149.99),
                new Item("ABC", "Bose Headphone", 149.99));
    }

}
