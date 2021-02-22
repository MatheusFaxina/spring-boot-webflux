package br.com.matheusfaxina.product.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import br.com.matheusfaxina.product.constants.ItemConstants;
import br.com.matheusfaxina.product.handler.ItemsHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET(ItemConstants.END_POINT_FUNCTIONAL_ITEM).and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getAllItems)
                .andRoute(GET(ItemConstants.END_POINT_FUNCTIONAL_ITEM + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getOneItem)
                .andRoute(POST(ItemConstants.END_POINT_FUNCTIONAL_ITEM).and(accept(MediaType.APPLICATION_JSON)), itemsHandler::createItem)
                .andRoute(DELETE(ItemConstants.END_POINT_FUNCTIONAL_ITEM + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::deleteItem)
                .andRoute(PUT(ItemConstants.END_POINT_FUNCTIONAL_ITEM + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET("/fun/runtime-exception").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::itemsException);
   }

}
