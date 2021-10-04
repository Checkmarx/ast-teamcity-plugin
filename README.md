[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="">
    <img src="./logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">AST-TEAMCITY-PLUGIN</h3>

<p align="center">
    The CxAST TeamCity plugin enables you to trigger SAST, SCA, and KICS scans directly from a TeamCity project.
<br />
    <a href="https://checkmarx.atlassian.net/wiki/spaces/AST/pages/6023875112/TeamCity+Plugin"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/CheckmarxDev/checkmarx-ast-teamcity-plugin/issues/new">Report Bug</a>
    ·
    <a href="https://github.com/CheckmarxDev/checkmarx-ast-teamcity-plugin/issues/new">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
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


All the procedures to set the teamcity plugin up can be found [here](https://checkmarx.atlassian.net/wiki/spaces/AST/pages/6022729247/Installing+the+TeamCity+CxAST+Plugin).



## Usage

To see how you can use our tool, please refer to the [Documentation](https://checkmarx.atlassian.net/wiki/spaces/AST/pages/6023875112/TeamCity+Plugin)


## Contribution

We appreciate feedback and contribution to the Teamcity Plugin!

** **

<!-- LICENSE -->
## License
See `LICENSE` for more information.

<!-- CONTACT -->
## Contact

Checkmarx - AST Integrations Team

Project Link: [https://github.com/CheckmarxDev/checkmarx-ast-teamcity-plugin](https://github.com/CheckmarxDev/checkmarx-ast-teamcity-plugin)


© 2021 Checkmarx Ltd. All Rights Reserved.

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/CheckmarxDev/checkmarx-ast-teamcity-plugin.svg?style=flat-square
[contributors-url]: https://github.com/CheckmarxDev/checkmarx-ast-teamcity-plugin/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/CheckmarxDev/checkmarx-ast-teamcity-plugin.svg?style=flat-square
[forks-url]: https://github.com/CheckmarxDev/checkmarx-ast-teamcity-plugin/network/members
[stars-shield]: https://img.shields.io/github/stars/CheckmarxDev/checkmarx-ast-teamcity-plugin.svg?style=flat-square
[stars-url]: https://github.com/CheckmarxDev/checkmarx-ast-teamcity-plugin/stargazers
[issues-shield]: https://img.shields.io/github/issues/CheckmarxDev/checkmarx-ast-teamcity-plugin.svg?style=flat-square
[issues-url]: https://github.com/CheckmarxDev/checkmarx-ast-teamcity-plugin/issues
[license-shield]: https://img.shields.io/github/license/CheckmarxDev/checkmarx-ast-teamcity-plugin.svg?style=flat-square
[license-url]: https://github.com/CheckmarxDev/checkmarx-ast-teamcity-plugin/blob/master/LICENSE
[product-screenshot]: images/screenshot.png