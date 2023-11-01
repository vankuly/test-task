package com.nortal.testtask.controller;

import com.nortal.testtask.model.Branch;
import com.nortal.testtask.model.Repository;
import com.nortal.testtask.service.GitHubService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
public class GitHubController {
    private final GitHubService gitHubService;

    @GetMapping(value = "/repositories/{username}")
    public Flux<Repository> listUserRepositories(
            @PathVariable String username)
    {

        return gitHubService.getNonForkRepositories(username)
                .flatMap(repo -> {
                    Flux<Branch> branches = gitHubService.getBranchesForRepository(
                            repo.getOwnerLogin(), repo.getRepositoryName()
                    );
                    return branches.collectList().map(branchList -> {
                        repo.setBranches(branchList);
                        return repo;
                    });
                });
    }

    @GetMapping(value = "/healthcheck")
    public HttpStatus healthcheck() {
        return HttpStatus.OK;
    }
}


