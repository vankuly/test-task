package com.nortal.testtask.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.WebFilter;

@SpringBootTest
@Import(HeaderFilter.class)
@AutoConfigureWebTestClient
public class HeaderFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WebFilter headerFilter;

    @Test
    public void shouldReturn200() {
        webTestClient.get()
                .uri("/healthcheck")
                .header("Accept", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody(HttpStatus.class).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void shouldReturn406BecauseMissAcceptHeader() {
        webTestClient.get()
                .uri("/healthcheck")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void shouldReturn406BecauseUseInvalidValueOfAcceptHeader() {
        webTestClient.get()
                .uri("/healthcheck")
                .header("Accept", "application/xml")
                .exchange()
                .expectStatus().is4xxClientError();
    }

}