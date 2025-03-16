package com.spm.portfolio.integration;

import com.spm.portfolio.controller.StockListController;
import com.spm.portfolio.model.StockList;
import com.spm.portfolio.service.StockListService;
import com.spm.portfolio.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StockListControllerIntegrationTest {

    private WebTestClient webTestClient;

    @Mock
    private StockListService stockListService;

    @InjectMocks
    private StockListController stockListController; // ✅ Controller under test

    @Mock
    private JwtUtil jwtUtil;

    private String getValidToken() {
        return "Bearer " + jwtUtil.generateToken("user123");
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(stockListController).build(); // ✅ Manually bind the controller
    }

    @Test
    public void testAddStockIntegration() {
        StockList sampleStock = new StockList();
        sampleStock.setStockSymbol("AAPL");
        sampleStock.setUserId("user123");

        // ✅ Mock behavior
        when(stockListService.addStock(any(StockList.class))).thenReturn(Mono.just(sampleStock));

        webTestClient.post()
                .uri("/api/stocksList")
                .header(HttpHeaders.AUTHORIZATION, getValidToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleStock)
                .exchange()
                .expectStatus().isOk()
                .expectBody(StockList.class)
                .value(stock -> assertEquals("AAPL", stock.getStockSymbol()));
    }

    @Test
    public void testDeleteStockIntegration() {
        when(stockListService.deleteByStockSymbol(eq("AAPL"))).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/stocksList/AAPL")
                .header(HttpHeaders.AUTHORIZATION, getValidToken())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void testGetStocksByUserIntegration() {
        StockList stock1 = new StockList();
        stock1.setStockSymbol("AAPL");
        stock1.setUserId("user123");

        StockList stock2 = new StockList();
        stock2.setStockSymbol("GOOGL");
        stock2.setUserId("user123");

        when(stockListService.getStocksByUserId("user123")).thenReturn(Flux.just(stock1, stock2));

        webTestClient.get()
                .uri("/api/stocksList/users/user123")
                .header(HttpHeaders.AUTHORIZATION, getValidToken())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(StockList.class)
                .hasSize(2)
                .value(list -> {
                    assertEquals("AAPL", list.get(0).getStockSymbol());
                    assertEquals("GOOGL", list.get(1).getStockSymbol());
                });
    }
}
