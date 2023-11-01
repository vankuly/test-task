package com.nortal.testtask.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
public class Branch {
    private String name;

    private String sha;

    @JsonProperty("commit")
    private void unpackNameFromNestedObject(Map<String, String> commit) {
        sha = commit.get("sha");
    }
}
