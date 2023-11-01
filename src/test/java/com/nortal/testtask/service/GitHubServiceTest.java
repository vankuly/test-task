package com.nortal.testtask.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nortal.testtask.exception.ApiRequestException;
import com.nortal.testtask.model.Branch;
import com.nortal.testtask.model.Repository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest()
@AutoConfigureWebTestClient
class GitHubServiceTest {
    private static MockWebServer mockWebServer;

    @Autowired
    WebClient webclient;

    @Autowired
    private GitHubService service;

    @BeforeAll
    public static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(9090);
    }

    @Test
    public void shouldReturnListOfRepositories() throws Exception {

        Repository repository = new Repository();
        repository.setRepositoryName("ironfunding-jan-18");
        repository.setOwnerLogin("ta-web-paris");

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(new Repository()))
                .addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        );

        Flux<Repository> response = service.getNonForkRepositories("ta-web-paris");

        Assertions.assertTrue(Objects.requireNonNull(response.collectList().block()).contains(repository));
        assertEquals(16, Objects.requireNonNull(response.collectList().block()).size());
    }

    @Test
    public void shouldReturn404StatusBecauseSetInvalidUsername() throws Exception {

        Repository repository = new Repository();
        repository.setRepositoryName("ironfunding-jan-18");
        repository.setOwnerLogin("ta-web-paris");

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(new Repository()))
                .addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        );

        Flux<Repository> response = service.getNonForkRepositories("tralalalalaaa");

        Assertions.assertThrows(ApiRequestException.class, ()->{
            throw new ApiRequestException("User not found");
        });
    }

    @Test
    public void shouldReturnBranchesOfRepository() throws Exception {

        Branch branch = new Branch();
        branch.setName("master");
        branch.setSha("513b9e0b7e906693b924793046b12548adc42374");

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(new Branch()))
        );

        Flux<Branch> response = service.getBranchesForRepository("ta-web-paris", "ironfunding-jan-18");

        Assertions.assertTrue(Objects.requireNonNull(response.collectList().block()).contains(branch));
        assertEquals(1, Objects.requireNonNull(response.collectList().block()).size());
    }

}