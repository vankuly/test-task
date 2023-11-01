package com.nortal.testtask.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class Repository {
    private String repositoryName;
    private String ownerLogin;
    private List<Branch> branches;
}
