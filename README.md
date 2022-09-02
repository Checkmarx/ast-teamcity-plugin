<img src="https://raw.githubusercontent.com/Checkmarx/ci-cd-integrations/main/.images/banner.png">
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
    <img src="https://raw.githubusercontent.com/Checkmarx/ci-cd-integrations/main/.images/logo.png" alt="Logo" width="80" height="80" />
  </a>

<h3 align="center">AST TEAMCITY PLUGIN</h3>

<p align="center">
    The CxAST TeamCity plugin enables you to trigger SAST, SCA, and KICS scans directly from a TeamCity project.
    <br />
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
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#setting-up">Setting Up</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

It provides a wrapper around the CxAST CLI Tool which creates a zip archive from your source code repository and uploads 
it to CxAST for scanning. This plugin provides easy integration with TeamCity while enabling scan customization using the 
full functionality and flexibility of the CLI tool.

<!-- GETTING STARTED -->
## Getting Started


### Prerequisites

- The source code for your project is hosted on a VCS that is supported by TeamCity (Subversion, Git, and Mercurial. 
TFS and Perforce are partially supported. See TeamCity documentation here.)

- You have a CxAST account and you have an OAuth2 Client ID and Client Secret for that account.

### Setting Up


All the procedures to set the teamcity plugin up can be found [here](https://checkmarx.com/resource/documents/en/34965-68698-configuring-global-integration-settings-for-checkmarx-one-teamcity-plugin.html).



## Usage

To see how you can use our tool, please refer to the [Documentation](https://checkmarx.com/resource/documents/en/34965-68696-checkmarx-one-teamcity-plugin.htmln)

## Contribution

We appreciate feedback and contribution to the TEAMCITY PLUGIN! Before you get started, please see the following:

- [Checkmarx contribution guidelines](docs/contributing.md)
- [Checkmarx Code of Conduct](docs/code_of_conduct.md)


<!-- LICENSE -->
## License
Distributed under the [Apache 2.0](LICENSE). See `LICENSE` for more information.


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
