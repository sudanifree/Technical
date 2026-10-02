package com.technical.projects.service;

import com.technical.projects.model.ProjectItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
public class ProjectCatalogService {

    private static final int DESCRIPTION_LIMIT = 220;
    private static final Pattern NON_WORD = Pattern.compile("[^\\p{IsAlphabetic}\\p{IsDigit}]+");
    private static final Set<String> EXCLUDED_DIRECTORIES = Set.of(
            ".git", ".idea", ".vscode", "build", "dist", "node_modules", "target"
    );

    private final Path repositoryRoot;
    private final List<ProjectItem> projects;

    public ProjectCatalogService(
            @Value("${technical.projects.repository-root:..}") String repositoryRoot) {
        this.repositoryRoot = Paths.get(repositoryRoot).toAbsolutePath().normalize();
        this.projects = discoverProjects();
    }

    public List<ProjectItem> getProjects() {
        return projects;
    }

    private List<ProjectItem> discoverProjects() {
        if (!Files.isDirectory(repositoryRoot)) {
            throw new IllegalStateException("Markdown repository directory does not exist: " + repositoryRoot);
        }

        try (Stream<Path> paths = Files.walk(repositoryRoot)) {
            List<Path> markdownFiles = paths
                    .filter(this::isIncludedMarkdown)
                    .sorted()
                    .toList();
            List<ProjectItem> discovered = new ArrayList<>(markdownFiles.size());

            for (Path file : markdownFiles) {
                Path realFile = file.toRealPath();
                if (!realFile.startsWith(repositoryRoot.toRealPath())) {
                    continue;
                }
                discovered.add(toProjectItem(file));
            }

            discovered.sort((left, right) -> {
                int categoryOrder = left.category().compareToIgnoreCase(right.category());
                return categoryOrder != 0 ? categoryOrder : left.name().compareToIgnoreCase(right.name());
            });
            return List.copyOf(discovered);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not discover Markdown files under " + repositoryRoot, exception);
        }
    }

    private boolean isIncludedMarkdown(Path path) {
        Path relative = repositoryRoot.relativize(path);
        for (Path segment : relative) {
            if (EXCLUDED_DIRECTORIES.contains(segment.toString())) {
                return false;
            }
        }

        return Files.isRegularFile(path)
                && path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".md");
    }

    private ProjectItem toProjectItem(Path file) throws IOException {
        Path relative = repositoryRoot.relativize(file);
        String relativeName = relative.toString().replace(file.getFileSystem().getSeparator(), "/");
        String name = file.getFileName().toString();
        name = name.substring(0, name.length() - 3).replace('_', ' ').replace('-', ' ');
        Path parent = relative.getParent();
        String category = parent == null ? "General" : parent.toString().replace('_', ' ').replace('-', ' ');

        return new ProjectItem(
                slug(relativeName) + "-" + shortHash(relativeName),
                name,
                category,
                description(file),
                "/docs/" + UriUtils.encodePath(relativeName, StandardCharsets.UTF_8),
                tags(name, category)
        );
    }

    private String description(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String candidate = line.strip();
                if (!candidate.isEmpty() && !candidate.startsWith("#")) {
                    if (candidate.length() > DESCRIPTION_LIMIT) {
                        return candidate.substring(0, DESCRIPTION_LIMIT - 3) + "...";
                    }
                    return candidate;
                }
            }
        }
        return "Markdown documentation.";
    }

    private List<String> tags(String name, String category) {
        LinkedHashSet<String> tags = new LinkedHashSet<>();
        tags.add(category);
        for (String token : name.split("[\\s_]+")) {
            if (!token.isBlank()) {
                tags.add(token);
            }
            if (tags.size() >= 6) {
                break;
            }
        }
        return List.copyOf(tags);
    }

    private String slug(String value) {
        String slug = NON_WORD.matcher(value.toLowerCase(Locale.ROOT)).replaceAll("-")
                .replaceAll("(^-+|-+$)", "");
        return slug.isEmpty() ? "document" : slug;
    }

    private String shortHash(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest, 0, 4);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
