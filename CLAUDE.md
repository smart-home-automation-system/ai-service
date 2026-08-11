# ai-service

`cloud.cholewa:ai-service` — the smart home's **AI integration** service: a thin reactive
(WebFlux) bridge that forwards a prompt to OpenAI and returns the model's reply. Java 21,
Spring Boot 4.1.0 (`spring-boot-starter-parent`), Maven. Local port **6004** (management
**8004**); in the deployed `home` profile it listens on **6200** with Actuator on **8200**
(HAS-165), where the Kubernetes probes and the Prometheus scrape go — the ingress routes only
6200. Docker image `magikabdul/ai-service`. Currently `0.0.1-SNAPSHOT`
(migrated to Java 21 / Boot 4.1 under HAS-125).

Org-wide conventions and working rules (PR flow, branch naming `feature/HAS-<n>`,
"user writes service code, Claude reviews", public-repo hygiene, reactive-everywhere) live
in the workspace `organization.md` — this file only covers what is specific to this repo.
When opened as part of the workspace, those rules apply here too.

## What this service is

One endpoint: **`POST /home/ai`** (`consumes`/`produces` `text/plain`) — the request body
is the prompt string, the response body is the model's text reply. `spring.webflux.base-path`
is `/home/ai` and the controller has no class-level mapping, so the path is exactly
`/home/ai`. No persistence, no messaging, no `smart-home-sdk`/`shelly-client`; the service's
whole job is the OpenAI call.

Flow: `AiController` → `AiBasicService` → Spring AI `ChatClient`. `AiBasicService` builds a
`ChatClient` from an injected `ChatClient.Builder` and calls the model **reactively** via
`chatClient.prompt().user(msg).stream().content().collect(joining())` → `Mono<String>`; the
controller maps that to `ResponseEntity`.

## AI framework — Spring AI, not langchain4j (read before touching dependencies)

The service uses **Spring AI 2.0.0** (`spring-ai-starter-model-openai`), *not* langchain4j.
It was on langchain4j 0.34.0 before HAS-125; langchain4j's Spring Boot starter (1.x) is
**not Spring Boot 4 compatible** (its spring-restclient bridge targets Boot 3 and fails at
context start), whereas Spring AI 2.0.0 is built for our exact stack (Boot 4.1 / Spring
Framework 7). Do not reintroduce langchain4j.

- **Stay non-blocking.** `ChatClient.call()` is blocking (sync OpenAI client); `.stream()`
  runs on the async client (`OpenAIClientAsync` / `AsyncStreamResponse`) and is genuinely
  non-blocking — use it on the WebFlux path. `.collect(joining())` aggregates the streamed
  chunks into one `text/plain` body (no SSE to the client, by design).
- **Config:** `spring.ai.openai.api-key: ${openai-token}` and
  `spring.ai.openai.chat.model: gpt-4o-mini` — use `chat.model`, not the deprecated
  `chat.options.model`.
- **Jackson 2 on the classpath is expected, not a bug.** The official OpenAI SDK
  (`com.openai:openai-java-core`, pulled by Spring AI) and victools jsonschema use Jackson 2
  (`com.fasterxml.jackson`) alongside Boot's Jackson 3 (`tools.jackson`) — different
  packages, no clash. **Do not exclude it** (breaks the SDK). `victools` jsonschema is
  pinned to 5.0.0 in `<dependencyManagement>` to satisfy the enforcer's
  `dependencyConvergence`.

## Error handling

`ExceptionHandlerConfig` registers `cholewa-commons`' `GlobalErrorExceptionHandler`
(`@Order(-2)`) so failures render as the shared `Errors` JSON contract — the same pattern as
`notification-service`. No custom `ExceptionProcessor` yet (no domain exceptions).
`cholewa-commons` 1.1.0 is the only shared-library dependency.

**The provider's error text never reaches the response or the logs** (HAS-165).
`AiBasicService` maps every failure of the `ChatClient` call to
`ResponseStatusException(BAD_GATEWAY, "AI provider call failed")` and logs the exception
*type*, not its message. The reason is concrete: OpenAI quotes the rejected credential back
in its message (`Incorrect API key provided: …`), `DefaultExceptionProcessor` puts an
unhandled message into the response body as `details`, and the cluster logs are shipped to
Loki — so an echoed key would end up stored and searchable. Keep that mapping in place; if a
failure ever needs to be distinguished by the caller, add a typed `ExceptionProcessor` rather
than passing the upstream text through.

## Build, tests & gotchas

- Build/verify: `mvn verify` (JDK 21; on WSL `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`).
  `tidy-maven-plugin:check` binds to the **verify** phase, so `mvn package` (what `CI.yml`
  runs) won't catch POM-style errors — after editing the pom run `mvn tidy:pom`, then verify
  with `mvn verify`.
- **Reactive tests use `publisher.as(StepVerifier::create)`**, never the static
  `StepVerifier.create(publisher)`. The controller is tested as a `@WebFluxTest` slice with
  `WebTestClient` + `@MockitoBean`. The SonarCloud `new_coverage` gate requires new code
  covered — a `@Configuration` bean like `ExceptionHandlerConfig` is **not** loaded by the
  slice, so cover it with a plain unit test (or rely on the `@SpringBootTest` context-load
  test).
- Mockito runs as an explicit `-javaagent` (surefire `argLine` + `maven-dependency-plugin`
  `properties`) with **`@{argLine}` first** so JaCoCo's agent survives — do not drop that
  prefix or coverage silently drops to 0% and the Sonar gate fails.
- logbook's WebFlux autoconfig needs the optional `spring-boot-http-client` module on
  Boot 4.1 (already a dependency) — the context will not start without it.

## CI/CD

`CI.yml` (build + tests on push to `main`/`feature/**`), `sonar.yml` (SonarCloud + JaCoCo),
`release.yml` (on GitHub release → Docker image `magikabdul/ai-service` to Docker Hub, with
the version taken from the git tag). Release flow: the `release` skill from the `smart-home`
plugin.
