package com.technical.projects.controller;

import com.technical.projects.model.ProjectItem;
import com.technical.projects.service.ProjectCatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProjectApiController {

    private final ProjectCatalogService projectCatalogService;

    public ProjectApiController(ProjectCatalogService projectCatalogService) {
        this.projectCatalogService = projectCatalogService;
    }

    @GetMapping("/projects")
    public List<ProjectItem> getProjects() {
        return projectCatalogService.getProjects();
    }

    @GetMapping("/projects/{id}")
    public ResponseEntity<?> getProject(@PathVariable String id) {
        return projectCatalogService.getProjects().stream()
                .filter(project -> project.id().equals(id))
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ProblemDetail.forStatusAndDetail(
                                HttpStatus.NOT_FOUND, "Project not found: " + id)));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "technical-projects");
        return response;
    }
}
