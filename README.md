<img src="https://raw.githubusercontent.com/Checkmarx/ci-cd-integrations/main/.images/PluginBanner.jpg">
<br />
<div align="center">
  
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![Apache License][license-shield]][license-url]

</div>
<br />
<p align="center">
  <a href="https://github.com/Checkmarx/ast-teamcity-plugin">
    <img src="https://raw.githubusercontent.com/Checkmarx/ci-cd-integrations/main/.images/cx-icon-logo.svg" alt="Logo" width="80" height="80" />
  </a>

<h3 align="center">AST-TEAMCITY-PLUGIN</h3>

<p align="center">
    <a href="https://checkmarx.com/resource/documents/en/34965-68696-checkmarx-one-teamcity-plugin.html"><strong>Explore the docs »</strong></a>
    <br />
    <a href="https://plugins.jetbrains.com/plugin/17610-checkmarx-ast"><strong>Marketplace »</strong></a>
    <br />
    <br />
    <a href="https://github.com/checkmarx/ast-teamcity-plugin/issues/new">Report Bug</a>
    ·
    <a href="https://github.com/checkmarx/ast-teamcity-plugin/issues/new">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#overview">Overview</a>
    </li>
    <li>
      <a href="#main-features">Main Features</a>
    </li>
    <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#initial-setup">Initial Setup</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
        <li><a href="#feedback">Feedback</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- OVERVIEW -->
# Overview

The Checkmarx One (AST) TeamCity plugin enables you to integrate the full functionality of the Checkmarx One platform into your TeamCity projects. You can use this plugin to trigger Checkmarx One scans as part of your CI/CD integration.
> This plugin is used with the Checkmarx One platform. If you are using the CxSAST and/or CxSCA standalone products, then you need to install the [Checkmarx](https://plugins.jetbrains.com/plugin/10101-checkmarx) plugin.
This plugin provides a wrapper around the [Checkmarx One CLI Tool](urn:resource:component:67829) which creates a zip archive from your source code repository and uploads it to Checkmarx One for scanning. This provides easy integration with TeamCity while enabling scan customization using the full functionality and flexibility of the CLI tool.
The plugin code can be found [here](https://github.com/Checkmarx/ast-teamcity-plugin/releases).

## Main Features

-   Configure TeamCity projects to automatically trigger scans running all Checkmarx One scanners: CxSAST, CxSCA, IaC Security, Container Security, API Security, Secret Detection and Repository Health (OSSF Scorecard).
-   Supports use of CLI arguments to customize scan configuration,
    enabling you to:
    -   Customize filters to specify which folders and files are scanned
    -   Apply preset query configurations
    -   Customize SCA scans using [SCA Resolver](https://checkmarx.com/resource/documents/en/34965-19196-checkmarx-sca-resolver.html)
    -   Set thresholds to break build
-   Send requests via a proxy server
-   Break build upon policy violation
-   View scan results summary and trends in the TeamCity environment
-   Direct links from within TeamCity to detailed Checkmarx One scan results
-   Generate customized scan reports in various formats (JSON, HTML, PDF etc.)
-   Generate SBOM reports (CycloneDX and SPDX)
-   Automatically updates to the latest plugin version

<!-- PREREQUISITES -->
## Prerequisites
-   The source code for your project is hosted on a VCS that is supported by TeamCity (Subversion, Git, and Mercurial. TFS and Perforce are partially supported. See TeamCity documentation [here](https://www.jetbrains.com/help/teamcity/creating-and-editing-projects.html#Creating+Project).)
-   Supported Java version - JDK 11
-   You have a Checkmarx One account and you have an OAuth **Client ID** and **Client Secret** for that account. To create an OAuth client, see [Creating an OAuth Client for Checkmarx One Integrations](https://checkmarx.com/resource/documents/en/34965-118315-authentication-for-checkmarx-one-cli.html#UUID-a4e31a96-1f36-6293-e95a-97b4b9189060_UUID-4123a2ff-32d0-2287-8dd2-3c36947f675e).

<!-- INITIAL SETUP -->
## Initial Setup
-   Verify that all prerequisites are in place.
-   Install the **Checkmarx AST** plugin and configure the settings as
    described [here](https://checkmarx.com/resource/documents/en/34965-68697-installing-the-teamcity-checkmarx-one-plugin.html).

<!-- USAGE -->
## Usage
To see how you can use our tool, please refer to the [Documentation](https://checkmarx.com/resource/documents/en/34965-68696-checkmarx-one-teamcity-plugin.htmln)

<!-- CONTRIBUTION -->
## Contribution

We appreciate feedback and contribution to the TEAMCITY PLUGIN! Before you get started, please see the following:

- [Checkmarx contribution guidelines](docs/contributing.md)
- [Checkmarx Code of Conduct](docs/code_of_conduct.md)


<!-- LICENSE -->
## License
Distributed under the [Apache 2.0](LICENSE). See `LICENSE` for more information.


<!-- FEEDBACK -->
## Feedback
We’d love to hear your feedback! If you come across a bug or have a feature request, please let us know by submitting an issue in [GitHub Issues](https://github.com/Checkmarx/ast-teamcity-plugin/issues).

<!-- CONTACT -->
## Contact

Checkmarx - AST Integrations Team

Project Link: [https://github.com/checkmarx/ast-teamcity-plugin](https://github.com/checkmarx/ast-teamcity-plugin)

Find more integrations from our team [here](https://github.com/Checkmarx/ci-cd-integrations#checkmarx-ast-integrations)


© 2022 Checkmarx Ltd. All Rights Reserved.

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/checkmarx/ast-teamcity-plugin.svg
[contributors-url]: https://github.com/checkmarx/ast-teamcity-plugin/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/checkmarx/ast-teamcity-plugin.svg
[forks-url]: https://github.com/checkmarx/ast-teamcity-plugin/network/members
[stars-shield]: https://img.shields.io/github/stars/checkmarx/ast-teamcity-plugin.svg
[stars-url]: https://github.com/checkmarx/ast-teamcity-plugin/stargazers
[issues-shield]: https://img.shields.io/github/issues/checkmarx/ast-teamcity-plugin.svg
[issues-url]: https://github.com/checkmarx/ast-teamcity-plugin/issues
[license-shield]: https://img.shields.io/github/license/checkmarx/ast-teamcity-plugin.svg
[license-url]: https://github.com/checkmarx/ast-teamcity-plugin/blob/main/LICENSE
