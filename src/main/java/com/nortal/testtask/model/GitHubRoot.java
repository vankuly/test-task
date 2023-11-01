package com.nortal.testtask.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRoot {
    private boolean fork;
    private String name;
    private String login;

    @JsonProperty("owner")
    private void unpackNameFromNestedObject(Map<String, String> commit) {
        login = commit.get("login");
    }
}
