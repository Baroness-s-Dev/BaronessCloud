package ru.baronessdev.lib.cloud;

import lombok.Data;

import java.util.List;

@Data
public class Index {

    private final String name;
    private final String url;
    private final String changelogsUrl;
    private final List<String> description;
    private final String material;
    private final String fallbackMaterial;
    private final List<String> depends;
    private final List<String> sync;
}
