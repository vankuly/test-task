package com.nortal.testtask.service;

import com.nortal.testtask.model.Branch;
import com.nortal.testtask.model.Repository;
import com.nortal.testtask.exception.ApiRequestException;
import com.nortal.testtask.model.GitHubRoot;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GitHubService {
    private final WebClient webClient;

    public Flux<Repository> getNonForkRepositories(String username) {

        return webClient.get()
                .uri("/users/{username}/repos?type=owner", username)
                .retrieve()
                .bodyToFlux(GitHubRoot.class)
                .onErrorResume(
                        WebClientResponseException.class,
                        ex -> ex.getStatusCode() == HttpStatusCode.valueOf(404) ?
                                Mono.error(new ApiRequestException("User not found")) : Mono.error(ex)
                )
                .filter(it -> !it.isFork())
                .map(gitHubRoot -> {
                    Repository repository = new Repository();
                    repository.setRepositoryName(gitHubRoot.getName());
                    repository.setOwnerLogin(gitHubRoot.getLogin());

                    return repository;
                });
    }

    public Flux<Branch> getBranchesForRepository(String owner, String repoName) {
        return webClient.get()
                .uri("/repos/{owner}/{repoName}/branches", owner, repoName)
                .retrieve()
                .bodyToFlux(Branch.class);
    }
}


