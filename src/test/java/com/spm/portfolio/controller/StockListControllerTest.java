package com.spm.portfolio.controller;

import com.spm.portfolio.model.StockList;
import com.spm.portfolio.service.StockListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StockListControllerTest {
    private WebTestClient webTestClient;
    private StockListService stockListService;

    @BeforeEach
    public void setUp() {
        // Create a mock StockListService
        stockListService = mock(StockListService.class);
        // Instantiate the controller with the mocked service
        StockListController controller = new StockListController(stockListService);
        // Bind WebTestClient to the controller with base URL /api/stocks.
        webTestClient = WebTestClient.bindToController(controller)
                .configureClient().baseUrl("/api/stocks").build();
    }

    @Test
    public void testAddStock() {
        // Arrange: Create a sample stock.
        StockList sampleStock = new StockList();
        sampleStock.setStockSymbol("AAPL");
        sampleStock.setUserId("user123");

        // Stub the service to return the sample stock.
        when(stockListService.addStock(any(StockList.class))).thenReturn(Mono.just(sampleStock));

        // Assert: POST /api/stocks/add should return the added stock.
        webTestClient.post()

                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleStock)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockList.class)
                .value(stock -> assertEquals("AAPL", stock.getStockSymbol(), "Stock symbol should match"));
    }

    @Test
    public void testDeleteStock() {
        // Arrange: Stub deletion to return an empty Mono.
        when(stockListService.deleteByStockSymbol("AAPL")).thenReturn(Mono.empty());

        // Act & Assert: DELETE /api/stocks/delete/AAPL should return 204 No Content.
        webTestClient.delete()
                .uri("/AAPL")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void testGetStocksByUser() {
        // Arrange: Create sample stocks.
        StockList stock1 = new StockList();
        stock1.setStockSymbol("AAPL");
        stock1.setUserId("user123");

        StockList stock2 = new StockList();
        stock2.setStockSymbol("GOOGL");
        stock2.setUserId("user123");

        // Stub the service to return a Flux of these stocks.
        when(stockListService.getStocksByUserId("user123")).thenReturn(Flux.just(stock1, stock2));

        //  Assert: GET /api/stocks/users/user123 should return a list of 2 stocks.
        webTestClient.get()
                .uri("/users/user123")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StockList.class)
                .hasSize(2)
                .value(list -> {
                    assertEquals("AAPL", list.get(0).getStockSymbol(), "First stock should be AAPL");
                    assertEquals("GOOGL", list.get(1).getStockSymbol(), "Second stock should be GOOGL");
                });
    }
}