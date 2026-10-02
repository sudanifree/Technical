# Technical Projects Catalog

A read-only web application for browsing the Markdown files across this repository. At startup it discovers Markdown documents recursively, then provides a searchable catalog, a JSON API, a health endpoint, and links to the original files.

> **What this project is:** a Spring Boot application for discovering and reading project documentation. It is not itself an Nginx, Cherokee, or general-purpose web-server implementation. The repository's web-server notes are catalog content and deployment references.

## Project goals

- Make selected technical notes easy to find by title, category, and tags.
- Serve the catalog as a browser-based application and as JSON.
- Let users open the original Markdown files without copying them into the application.
- Keep the first version small: no database, accounts, write operations, or paid services.

## Features and scope

### Included

- Automatically discovered Markdown catalog page at `/` with search and category filtering.
- Read-only project API at `/api/projects` and `/api/projects/{id}`.
- Basic liveness response at `/api/health`.
- Markdown delivery at `/docs/{filename}` from the configured repository root.
- Maven build and Spring Boot tests.

### Not included

- Editing, uploading, or deleting documentation.
- User accounts, authentication, or authorization.
- A database, distributed cache, or application-level monitoring backend.
- Provisioning Ubuntu, Nginx, Cherokee, MySQL, Redis, Prometheus, or Grafana.

The separate notes [Free Website SD.md](../Free%20Website%20SD.md) and [Cherokee webserver SD.md](../Cherokee%20webserver%20SD.md) describe web-server options and setup ideas; they are not automatically installed or managed by this application.

## Technology

| Component | Version / choice |
| --- | --- |
| Java | 17 |
| Spring Boot | 4.1.1 |
| Build | Maven |
| Web and API | Spring MVC |
| Server-rendered page | Thymeleaf |
| UI assets | HTML, CSS, and JavaScript |
| Storage | Markdown files in the repository; no database |

Spring Boot 4.1.1 is set in the parent version in [`pom.xml`](pom.xml). The dependencies and their versions are managed by the Spring Boot parent.

## Architecture

```text
Browser
  ├── GET /                 -> Thymeleaf page and static CSS/JavaScript
  ├── GET /api/projects     -> JSON catalog
  ├── GET /api/health       -> JSON liveness response
  └── GET /docs/{path}     -> Markdown file under repository root
                                  |
                                  └── Technical repository Markdown files
```

`ProjectCatalogService` discovers Markdown files under the configured repository root at startup. Build output and dependency directories (`target`, `build`, `dist`, and `node_modules`) and tool metadata directories (`.git`, `.idea`, and `.vscode`) are excluded. The in-memory catalog is refreshed when the application restarts; document contents are read from disk when requested. Document links can address nested directories, but requests are confined to regular Markdown files whose resolved paths remain under the configured repository root.

## Requirements

- JDK 17 or newer.
- Maven 3.9 or newer (or use the Maven wrapper if one is added to this module).
- A checkout of this repository, including the Markdown files shown in the catalog.
- Port 8080 available locally, unless `server.port` is overridden.

## Run locally

From the repository root:

```bash
cd springboot-technical
mvn spring-boot:run
```

Then visit:

- Catalog: <http://localhost:8080/>
- Projects API: <http://localhost:8080/api/projects>
- Health: <http://localhost:8080/api/health>
- Example document: <http://localhost:8080/docs/Free%20Website%20SD.md>

By default, `technical.projects.repository-root` is `..`, interpreted relative to the application's working directory. Running the command above from `springboot-technical` therefore points document requests at the repository root.

To use a different document directory, set `TECHNICAL_REPOSITORY_ROOT` to its absolute path:

```bash
TECHNICAL_REPOSITORY_ROOT=/srv/technical-docs mvn spring-boot:run
```

On PowerShell:

```powershell
$env:TECHNICAL_REPOSITORY_ROOT = "C:\srv\technical-docs"
mvn spring-boot:run
```

## HTTP API

### `GET /api/projects`

Returns the discovered catalog as an array of document objects. Each object contains `id`, `name`, `category`, `shortDescription`, `markdownFile`, and `tags`.

### `GET /api/projects/{id}`

Returns the catalog item matching the ID assigned to that document. IDs are stable for a given relative file path. Returns HTTP `404 Not Found` with a problem-detail response when the ID does not exist.

### `GET /api/health`

Returns a lightweight application status response, for example:

```json
{
  "status": "UP",
  "service": "technical-projects"
}
```

This is an application liveness endpoint, not a comprehensive readiness check of external dependencies.

### `GET /docs/{path}`

