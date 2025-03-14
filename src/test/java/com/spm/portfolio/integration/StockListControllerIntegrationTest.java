package com.spm.portfolio.integration;

import com.spm.portfolio.model.StockList;
import com.spm.portfolio.service.StockListService;
import com.spm.portfolio.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class StockListControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    // Replace real StockListService with a mock.
    @MockBean
    private StockListService stockListService;

    // Optionally, you can also inject JwtUtil to generate a valid token.
    @Autowired
    private JwtUtil jwtUtil;

    // A dummy token for testing; adjust so that it is accepted by your JwtSecurityContextRepository.
    private String getValidToken() {
        // For example, if your JwtUtil.generateToken returns a token that your filters accept,
        // you can use:
        return "Bearer " + jwtUtil.generateToken("user123");
        // Alternatively, if you want to use a fixed dummy token, simply return:
        // return "Bearer dummy-valid-token";
    }

    @Test
    public void testAddStockIntegration() {
        // Arrange: Create a sample stock.
        StockList sampleStock = new StockList();
        sampleStock.setStockSymbol("AAPL");
        sampleStock.setUserId("user123");

        when(stockListService.addStock(any(StockList.class))).thenReturn(Mono.just(sampleStock));

        // Act & Assert: Call POST /api/stocks/add with a valid token.
        webTestClient.post()
                .uri("/api/stocks")
                .header(HttpHeaders.AUTHORIZATION, getValidToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleStock)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockList.class)
                .value(stock -> assertEquals("AAPL", stock.getStockSymbol(), "Stock symbol should match"));
    }

    @Test
    public void testDeleteStockIntegration() {
        // Arrange: Stub the delete method to return an empty Mono.
        when(stockListService.deleteByStockSymbol(eq("AAPL"))).thenReturn(Mono.empty());

        // Act & Assert: Call DELETE /api/stocks/delete/AAPL with a valid token.
        webTestClient.delete()
                .uri("/api/stocks/AAPL")
                .header(HttpHeaders.AUTHORIZATION, getValidToken())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void testGetStocksByUserIntegration() {
        // Arrange: Create sample stocks.
        StockList stock1 = new StockList();
        stock1.setStockSymbol("AAPL");
        stock1.setUserId("user123");

        StockList stock2 = new StockList();
        stock2.setStockSymbol("GOOGL");
        stock2.setUserId("user123");

        when(stockListService.getStocksByUserId("user123")).thenReturn(Flux.just(stock1, stock2));

        // Act & Assert: Call GET /api/stocks/users/user123 with a valid token.
        webTestClient.get()
                .uri("/api/stocks/users/user123")
                .header(HttpHeaders.AUTHORIZATION, getValidToken())
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
