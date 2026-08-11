# ai-service

[![CI](https://github.com/smart-home-automation-system/ai-service/actions/workflows/CI.yml/badge.svg)](https://github.com/smart-home-automation-system/ai-service/actions/workflows/CI.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=smart-home-automation-system_ai-service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=smart-home-automation-system_ai-service)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=smart-home-automation-system_ai-service&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=smart-home-automation-system_ai-service)

![GitHub Release Date - Published_At](https://img.shields.io/github/release-date/smart-home-automation-system/ai-service?style=plastic)
![GitHub Release](https://img.shields.io/github/v/release/smart-home-automation-system/ai-service?style=plastic)


---

![GitHub top language](https://img.shields.io/github/languages/top/smart-home-automation-system/ai-service?style=plastic)
![Java](https://img.shields.io/badge/java-21-yellow?style=plastic)
![SpringBoot](https://img.shields.io/badge/SpringBoot-4.1.0-blue?style=plastic)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=smart-home-automation-system_ai-service&metric=coverage)](https://sonarcloud.io/summary/new_code?id=smart-home-automation-system_ai-service)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=smart-home-automation-system_ai-service&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=smart-home-automation-system_ai-service)

![GitHub issues](https://img.shields.io/github/issues/smart-home-automation-system/ai-service?style=plastic)
![GitHub contributors](https://img.shields.io/github/contributors/smart-home-automation-system/ai-service?style=plastic)
![GitHub pull requests](https://img.shields.io/github/issues-pr-raw/smart-home-automation-system/ai-service?style=plastic)

![GitHub last commit](https://img.shields.io/github/last-commit/smart-home-automation-system/ai-service?style=plastic)
![GitHub commit activity](https://img.shields.io/github/commit-activity/m/smart-home-automation-system/ai-service?style=plastic)

---

# Description

AI integration for the smart-home-automation-system. It forwards a text prompt to
**OpenAI** through **Spring AI** (`ChatClient`, `spring-ai-starter-model-openai`) and
returns the model's reply. Reactive throughout (Spring WebFlux / Reactor) — the model is
called over Spring AI's non-blocking streaming API and the streamed chunks are aggregated
into a single response.

# Run locally

- Build: `mvn verify` (JDK 21).
- Ports: local profile `6004` (management `8004`); in the deployed `home` profile the
  service listens on `6200` and Actuator on `8200` like every service in the cluster. The
  ingress routes only 6200, so Actuator is reachable inside the cluster only — that is where
  the Kubernetes probes hit `/actuator/health/{readiness,liveness}` and Prometheus scrapes
  `/actuator/prometheus`.
- Requires an OpenAI API key via the `openai-token` property
  (e.g. `--openai-token=<key>` or an environment variable).

# API

Base path `/home/ai` (`spring.webflux.base-path`); external traffic reaches it through
`api-gateway-service`.

| Method | Path | Description |
|---|---|---|
| `POST` | `/home/ai` | Send a prompt as the request body (`text/plain`); returns the model's reply as `text/plain` (`200 OK`). `Content-Type: text/plain` is required (`415 Unsupported Media Type` otherwise); a missing body returns `400 Bad Request`. |
