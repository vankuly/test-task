package com.nortal.testtask.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nortal.testtask.exception.ApiRequestException;
import com.nortal.testtask.model.Branch;
import com.nortal.testtask.model.Repository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest()
@AutoConfigureWebTestClient
class GitHubControllerTestIT {
    private static MockWebServer mockWebServer;

    @Autowired
    WebClient webclient;

    @Autowired
    private GitHubController controller;

    @BeforeAll
    public static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(9090);
    }

    @AfterAll
    public static void tearDown() throws IOException {

        mockWebServer.shutdown();
    }

    @Test
    public void shouldReturnListOfRepositoriesWithBranches() throws Exception {

        Repository repository = new Repository();
        repository.setRepositoryName("ironfunding-jan-18");
        repository.setOwnerLogin("ta-web-paris");

        Branch branch = new Branch();
        branch.setName("master");
        branch.setSha("513b9e0b7e906693b924793046b12548adc42374");

        List<Branch> branchList = new ArrayList<>();
        branchList.add(branch);

        repository.setBranches(branchList);

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(new Repository()))
                .addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        );

        Flux<Repository> response = controller.listUserRepositories(
                "ta-web-paris"
        );

        Assertions.assertTrue(Objects.requireNonNull(response.collectList().block()).contains(repository));
    }

    @Test
    public void shouldFailUserDoesNotExists() throws Exception {

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(new Repository()))
                .addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        );

        controller.listUserRepositories("ta-web-parisssssss");

        Assertions.assertThrows(ApiRequestException.class, ()->{
            throw new ApiRequestException("User not found");
        });
    }

    @Test
    public void shouldFailWithoutAcceptHeader() throws Exception {

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(new Repository()))
        );

        controller.listUserRepositories("ta-web-paris");

        Assertions.assertThrows(ResponseStatusException.class, ()->{
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE,"Accept header must be 'application/json'"
            );
        });
    }

    @Test
    public void shouldFailBecauseAcceptHeaderHasInvalidValue() throws Exception {

        mockWebServer.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(new Repository()))
                .addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_CBOR)
        );

        controller.listUserRepositories("ta-web-paris");

        Assertions.assertThrows(ResponseStatusException.class, ()->{
            throw new ResponseStatusException(
                    HttpStatus.NOT_ACCEPTABLE, "Accept header must be 'application/json'"
            );
        });
    }

}