Returns a Markdown document under the configured repository root, including nested directories. URL-encode special characters in path segments as needed. A missing file, non-Markdown file, or path that resolves outside the configured root is not served.

## Configuration

The default settings are in `src/main/resources/application.properties`.

| Setting | Default | Purpose |
| --- | --- | --- |
| `server.port` | `8080` | HTTP port for the embedded server |
| `spring.application.name` | `technical-projects` | Application name |
| `spring.thymeleaf.cache` | `false` | Template cache setting for development |
| `technical.projects.repository-root` | `..` | Root directory from which Markdown documents may be served |

The repository-root setting can be supplied through `TECHNICAL_REPOSITORY_ROOT`. In production, configure an explicit path containing only the documents intended to be public.

## Build and test

From `springboot-technical/`:

```bash
mvn test
mvn package
```

The Spring Boot integration tests exercise the health endpoint, catalog discovery (including nested documents), unknown-ID 404 handling, and retrieval of Markdown files from root, nested, and non-English paths.

## Deployment

### Run the packaged application

```bash
cd springboot-technical
mvn clean package
TECHNICAL_REPOSITORY_ROOT=/srv/technical-docs \
  java -jar target/technical-projects-0.0.1-SNAPSHOT.jar
```

Run the process as a dedicated unprivileged OS account. Make the configured document directory readable by that account and do not place secrets, private notes, backups, or unrelated files in it.

### Reverse proxy and TLS

For a public deployment, put a maintained reverse proxy such as Nginx in front of the application. Terminate TLS at the proxy, forward only the required HTTP traffic to the application, and keep port 8080 inaccessible from untrusted networks. Obtain and renew certificates for the actual deployment domain using a trusted certificate authority; certificate setup is environment-specific and is not part of this application.

Use a firewall to allow only the intended public ports (normally 80/443 at the proxy and no direct public access to 8080). Configure request-size limits, rate limits, and access/error logging at the proxy according to the deployment's traffic and operational needs.

## Security and operations

- The application is read-only and has no authentication. Treat its catalog and all documents under the configured repository root as public.
- Keep the document root narrow and do not point it at a directory containing credentials, configuration secrets, or unrelated system files.
- The document controller confines resolved paths to the configured root, but this is not a substitute for operating-system permissions or careful document-root selection.
- Run with least privilege and keep Java, Maven dependencies, the host OS, and the reverse proxy patched.
- Use HTTPS for public access. Do not expose the embedded server directly to the Internet when operating behind a reverse proxy.
- Monitor application and proxy logs, disk space, process health, and certificate expiry. The built-in health endpoint provides only basic liveness.
- Back up the Markdown source and deployment configuration using the repository's normal backup process; there is no application database to back up.

## Troubleshooting

| Symptom | Checks |
| --- | --- |
| `mvn` or Java is not found | Install JDK 17+ and Maven, then confirm with `java -version` and `mvn -version`. |
| Port 8080 is already in use | Stop the conflicting process or choose another port, for example `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081`. |
| A document returns 404 | Confirm the file exists under the configured root, check filename spelling/URL encoding, and verify the service account can read it. |
| Catalog page loads but documents do not | Confirm `TECHNICAL_REPOSITORY_ROOT` points to the repository/document directory, not to the module directory. |
| Build fails resolving Spring Boot | Check Maven Central connectivity and verify the Spring Boot version and Java requirements in `pom.xml`. |

## Known limitations and next steps

- The catalog is discovered at application startup, so newly added or removed Markdown files appear after the next restart.
- There is no authentication, write API, database, readiness probe, container image, or service-manager definition.
- Production deployments should add environment-specific service management, TLS/reverse-proxy configuration, structured monitoring, and an operational backup/restore procedure.
- If the catalog grows substantially, consider adding automated catalog validation and pagination before introducing a database.

## Source layout

```text
src/
├── main/
│   ├── java/com/technical/projects/
│   │   ├── controller/       # Page, API, and Markdown routes
│   │   ├── model/            # Catalog item type
│   │   ├── service/          # Catalog contents and document links
│   │   └── TechnicalProjectsApplication.java
│   └── resources/
│       ├── static/           # CSS and JavaScript
│       ├── templates/        # Thymeleaf page
│       └── application.properties
└── test/                     # Spring MVC integration tests
```

## Related documentation

- [Repository overview](../README.md)
- [Free web-server plan](../Free%20Website%20SD.md)
- [Cherokee web-server installation notes](../Cherokee%20webserver%20SD.md)
- [Project completion template](../docs/project-template.md)
