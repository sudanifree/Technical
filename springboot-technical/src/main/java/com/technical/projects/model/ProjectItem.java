package com.technical.projects.model;

import java.util.List;

public record ProjectItem(
        String id,
        String name,
        String category,
        String shortDescription,
        String markdownFile,
        List<String> tags
) {
}
