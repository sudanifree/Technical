package com.technical.projects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointShouldReturnUpStatus() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void projectsEndpointShouldReturnProjectList() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(90)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString(
                                "\"markdownFile\":\"/docs/docs/project-template.md\"")))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString(
                                "\"markdownFile\":\"/docs/springboot-technical/README.md\"")));
    }

    @Test
    void unknownProjectShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/projects/not-a-project"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Project not found: not-a-project"));
    }

    @Test
    void openProjectLinkShouldServeMarkdownDocument() throws Exception {
        mockMvc.perform(get(URI.create("/docs/10-strike.com%20build%20tools%20sd.md")))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith("text/markdown"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("Buildroot")));
    }

    @Test
    void openNestedMarkdownDocumentShouldWork() throws Exception {
        mockMvc.perform(get("/docs/docs/project-template.md"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .string(org.hamcrest.Matchers.containsString("Project title")));
    }

    @Test
    void openArabicMarkdownDocumentShouldWork() throws Exception {
        String path = "/docs/" + UriUtils.encodePath("بيانات المواطنين.md", StandardCharsets.UTF_8);
        mockMvc.perform(get(URI.create(path)))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith("text/markdown"));
    }
}
