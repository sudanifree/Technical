package com.technical.projects.controller;

import com.technical.projects.service.ProjectCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectController {

    private final ProjectCatalogService projectCatalogService;

    public ProjectController(ProjectCatalogService projectCatalogService) {
        this.projectCatalogService = projectCatalogService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("projects", projectCatalogService.getProjects());
        return "index";
    }
}
