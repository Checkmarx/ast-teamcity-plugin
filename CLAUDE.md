# CLAUDE.md — `checkmarx-ast-teamcity-plugin`

> **Maintenance notice:** Keep this file up to date with every architecture change, dependency upgrade,
> or operational procedure change. Out-of-date documentation is worse than none.
>
> **Version:** `1.1.0` | **Last updated:** `2026-05-05` | **Updated by:** `Checkmarx Integrations Team`

> **AI agent notice:** This file is the authoritative context document for Claude working in this
> repository. Read it fully before making any suggestions or code changes.

---

## Quick-Reference Card

| Attribute | Value |
|---|---|
| Service name | `checkmarx-ast-teamcity-plugin` |
| Status | Active |
| Owner team | Checkmarx Integrations Team |
| Primary contact | `<TEAM_SLACK_CHANNEL>` |
| On-call rotation | N/A — plugin distributed as ZIP; no running service |
| Source repo | https://github.com/Checkmarx/ast-teamcity-plugin |
| Primary language | Java 17 |
| Build tool | Maven 3.8+ (multi-module) |
| TeamCity API version | 2025.07 |
| Cloud provider | N/A — plugin runs inside customer-managed TeamCity |
| Production artifact | `build/target/checkmarx-ast-teamcity-plugin.zip` |
| Runbook | `<LINK_TO_RUNBOOK>` |
| Architecture diagram | `<LINK_TO_DIAGRAM>` |
| CI pipeline | https://github.com/Checkmarx/ast-teamcity-plugin/actions |

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Architecture](#2-architecture)
3. [Repository Structure](#3-repository-structure)
4. [Technology Stack](#4-technology-stack)
5. [Development Setup](#5-development-setup)
6. [Coding Standards](#6-coding-standards)
7. [Project Rules & Constraints](#7-project-rules--constraints)
8. [Testing Strategy](#8-testing-strategy)
9. [Known Issues & Limitations](#9-known-issues--limitations)
10. [Database Schema](#10-database-schema)
11. [External Integrations](#11-external-integrations)
12. [Deployment Information](#12-deployment-information)
13. [Performance Considerations](#13-performance-considerations)
14. [API / Endpoints / Interfaces](#14-api--endpoints--interfaces)
15. [Security & Access](#15-security--access)
16. [Logging & Observability](#16-logging--observability)
17. [Debugging & Troubleshooting](#17-debugging--troubleshooting)
18. [How Claude Should Assist](#18-how-claude-should-assist)
- [Appendix](#appendix)

---

## 1. Project Overview

### Purpose

`checkmarx-ast-teamcity-plugin` integrates [Checkmarx One](https://checkmarx.com) (AST — Application Security Testing) scanning directly into JetBrains TeamCity CI/CD pipelines. It is consumed by development teams who run TeamCity as their CI server and want security scan results embedded in the build lifecycle. The plugin serves as the bridge between TeamCity's build runner framework and the Checkmarx One CLI, managing credential injection, multi-platform CLI dispatch, scan orchestration, and report surfacing — all without requiring developers to write custom build scripts.

### Status

| Field | Detail |
|---|---|
| Lifecycle status | Active |
| Deprecation date | N/A |
| Replacement service | N/A |
| Migration guide | N/A |

### Ownership

| Role | Name / Handle | Contact |
|---|---|---|
| Engineering owner | `<NAME>` | `<EMAIL>` |
| Product owner | `<NAME>` | `<EMAIL>` |
| On-call team | Checkmarx Integrations Team | `<SLACK_CHANNEL>` |
| Security contact | `<NAME>` | `<EMAIL>` |

### SLA / SLO Targets

> This is a distributed plugin, not a running service. SLA/SLO targets below refer to the quality of the plugin artifact itself and the CI pipeline that produces it.

| Metric | Target |
|---|---|
| CI build pass rate | ≥ 99% on `main` branch |
| PR CI turnaround | < 10 minutes |
| CVE fix SLA (Critical) | Patch released within 5 business days |
| CVE fix SLA (High) | Patch released within 10 business days |
| Plugin compatibility | Always supports the latest TeamCity GA release |

---

## 2. Architecture

### High-Level Design

```
┌──────────────────────────────────────────────────────────────────┐
│                     TeamCity Server                              │
│                                                                  │
│  CheckmarxScanRunType          ← registers build runner type     │
│  CheckmarxAdminPageController  ← admin UI, encrypts secrets      │
│  CheckmarxAdminConfig          ← reads/writes .properties file   │
│  CheckmarxBuildStartContextProcessor ← injects global config     │
│  CheckmarxScanReportTab        ← renders HTML report artifact    │
└──────────────────────────┬───────────────────────────────────────┘
                           │  TeamCity internal messaging
                           │  (shared build parameters)
┌──────────────────────────▼───────────────────────────────────────┐
│                     TeamCity Build Agent                         │
│                                                                  │
│  CheckmarxBuildSessionFactory                                    │
│    └─ CheckmarxScanBuildSession (3-step pipeline)                │
│         ├─ CheckmarxVersionCommand    → cx version               │
│         ├─ CheckmarxScanCreateCommand → cx scan create ...       │
│         └─ CheckmarxResultsCommand   → cx results show ...       │
│                   ↓                                              │
│         Bundled CLI binary: src/runner/bin/2.0.0/cx[.exe|-mac]  │
└──────────────────────────┬───────────────────────────────────────┘
                           │  HTTPS (OAuth 2.0 Client Credentials)
                           ▼
┌──────────────────────────────────────────────────────────────────┐
│                  Checkmarx One Platform                          │
│                  (AST API — external SaaS)                       │
└──────────────────────────────────────────────────────────────────┘
```

### Key Components

| Component | Technology | Responsibility |
|---|---|---|
| `CheckmarxScanRunType` | TeamCity `RunType` | Registers runner type `checkmarxScan`; validates required params |
| `CheckmarxAdminConfig` | Java + `.properties` file | Persists global credentials to `checkmarx-ast-plugin.properties` |
| `CheckmarxAdminPageController` | Spring MVC `BaseFormXmlController` | Handles admin form POST; encrypts client secret via `RSACipher` + `EncryptUtil` |
| `CheckmarxBuildStartContextProcessor` | TeamCity `BuildStartContextProcessor` | Copies global config to shared build parameters before each build |
| `CheckmarxScanReportTab` | TeamCity `ViewLogTab` | Injects HTML report artifact as a tab on the build results page |
| `CheckmarxBuildSessionFactory` | TeamCity `MultiCommandBuildSessionFactory` | Creates the 3-command `CheckmarxScanBuildSession` per build |
| `CheckmarxScanBuildSession` | Java | Orchestrates version check → scan create → results fetch; collects artifacts |
| `CheckmarxBuildServiceAdapter` | Abstract Java class | Base class for all CLI commands; resolves CLI binary path, sets env vars |
| Checkmarx One CLI (`cx`) | Bundled binary (Git LFS) | Performs actual scan operations against the Checkmarx One platform |
| `PluginUtils` | Java utility class | Façade for encryption/decryption, configuration resolution, logging helpers |

### Data Flow

1. Developer configures a **Checkmarx AST Scan** build step in TeamCity (server-side UI served by `CheckmarxScanRunType`).
2. Admin optionally sets global credentials via the admin page (`CheckmarxAdminPageController`). Client secret is encrypted and stored in `checkmarx-ast-plugin.properties`.
3. A build triggers; `CheckmarxBuildStartContextProcessor` copies global config into the build's shared parameters.
4. The build agent receives the build; `CheckmarxBuildSessionFactory` creates a `CheckmarxScanBuildSession`.
5. **Step 1** — `CheckmarxVersionCommand` runs `cx version` to verify CLI availability.
6. **Step 2** — `CheckmarxScanCreateCommand` runs `cx scan create` with auth flags and `CX_CLIENT_SECRET` env var. Scan ID is extracted from stdout by `CheckmarxScanParamRetriever`.
7. **Step 3** — Unless `--async` is present in `ADDITIONAL_PARAMETERS`, `CheckmarxResultsCommand` runs `cx results show` to produce `Checkmarx_ast_report.html`.
8. The HTML report is published as a build artifact under `Checkmarx AST Scan/Checkmarx_ast_report.html`.
9. `CheckmarxScanReportTab` renders the artifact in a dedicated tab on the TeamCity build results page.

### Architecture Decision Records (ADRs)

| ADR | Title | Status | Link |
|---|---|---|---|
| ADR-001 | Credentials passed via env var, never CLI args | Accepted | See [Section 15 — Security & Access](#15-security--access) |
| ADR-002 | CLI binary bundled in plugin ZIP (Git LFS) | Accepted | Ensures hermetic builds; no external download at build time |
| ADR-003 | Separate classloader (`use-separate-classloader="true"`) | Accepted | Prevents classpath collisions with TeamCity's bundled Spring/Jackson |
| ADR-004 | Exclude `common-jackson` and `spring-security-oauth2` from all TC API deps | Accepted | CVE mitigation; see [Section 7 — Constraints](#7-project-rules--constraints) |

---

## 3. Repository Structure

```
checkmarx-ast-teamcity-plugin/
├── checkmarx-ast-teamcity-plugin-common/   # Shared models, constants, utilities, CLI runner registry
│   ├── src/main/java/                      # CheckmarxParams, PluginUtils, CheckmarxScanConfig, ...
│   └── src/test/java/                      # Unit tests for common classes
├── checkmarx-ast-teamcity-plugin-server/   # Server-side plugin (UI, config, runner type registration)
│   ├── src/main/java/                      # Spring MVC controllers, admin page, report tab
│   ├── src/main/resources/                 # Spring bean XML, JSP templates, i18n properties
│   └── src/assembly/server.xml             # Assembly descriptor for server JAR
├── checkmarx-ast-teamcity-plugin-agent/    # Agent-side plugin (scan execution, CLI invocation)
│   ├── src/main/java/                      # Build session factory, commands, CLI adapter
│   ├── src/runner/bin/2.0.0/               # Bundled CLI binaries (cx, cx.exe, cx-mac) — Git LFS
│   ├── src/assembly/agent.xml              # Assembly descriptor for agent plugin ZIP
│   └── src/assembly/runner.xml             # Assembly descriptor for runner (CLI binaries) ZIP
├── build/                                  # Final plugin ZIP assembly (installs into TeamCity)
│   └── src/assembly/plugin.xml             # Top-level assembly — combines server + agent + runner
├── coverage-report/                        # JaCoCo aggregate coverage report module
├── .github/workflows/ci.yml               # GitHub Actions CI pipeline
├── teamcity-plugin.xml                    # Root plugin descriptor (version substituted at build time)
├── pom.xml                                # Root Maven POM — version mgmt, CVE exclusions, build config
├── CLAUDE.md                              # AI agent context file (this file)
├── cloud.md                               # Operational cloud documentation
└── README.md                              # Project entry point
```

### Module Responsibilities

| Path | Responsibility |
|---|---|
| `checkmarx-ast-teamcity-plugin-common/` | Parameter name constants (`CheckmarxParams`), runner type string, config POJO, encryption utilities, CLI platform detection, scan ID extraction |
| `checkmarx-ast-teamcity-plugin-server/` | TeamCity runner type registration, admin UI controller, global config persistence, build context propagation, HTML report tab |
| `checkmarx-ast-teamcity-plugin-agent/` | CLI command execution (version, scan create, results), build session orchestration, scan cancellation, artifact collection |
| `build/` | Assembles the final installable `checkmarx-ast-teamcity-plugin.zip` from server, agent, and runner ZIPs |
| `coverage-report/` | Aggregates JaCoCo execution data from all modules into a single HTML report |
| `.github/workflows/` | GitHub Actions CI: build, test, SpotBugs, package, coverage badge |

---

## 4. Technology Stack

### Core

| Layer | Technology | Version | Notes |
|---|---|---|---|
| Language | Java | 17 | Minimum JDK 17 required; `--release 17` enforced by compiler plugin |
| Build tool | Maven | 3.8+ | Multi-module reactor build |
| Plugin framework | JetBrains TeamCity SDK | 2025.07 | `server-api`, `agent-api`, `tests-support` |
| Web framework | Spring MVC | 6.2.11 | `scope=provided` — supplied by TeamCity; never bundled |
| Spring Security | Spring Security | 6.5.9 | `scope=provided`; CVE-forced override in `dependencyManagement` |
| Container runtime | N/A | — | Plugin runs inside TeamCity JVM; no containerization |
| Orchestration | N/A | — | No Kubernetes/ECS; plugin lifecycle managed by TeamCity |

### Data

| Type | Technology | Version | Purpose |
|---|---|---|---|
| Configuration store | Java `.properties` file | — | Global admin credentials (`checkmarx-ast-plugin.properties`) in TeamCity config dir |
| Secret storage | TeamCity `EncryptUtil` / `RSACipher` | N/A (TC internal) | Scrambled client secret in properties file |
| Cache | None | — | No caching layer; each build reads fresh config |
| Message broker | None | — | TeamCity internal parameter passing only |

### Infrastructure & Observability

| Concern | Tool | Notes |
|---|---|---|
| CI/CD | GitHub Actions | `.github/workflows/ci.yml`; triggers on pull_request |
| Static analysis | SpotBugs 4.8.6.2 | Runs in CI; threshold=High; `failOnError=false` |
| Coverage | JaCoCo 0.8.13 | Aggregate report via `coverage-report` module |
| Dependency security | Dependabot | Scheduled + PR-based; SpotBugs step skipped for Dependabot PRs |
| Secrets manager | N/A | No cloud secrets manager; TeamCity's built-in `EncryptUtil` handles plugin secrets |
| Logging | TeamCity build log | Structured output via `BuildProgressLogger`; SLF4J on server side |
| Monitoring | N/A | Plugin has no running process to monitor |
| Tracing | N/A | No distributed tracing |
| Alerting | N/A | GitHub Actions failure notifications cover CI |

---

## 5. Development Setup

### Prerequisites

| Tool | Version | Install Guide |
|---|---|---|
| JDK | 17 | https://adoptium.net |
| Maven | 3.8+ | https://maven.apache.org/download.cgi |
| Git LFS | Latest | https://git-lfs.github.com — required for CLI binaries |
| TeamCity (optional) | 2025.07+ | https://www.jetbrains.com/teamcity/download — for manual plugin testing |

### First-Time Setup

```bash
# 1. Clone the repository
git clone https://github.com/Checkmarx/ast-teamcity-plugin.git
cd ast-teamcity-plugin

# 2. Initialize Git LFS (fetches CLI binaries from LFS storage)
git lfs install
git lfs pull

# 3. Verify Java and Maven versions
java -version    # must show 17.x
mvn -version     # must show 3.8+

# 4. Download all dependencies (TeamCity artifacts fetched automatically
#    from https://download.jetbrains.com/teamcity-repository)
mvn -B dependency:resolve

# 5. Build and run all tests
mvn -B verify

# 6. (Optional) Install the plugin into a local TeamCity instance
#    Upload build/target/checkmarx-ast-teamcity-plugin.zip via
#    TeamCity Administration → Plugins
```

### Common Commands

```bash
# Full build + unit tests + SpotBugs + JaCoCo coverage
mvn -B verify

# Package plugin ZIP only (skips verify/test)
mvn -B package

# Run tests for a single module
mvn -B test -pl checkmarx-ast-teamcity-plugin-agent
mvn -B test -pl checkmarx-ast-teamcity-plugin-server
mvn -B test -pl checkmarx-ast-teamcity-plugin-common

# Skip tests (emergency use only — never merge with this flag)
mvn -B package -DskipTests

# Force refresh of all SNAPSHOT dependencies
mvn -B verify -U

# Inspect resolved dependency tree for a module
mvn dependency:tree -pl checkmarx-ast-teamcity-plugin-agent

# Audit for vulnerable transitive dependencies
mvn dependency:tree -pl checkmarx-ast-teamcity-plugin-agent | grep -i jackson
mvn dependency:tree | grep -i spring-security
```

### Environment Configuration

> This is a TeamCity plugin — there are no runtime environment variables for the plugin process itself.
> Configuration is managed entirely through the TeamCity admin UI. The table below documents the
> **build parameters** that the plugin reads during a TeamCity build.

| Parameter | Scope | Required | Description |
|---|---|---|---|
| `USE_DEFAULT_SERVER` | Runner | No (default: `false`) | When `true`, overrides runner-level config with global admin config |
| `SERVER_URL` | Runner or Global | Yes (unless `USE_DEFAULT_SERVER=true`) | Checkmarx One server base URL |
| `AUTHENTICATION_URL` | Runner or Global | No | Separate auth server URL (if different from `SERVER_URL`) |
| `TENANT` | Runner or Global | No | Checkmarx One tenant identifier |
| `AST_CLIENT_ID` | Runner or Global | Yes | OAuth2 client ID |
| `AST_SECRET` (secure) | Runner or Global | Yes | OAuth2 client secret — stored scrambled via `EncryptUtil` |
| `PROJECT_NAME` | Runner | Yes | Checkmarx One project name for the scan |
| `BRANCH_NAME` | Runner | Yes | Branch name to associate with the scan |
| `ADDITIONAL_PARAMETERS` | Runner or Global | No | Extra CLI flags (e.g., `--async`, `--scan-types sast`) |
| `USE_GLOBAL_ADDITIONAL_PARAMETERS` | Runner | No | When `true`, uses global additional parameters instead of runner-level |

---

## 6. Coding Standards

### Style & Formatting

| Rule | Standard |
|---|---|
| Code formatter | No auto-formatter enforced; follow existing style manually |
| Indentation | 4 spaces (no tabs) |
| Max line length | ~120 characters (soft limit) |
| Imports | No wildcard imports |
| Javadoc | Required on all `public` methods and classes |
| Enforced in CI | SpotBugs (threshold=High) via `spotbugs-maven-plugin` 4.8.6.2 |
| Pre-commit hook | None configured |

### Naming Conventions

| Element | Convention | Example |
|---|---|---|
| Parameter name constants | `UPPER_SNAKE_CASE` in `CheckmarxParams` | `AST_CLIENT_ID` |
| Runner type / artifact name constants | `UPPER_SNAKE_CASE` in `CheckmarxScanRunnerConstants` | `RUNNER_TYPE = "checkmarxScan"` |
| Spring bean IDs | `camelCase`, descriptive noun | `checkmarxAdminConfig` |
| Classes | `PascalCase`, prefixed with `Checkmarx` | `CheckmarxScanBuildSession` |
| Test methods | `methodUnderTest_scenario_expectedResult` | `getConfiguration_useDefaultServer_returnsGlobalValues` |
| Feature branches | `feature/<ticket-id>-short-description` | `feature/AST-123-add-timeout-param` |
| CVE / security branches | `vulnerability/<name>-<ticket-id>` | `vulnerability/spring_security_AST-142710` |
| Bug fix branches | `fix/<ticket-id>-short-description` | `fix/AST-456-fix-null-scan-id` |
| Environment variables (runtime) | `UPPER_SNAKE_CASE` | `CX_CLIENT_SECRET` |

### Documentation Expectations

- **Public APIs:** all `public` methods must have Javadoc with `@param`, `@return`, and `@throws` tags.
- **Complex business logic:** inline comments explaining *why* the code does something, not *what* it does.
- **Non-obvious architectural decisions:** update ADR table in Section 2 and commit rationale in the PR description.
- **TODO / FIXME:** must include a Jira ticket reference, e.g., `// TODO AST-789: replace hardcoded version`. Never leave unlinked TODOs.
- **New CLI parameters:** follow the end-to-end checklist in [Section 7 — Constraints](#7-project-rules--constraints).

### Branching Strategy

| Branch | Purpose | Protection rules |
|---|---|---|
| `main` | Production-ready, tagged releases | Requires PR + passing CI + ≥ 1 approval |
| `feature/<ticket-id>-*` | New feature development | PR required |
| `fix/<ticket-id>-*` | Bug fixes | PR required |
| `vulnerability/<name>-<ticket-id>` | CVE / security patches | PR required; security review recommended |
| `release/<version>` | Release preparation | Restricted push |

---

## 7. Project Rules & Constraints

### ❌ Never Do

- **Never commit secrets** (API keys, client secrets, passwords, certificates) to the repository.
- **Never add the client secret to CLI command-line arguments** — it must travel exclusively as the `CX_CLIENT_SECRET` environment variable.
- **Never log the client secret** — not even as a masked or truncated string.
- **Never bundle Spring, Jackson, or Jakarta Servlet JARs with `compile` scope** — they must be `provided`; TeamCity supplies them at runtime.
- **Never pin a Jackson artifact version** without first confirming that exact `groupId:artifactId:version` triple exists on Maven Central. `jackson-annotations` does not always share the same version tag as `jackson-core`.
- **Never add a new TeamCity API dependency** (`server-api`, `agent-api`, `tests-support`) without auditing its transitive graph for `common-jackson` and `spring-security-oauth2` and applying the corresponding exclusions.
- **Never modify an existing LFS-tracked CLI binary** — always add new versions to a new versioned directory under `src/runner/bin/<version>/`.
- **Never bypass CI** with `--no-verify` or force-pushes to `main`.
- **Never write tests that depend on actual CLI binary execution or live network calls** — mock everything.

### Architectural Constraints

| Constraint | Reason |
|---|---|
| Plugin deployed with `use-separate-classloader="true"` | Prevents classpath collisions with TeamCity's bundled Spring, Jackson, and other shared libraries |
| `spring-security-oauth2` excluded from all TeamCity API deps | EOL artifact; its transitive pull of `spring-security-web < 6.5.9` triggers CVE-2026-22732 |
| `common-jackson` excluded from all TeamCity API deps in all modules | Prevents vulnerable `jackson-core @ 2.19.0` from entering the classpath via `web-openapi → jackson-datatype-jdk8` |
| Jackson artifacts must NOT be version-pinned in `dependencyManagement` | `jackson-annotations` publishes independently from `jackson-core`; cross-version pins cause artifact-not-found failures |
| `commons-lang3` pinned to `≥ 3.18.0` | Fixes `ClassUtils.getClass()` uncontrolled recursion DoS |
| `commons-text` pinned to `≥ 1.15.0` | Transitively brings safe `commons-lang3 @ 3.20.0`; removes vulnerable declared path from scanner reports |
| CLI binary version managed via `Runners.DEFAULT_VERSION` constant | Single source of truth; changing requires both code update and new binary in Git LFS |
| All CLI credentials passed via environment variables | Defense against secret leakage in process argument lists and build logs |

### New CLI Parameter Checklist

When adding a new configuration parameter to the plugin, touch all 6 of these in a single PR:

1. Add the constant to `CheckmarxParams.java`.
2. Add the JSP input field to `editCheckmarxScanRunnerParameters.jsp` (and display view in `viewCheckmarxScanRunnerParameters.jsp`).
3. Update `CheckmarxScanConfig.java` to expose a getter.
4. Update `CheckmarxScanCreateCommand.java` (or the relevant command class) to append the flag.
5. Update `CheckmarxScanRunType.getDefaultRunnerProperties()` / `describeParameters()`.
6. Add a unit test in `CheckmarxScanCreateCommandTest.java`.

### Compliance & Regulatory

| Regulation / Standard | Scope | Owner |
|---|---|---|
| Checkmarx internal secure-coding standards | All production code | Engineering owner |
| CVE response policy | Dependency vulnerabilities → patch within 5 days (Critical) / 10 days (High) | Checkmarx Integrations Team |
| No PII in logs | Build logs must not contain customer credentials or scan data | Engineering owner |

---

## 8. Testing Strategy

### Coverage Requirements

| Scope | Target | Enforced in CI |
|---|---|---|
| Unit tests | Measured via JaCoCo aggregate; target ≥ 70% line coverage | Yes — JaCoCo report generated on every `mvn verify` |
| Integration tests | None currently — no Testcontainers or embedded TeamCity | No |
| E2E / smoke tests | Manual — requires live TeamCity + Checkmarx One environment | No |

### Test Types & Tooling

| Test type | Framework / Tool | Location | When it runs |
|---|---|---|---|
| Unit | JUnit Jupiter 5.10.2 + Mockito 5.11.0 | `*/src/test/java/` | Every PR, every `mvn verify` |
| Integration | None (N/A) | — | — |
| E2E / smoke | Manual via live TeamCity | — | Pre-release, manual only |
| SAST | SpotBugs 4.8.6.2 | CI pipeline | Every PR (skipped for Dependabot) |
| SCA | Dependabot | GitHub | Scheduled + every PR |

### Test Class Map

```
checkmarx-ast-teamcity-plugin-common/src/test/java/
├── CheckmarxScanConfigTest.java             ← Param parsing, additional-params tokenizer
├── CheckmarxScanParamRetrieverTest.java     ← Scan ID regex extraction from CLI stdout
├── CheckmarxScanCancelCommandExecutorTest.java ← Cancel command construction
└── PluginUtilsTest.java                     ← Encryption/decryption, config resolution logic

checkmarx-ast-teamcity-plugin-agent/src/test/java/
├── CheckmarxBuildSessionFactoryTest.java    ← Session factory instantiation
├── CheckmarxScanBuildSessionTest.java       ← 3-step orchestration with mocked commands
├── CommandExecutionAdapterTest.java         ← Command execution and cancellation
├── CheckmarxVersionCommandTest.java         ← `cx version` invocation
└── CheckmarxScanCreateCommandTest.java      ← Auth flags, env vars, CLI argument construction

checkmarx-ast-teamcity-plugin-server/src/test/java/
├── CheckmarxScanRunTypeTest.java            ← Runner validation (required fields)
└── CheckmarxOptionsTest.java               ← Options helper getters
```

### Conventions

- Use `@ExtendWith(MockitoExtension.class)` — never create `MockitoSession` or `Mockito.openMocks()` manually.
- Mock TeamCity API interfaces (`BuildRunnerContext`, `AgentRunningBuild`, `BuildProgressLogger`, `BuildAgent`) — never instantiate real TeamCity implementations.
- Assert CLI command arguments by capturing the `List<String>` passed to `ProcessBuilder` or by verifying `addArgs()` / `buildCommand()` call interactions on mocks.
- Tests must be **deterministic** — no real-time dependency, no network calls, no file system side effects outside temp directories.
- Every bug fix must be accompanied by a regression test that would have caught the original bug.
- Coverage report is available at: `coverage-report/target/site/jacoco-aggregate/index.html`

---

## 9. Known Issues & Limitations

### Active Issues

| ID | Title | Severity | Workaround | Tracking |
|---|---|---|---|---|
| — | No active known bugs at time of writing | — | — | — |

### Technical Debt

| Area | Description | Estimated effort | Priority | Tracking |
|---|---|---|---|---|
| Mockito version split | Agent module uses Mockito 5.8.0; server/common use 5.11.0 — inconsistent test classpath | < 1 day | Low | `<TICKET>` |
| Hardcoded CLI version | `Runners.DEFAULT_VERSION = "2.0.0"` is hardcoded — upgrading requires code change + new LFS binary | 1 day | Medium | `<TICKET>` |
| No integration tests | No automated test against a real TeamCity or real Checkmarx One instance | 2+ sprints | Medium | `<TICKET>` |
| Admin controller adapter layer | `CheckmarxAdminPageController` contains a Jakarta Servlet / Spring 6.x backward-compatibility shim — fragile on TC upgrades | 3 days | Medium | `<TICKET>` |

### Operational Constraints

- **Async scan support:** If `--async` is present in `ADDITIONAL_PARAMETERS`, the results-fetch step is skipped entirely. Builds complete immediately after scan submission — no poll-and-wait mechanism exists.
- **Single CLI binary per agent:** Multiple concurrent builds on the same agent share the same CLI binary unpacked to the agent tools directory. No per-build isolation of the binary.
- **Git LFS dependency:** CLI binaries are tracked via Git LFS. CI must have `lfs: true` on checkout. Developers need `git lfs install` and `git lfs pull` after cloning.
- **Spring classloader boundary:** Because the plugin uses `use-separate-classloader="true"`, any Spring bean it exposes to TeamCity server internals must be compatible with the interface version expected by TeamCity 2025.07.

---

## 10. Database Schema

> **Not applicable.** This plugin does not manage its own database. The only persistent storage is a single flat properties file.

### Configuration File

| Attribute | Detail |
|---|---|
| File | `checkmarx-ast-plugin.properties` |
| Location | TeamCity server config directory (`${teamcity.data.path}/config/`) |
| Format | Java `.properties` (key=value) |
| Managed by | `CheckmarxAdminConfig.java` |
| Encryption at rest | Client secret is scrambled via `EncryptUtil.scramble()` before writing |
| Backup | TeamCity's own config backup covers this file |
| Migration | No migration tooling needed — flat key-value file is schema-free |

### Properties Keys

| Key | Encrypted | Description |
|---|---|---|
| `GLOBAL_AST_SERVER_URL` | No | Checkmarx One server base URL |
| `GLOBAL_AST_AUTHENTICATION_URL` | No | Auth server URL (optional) |
| `GLOBAL_AST_TENANT` | No | Tenant identifier |
| `GLOBAL_AST_CLIENT_ID` | No | OAuth2 client ID |
| `GLOBAL_AST_SECRET` | **Yes** (EncryptUtil scrambled) | OAuth2 client secret |
| `GLOBAL_ADDITIONAL_PARAMETERS` | No | Extra CLI flags applied globally |

---

## 11. External Integrations

### Dependency Map

| Service | Type | Protocol | Auth method | Rate limit | Fallback |
|---|---|---|---|---|---|
| Checkmarx One (AST Platform) | SaaS security scan engine | HTTPS REST (via CLI) | OAuth 2.0 Client Credentials | Per-tenant (Checkmarx SLA) | None — hard dependency; build fails if unreachable |
| JetBrains TeamCity | Host platform (server + agent SDK) | In-process Java API | N/A — plugin runs inside TC JVM | N/A | N/A — host platform |
| JetBrains Maven repository | Dependency source | HTTPS | None (public) | None | Maven Central fallback |
| GitHub Actions | CI runner | HTTPS | `github.token` / `PERSONAL_ACCESS_TOKEN` | GitHub Actions limits | N/A |

### Integration Details

#### Checkmarx One Platform

| Attribute | Value |
|---|---|
| Purpose | Runs SAST/SCA/IaC security scans against the project source code |
| Communication channel | Checkmarx One CLI (`cx`) — the plugin never calls the REST API directly |
| Auth mechanism | OAuth 2.0 Client Credentials: `--client-id` flag + `CX_CLIENT_SECRET` env var |
| Base URL config param | `SERVER_URL` / `GLOBAL_AST_SERVER_URL` |
| Auth URL config param | `AUTHENTICATION_URL` / `GLOBAL_AST_AUTHENTICATION_URL` (optional) |
| Timeout | Determined by Checkmarx One scan duration — no fixed timeout in plugin |
| Retry policy | None built into plugin; rely on CLI retry behaviour |
| Circuit breaker | None — build fails if CLI exits non-zero |
| CLI documentation | https://checkmarx.com/resource/documents/en/34965-68621-checkmarx-one-cli-tool.html |

---

## 12. Deployment Information

### Environments

| Environment | Description | How to deploy | Approval required |
|---|---|---|---|
| Local developer | Run `mvn verify`, install resulting ZIP via TeamCity admin UI | Manual — copy `build/target/*.zip` | No |
| CI (PR validation) | GitHub Actions — build + test only; no deployment | Automatic on PR open/push | No |
| Production (customer) | Customer installs ZIP on their TeamCity instance | ZIP published with GitHub release | Yes — release tag + changelog review |

### CI/CD Pipeline

```
PR open / push
  │
  ├─► Checkout (with LFS) — actions/checkout@v4.1.7 lfs:true
  ├─► Set up JDK 17 — actions/setup-java@v4 (Adopt)
  ├─► Restore Maven cache — actions/cache@v4 keyed on **/pom.xml hash
  ├─► Install xmllint (sudo apt-get install libxml2-utils)
  ├─► mvn -B verify  (compile + unit tests + SpotBugs + JaCoCo)
  ├─► SpotBugs GitHub Action (skipped for Dependabot PRs)
  ├─► mvn -B package (produces plugin ZIP)
  ├─► Upload coverage-report artifact — actions/upload-artifact@v4
  └─► Generate JaCoCo badge — cicirello/jacoco-badge-generator
```

### CI/CD Configuration

| Attribute | Value |
|---|---|
| CI/CD platform | GitHub Actions |
| Pipeline file | `.github/workflows/ci.yml` |
| Trigger | `on: [pull_request]` |
| Artifact produced | `build/target/checkmarx-ast-teamcity-plugin.zip` |
| Coverage report artifact | `coverage-report/target/site/jacoco-aggregate/index.html` |
| Container registry | N/A — no Docker images |
| IaC tool | N/A |

### Releasing a New Plugin Version

1. Update `<version>` in root `pom.xml` to the new release version.
2. Tag the commit: `git tag -a vX.Y.Z -m "Release X.Y.Z"`.
3. Create a GitHub release; attach the `checkmarx-ast-teamcity-plugin.zip` artifact.
4. Update the changelog in `README.md`.

### Releasing a New CLI Version

1. Add new CLI binaries to `checkmarx-ast-teamcity-plugin-agent/src/runner/bin/<new-version>/` — commit via Git LFS.
2. Update `Runners.DEFAULT_VERSION` in `checkmarx-ast-teamcity-plugin-common`.
3. Update `src/assembly/runner.xml` if the directory name changes.
4. Run `mvn verify` locally to confirm the new binary is packaged and the version command succeeds.

### Feature Flags

> No feature flag system is implemented. Behaviour is controlled by build parameters (see Section 5 — Environment Configuration) and the presence of flags in `ADDITIONAL_PARAMETERS`.

---

## 13. Performance Considerations

### Scaling Strategy

> This is a TeamCity plugin — it does not scale independently. Performance concerns are scoped to:
> 1. Plugin overhead within a TeamCity build (JVM startup, CLI process spawning)
> 2. Checkmarx One scan duration (external; not controllable by this plugin)

| Dimension | Approach |
|---|---|
| Concurrent builds | TeamCity manages build agent concurrency. Plugin is stateless per build — no shared mutable state between concurrent builds on different agents. |
| CLI process isolation | Each `CommandExecutionAdapter` spawns its own `ProcessBuilder` process. No connection pooling or CLI process reuse. |
| Async scans | Set `--async` in `ADDITIONAL_PARAMETERS` to skip waiting for results. Scan is submitted and build completes immediately. |

### Known Bottlenecks

| Area | Description | Current mitigation | Long-term fix |
|---|---|---|---|
| Scan duration | Checkmarx One scans can take minutes to hours depending on project size | `--async` flag available | N/A — external service |
| CLI cold start | Each build spawns a new CLI process with JVM startup overhead | Acceptable — runs once per build | N/A |
| Single binary per agent | All concurrent builds on the same agent use the same CLI binary | Read-only binary access; no conflict in practice | Per-build CLI isolation |

### Optimization Guidelines

- Use `--async` in `ADDITIONAL_PARAMETERS` for builds where scan results are not needed for pass/fail gate logic.
- Keep `ADDITIONAL_PARAMETERS` values tightly scoped — every extra flag is forwarded verbatim to the CLI.
- Do not add synchronous network calls to the plugin's server-side code path (admin page save, build start context processor) — these run on TeamCity's main server thread.

---

## 14. API / Endpoints / Interfaces

> This is a TeamCity **plugin**, not an HTTP service. It exposes no public REST API. The interfaces below are internal to the TeamCity plugin framework.

### TeamCity Extension Points Implemented

| Interface | Implementation | Purpose |
|---|---|---|
| `jetbrains.buildServer.serverSide.RunType` | `CheckmarxScanRunType` | Registers the `checkmarxScan` runner type in TeamCity |
| `jetbrains.buildServer.controllers.BaseFormXmlController` | `CheckmarxAdminPageController` | Handles admin configuration form POST |
| `jetbrains.buildServer.serverSide.BuildStartContextProcessor` | `CheckmarxBuildStartContextProcessor` | Injects global config as shared build params |
| `jetbrains.buildServer.web.openapi.ViewLogTab` | `CheckmarxScanReportTab` | Renders HTML artifact as build results tab |
| `jetbrains.buildServer.agent.MultiCommandBuildSessionFactory` | `CheckmarxBuildSessionFactory` | Creates the agent-side build session |
| `jetbrains.buildServer.agent.BuildCommandLineProcessor` | `CheckmarxBuildServiceAdapter` (abstract) | Base class for CLI command construction |

### Admin Controller Endpoint

| Attribute | Value |
|---|---|
| Method | `POST` |
| Path | Registered via Spring bean in `build-server-plugin-checkmarx-ast-teamcity-plugin.xml` |
| Auth | TeamCity admin session (enforced by TeamCity framework) |
| Request body | HTML form with global config fields |
| Response | XML (Spring MVC `ModelAndView` → TeamCity admin page) |
| Side effect | Writes encrypted credentials to `checkmarx-ast-plugin.properties` |

### Runner Type Registration

| Attribute | Value |
|---|---|
| Runner type string | `checkmarxScan` |
| Display name | `Checkmarx AST Scan` |
| Description | `Build Runner to scan the source code with Checkmarx AST engine.` |
| Required params | `SERVER_URL` (unless `USE_DEFAULT_SERVER=true`), `BRANCH_NAME` |
| Validation | `CheckmarxScanRunType.getRunnerPropertiesProcessor()` returns `InvalidProperty` errors |

---

## 15. Security & Access

### Authentication

| Mechanism | Used for | Token lifetime | Notes |
|---|---|---|---|
| OAuth 2.0 Client Credentials | CLI → Checkmarx One API | Short-lived (managed by CLI) | `--client-id` + `CX_CLIENT_SECRET` env var |
| TeamCity `EncryptUtil` (RSA + AES scramble) | Storing client secret in `.properties` file | At rest, until rotated | `RSACipher.decryptWebRequestData()` on form submit; `EncryptUtil.scramble()` for storage |
| TeamCity session authentication | Admin page access | TeamCity session lifetime | Enforced by TeamCity framework; no plugin-level auth needed |

### Credential Flow

```
Admin saves global config
  → CheckmarxAdminPageController.ensurePasswordEncryption()
       RSACipher.decryptWebRequestData(rawSecret)   → plaintext
       PluginUtils.encrypt(plaintext)
         → EncryptUtil.scramble(plaintext)           → scrambled string
       Written to checkmarx-ast-plugin.properties

Build executes on agent
  → PluginUtils.decrypt(scrambled)
       → EncryptUtil.unscramble(scrambled)           → plaintext
  → CheckmarxScanCreateCommand sets:
       env["CX_CLIENT_SECRET"] = plaintext
  → CLI reads CX_CLIENT_SECRET at runtime
  → Secret NEVER appears in process argument list or build log
```

### Parameter Security

| Parameter | Scope | Storage |
|---|---|---|
| `AST_CLIENT_ID` | Runner or global | Properties file — plaintext (not sensitive) |
| `AST_SECRET` / `GLOBAL_AST_SECRET` | Runner or global | Properties file — scrambled via `EncryptUtil.scramble()` |
| `CX_CLIENT_SECRET` | Runtime only | Process environment variable — never persisted |

### Secrets Management

| Secret | Stored in | Rotation | Who can access |
|---|---|---|---|
| OAuth2 client secret | `checkmarx-ast-plugin.properties` (scrambled) | Manual — via TeamCity admin page | TeamCity server admin |
| CI `PERSONAL_ACCESS_TOKEN` (optional) | GitHub repo secrets | Manual rotation | Repository admins |

### Security CVE Mitigations Active in This Codebase

| CVE / Issue | Affected component | Mitigation applied |
|---|---|---|
| CVE-2026-22732 — `spring-security-web < 6.5.9` | Via `spring-security-oauth2` transitively through TeamCity API | `spring-security-oauth2` excluded from `server-api`, `agent-api`, `tests-support` in all modules; `springSecurity.version=6.5.9` forced |
| `jackson-core 2.19.0` async-parser DoS | Via `common-jackson → jackson-datatype-jdk8` through TeamCity API | `common-jackson` excluded from `server-api`, `agent-api`, `tests-support` in all modules |
| `commons-lang3 < 3.18.0` uncontrolled recursion DoS | Via `commons-text 1.13.1` | `commons-lang3` pinned to `3.20.0`; `commons-text` upgraded to `1.15.0` |

**Rule for new TeamCity API dependencies:** always run `mvn dependency:tree -pl <module>` and check for `common-jackson` and `spring-security-oauth2` in the tree before adding any new `server-api`, `agent-api`, or `tests-support` dependency. Apply matching exclusions if found.

---

## 16. Logging & Observability

### Logging

| Attribute | Value |
|---|---|
| Agent log mechanism | `BuildProgressLogger` (TeamCity API) — output appears in the TeamCity build log |
| Server log mechanism | SLF4J `LoggerFactory.getLogger(Foo.class)` — written to TeamCity server log |
| Log format | Plain text (TeamCity formats and displays it) |
| Log levels used | `INFO` (user-visible progress), `DEBUG` (internal diagnostics), `ERROR` (failures) |
| Log aggregation | TeamCity's own build log storage and UI |
| Log retention | Governed by TeamCity server configuration (not the plugin) |

**Rules:**
- **Never log** the client secret, even partially or masked.
- **Never log** customer source code paths or scan data beyond what the CLI outputs.
- Progress messages logged via `BuildProgressLogger.message()` are visible to all TeamCity users with build view access — keep them informational and safe.

### Build Artifacts Published Per Build

| Artifact | Path in TeamCity artifacts | Notes |
|---|---|---|
| HTML scan report | `Checkmarx AST Scan/Checkmarx_ast_report.html` | Rendered in the Checkmarx AST Scan tab |
| Raw scan log | `checkmarxASTScan.txt` (temp dir) | Not published to artifacts; used internally by `CheckmarxScanParamRetriever` to extract scan ID |

### Report Tab Visibility

`CheckmarxScanReportTab` registers the **Checkmarx AST Scan** tab on the build results page. The tab is visible only when:
- The build type includes a `checkmarxScan` runner step.
- The build has completed (not currently running).
- The `Checkmarx AST Scan/Checkmarx_ast_report.html` artifact exists.

### Observability Gaps

- No distributed tracing — the plugin does not propagate trace IDs to the Checkmarx One platform.
- No custom metrics — build duration and pass/fail rates are tracked by TeamCity's built-in reporting.
- No health endpoint — the plugin has no HTTP server to probe.

---

## 17. Debugging & Troubleshooting

### Common Issues

| Symptom | Probable cause | Resolution steps |
|---|---|---|
| `cx: command not found` or CLI exits non-zero immediately | Git LFS not initialized; binary not extracted to agent tools dir | 1. Run `git lfs pull` in repo. 2. Verify `src/runner/bin/2.0.0/cx` is not an LFS pointer file. 3. Check agent tools dir for the binary. |
| `401 Unauthorized` from Checkmarx One | Invalid or expired client credentials | 1. Verify `AST_CLIENT_ID` and `AST_SECRET` in admin config. 2. Re-save admin config to re-encrypt secret. 3. Test credentials with the CLI manually. |
| `Could not resolve dependencies: jackson-annotations:jar:X.Y.Z not found` | Jackson version pinned in `dependencyManagement` for a version that doesn't exist | Remove all jackson `dependencyManagement` entries and `jackson.version` property; add `common-jackson` exclusion to all affected TC API deps. |
| Build hangs indefinitely | Scan running in synchronous mode on a very large project | Add `--async` to `ADDITIONAL_PARAMETERS`, or increase TeamCity build timeout. |
| Checkmarx AST Scan tab missing on build results | HTML report not produced (async mode, or CLI failed before results step) | 1. Check build log for errors in `cx results show` step. 2. Verify `--async` is not in `ADDITIONAL_PARAMETERS` if results are expected. |
| `ClassNotFoundException` or `NoSuchMethodError` at runtime | Spring / Jackson version mismatch between plugin and TeamCity internals | Verify all Spring/Jackson/Servlet deps are `scope=provided`. Check exclusions are applied to TC API deps. |
| Scan ID not captured; results step skipped | `CheckmarxScanParamRetriever` regex failed to match CLI output format | Check CLI version output format against the regex in `CheckmarxScanParamRetriever`. May need update after CLI upgrade. |
| Secret visible in build log | `CX_CLIENT_SECRET` inadvertently logged | Grep build log and CLI invocation for `--client-secret`. Ensure only env var path is used. File security incident if exposed. |

### Useful Debug Commands

```bash
# Verify CLI binary is present and executable (Linux/macOS)
ls -la checkmarx-ast-teamcity-plugin-agent/src/runner/bin/2.0.0/
file  checkmarx-ast-teamcity-plugin-agent/src/runner/bin/2.0.0/cx

# Check if file is an LFS pointer (should NOT be for a proper checkout)
git lfs ls-files | grep cx

# Inspect resolved dependency tree for a module (check for CVE transitive deps)
mvn dependency:tree -pl checkmarx-ast-teamcity-plugin-agent | grep -E "(jackson|spring-security|oauth2|common-jackson)"

# Run a single test class
mvn -B test -pl checkmarx-ast-teamcity-plugin-agent \
    -Dtest=CheckmarxScanCreateCommandTest

# Run build with full SpotBugs output
mvn -B verify -Dspotbugs.threshold=Low

# Check which version of a transitive dep is actually resolved
mvn dependency:tree -pl checkmarx-ast-teamcity-plugin-server \
    -Dincludes=org.springframework.security:spring-security-web
```

### Escalation Path

| Severity | Initial responder | Escalation |
|---|---|---|
| P1 — Security vulnerability in released plugin | Security contact (see Section 1 — Ownership) | Engineering owner within 1 business day |
| P2 — Plugin breaks TeamCity builds for customers | Checkmarx Integrations Team Slack channel | Engineering owner within 1 business day |
| P3 — CI pipeline failure | Author of the failing PR | Engineering owner if unresolved within 2 business days |
| P4 — Technical debt / non-critical bug | File Jira ticket; triage in next sprint planning | — |

---

## 18. How Claude Should Assist

### Allowed Actions

- Read any source file, test, POM, resource descriptor, or workflow file in the repository.
- Suggest code changes as **complete, compilable snippets** — never partial fragments without context.
- Add new `CheckmarxParams` constants, JSP inputs, config model getters, CLI flag appenders, runner type entries, and unit tests following the **new CLI parameter checklist** in Section 7.
- Write or improve unit tests adhering to the Mockito + JUnit 5 conventions in Section 8.
- Propose dependency updates with CVE justification; **always verify the exact `groupId:artifactId:version` exists on Maven Central** before suggesting a version.
- Diagnose Maven dependency issues by reasoning through `mvn dependency:tree` output before proposing exclusions or version pins.
- Update this `CLAUDE.md` when project facts change (new classes, new CVE mitigations, new constraints).

### What Claude Should Never Do

- Add the client secret to CLI command-line arguments or log output — not even in test stubs.
- Bundle Spring, Jackson, or Servlet JARs with `compile` scope.
- Pin a Jackson version in `dependencyManagement` without verifying every artifact in the group publishes that exact version.
- Add a TeamCity API dependency without checking its transitive tree for `common-jackson` and `spring-security-oauth2`.
- Modify existing LFS-tracked CLI binaries — always add new versions to a new versioned directory.
- Write tests with real network calls, real file system side effects, or hardcoded sleep delays.

### Preferred Interaction Style

- When suggesting a code change, show the **full method or class** being modified — partial snippets cause merge errors.
- When diagnosing a Maven dependency issue, walk through the resolved vs. declared graph distinction before proposing a fix.
- When adding a new configuration parameter, follow the **end-to-end 6-step checklist** in Section 7.
- Flag security-sensitive changes (secrets, encryption, CLI invocation, new dependencies) explicitly and explain the threat model impact.
- Reference the relevant Jira ticket (e.g., `AST-XXXXXX`) in PR descriptions, commit messages, and inline comments.

---

## Appendix

### Glossary

| Term | Definition |
|---|---|
| AST | Application Security Testing — umbrella term for Checkmarx One scanning capabilities (SAST, SCA, IaC, etc.) |
| Runner type | TeamCity concept for a pluggable build step executor; identified by a string key (`checkmarxScan`) |
| Build session | A sequence of `CommandExecution` objects that TeamCity agent executes in order for one build step |
| `common-jackson` | TeamCity-internal artifact that bundles Jackson; not published to Maven Central; excluded to prevent bringing in vulnerable `jackson-core @ 2.19.0` |
| LFS | Git Large File Storage — used to track CLI binaries that exceed normal git object size limits |
| EncryptUtil | TeamCity internal class for scrambling/unscrambling secrets stored in `.properties` files |
| CVE | Common Vulnerabilities and Exposures — security vulnerability identifier |
| SCA | Software Composition Analysis — scanning for vulnerable open-source dependencies |
| SAST | Static Application Security Testing — scanning source code for security flaws |

### Useful Links

| Resource | Link |
|---|---|
| Source repository | https://github.com/Checkmarx/ast-teamcity-plugin |
| CI pipeline | https://github.com/Checkmarx/ast-teamcity-plugin/actions |
| Checkmarx One CLI documentation | https://checkmarx.com/resource/documents/en/34965-68621-checkmarx-one-cli-tool.html |
| TeamCity plugin development guide | https://plugins.jetbrains.com/docs/teamcity/developing-teamcity-plugins.html |
| TeamCity runner type API | https://javadoc.io/doc/org.jetbrains.teamcity/server-api |
| JetBrains Maven repository | https://download.jetbrains.com/teamcity-repository |
| Maven Central (dependency verification) | https://central.sonatype.com |
| Jira / issue tracker | `<LINK_TO_JIRA>` |
| Confluence / wiki | `<LINK_TO_CONFLUENCE>` |
| Slack channel | `<SLACK_CHANNEL>` |

### Changelog

| Version | Date | Author | Summary |
|---|---|---|---|
| `1.0.0` | `2026-05-05` | Checkmarx Integrations Team | Initial CLAUDE.md — 12-section agent context doc |
| `1.1.0` | `2026-05-05` | Checkmarx Integrations Team | Restructured to 17-section `claude-md-template.md` format; added Quick-Reference Card, Technology Stack, External Integrations, Performance, API Interfaces, Debugging sections; retained all existing agent guidance in Section 18 |
