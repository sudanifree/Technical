package com.technical.projects.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ProjectFilesController {

    private final Path repositoryRoot;

    public ProjectFilesController(
            @Value("${technical.projects.repository-root:..}") String repositoryRoot) {
        this.repositoryRoot = Paths.get(repositoryRoot).toAbsolutePath().normalize();
    }

    @GetMapping("/docs/{*filename}")
    public ResponseEntity<Resource> serveDocument(@PathVariable String filename) throws IOException {
        String relativeFilename = filename.startsWith("/") ? filename.substring(1) : filename;
        Path filePath = repositoryRoot.resolve(relativeFilename).normalize();

        if (!filePath.startsWith(repositoryRoot)
                || filePath.getFileName() == null
                || !filePath.getFileName().toString().toLowerCase().endsWith(".md")
                || !Files.isRegularFile(filePath)
                || !filePath.toRealPath().startsWith(repositoryRoot.toRealPath())) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(filePath);
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "text/markdown; charset=UTF-8";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }
}
