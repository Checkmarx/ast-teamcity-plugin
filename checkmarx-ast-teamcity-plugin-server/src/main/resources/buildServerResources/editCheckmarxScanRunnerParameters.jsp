<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="constants" class="com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants"/>
<jsp:useBean id="runners" class="com.checkmarx.teamcity.common.runner.Runners"/>
<jsp:useBean id="optionsBean" class="com.checkmarx.teamcity.server.CheckmarxOptions"/>


<c:if test="${propertiesBean.properties[optionsBean.useDefaultServer] == 'true'}">
    <c:set var="hideServerOverrideSection" value="${optionsBean.noDisplay}"/>
</c:if>
<c:if test="${propertiesBean.properties[optionsBean.useGlobalAdditionalParameters] == 'true'}">
    <c:set var="hideAdditionalParametersOverrideSection" value="${optionsBean.noDisplay}"/>
</c:if>

<!-- Set a variable to define when the server url error element must be shown or not -->
<c:choose>
    <c:when test="${empty propertiesBean.properties[optionsBean.serverUrl]}">
        <c:set var="displayServerUrlError" value="block"/>
    </c:when>
    <c:otherwise>
        <c:set var="displayServerUrlError" value="none"/>
    </c:otherwise>
</c:choose>

<l:settingsGroup title="Checkmarx Scan Settings">

    <style>
        .cx-textarea {
            height: 10em;
            width: 31em;
        }

        /* Created custom error because when using generic error class, error text is cleared from the form when save button is pressed */
        .cx-error {
            color: #c22731
        }
    </style>

    <script>
        function toggleGlobalArguments(e) {
            e.preventDefault();
            let link = $('globalArgumentsLink'),
                textArea = $('globalArgumentsTextArea'),
                hidden = textArea.style.display === "none"

            textArea.style.display = hidden ? "" : "none"
            link.innerText = (hidden ? "Hide" : "Show") + " global parameters"
            return false
        }

        /**
         * Function used to highlight an error message when a mandatory field is not filled
         *
         * @param event
         */
        function validateRequiredField(event) {
            const element = event.target;
            const errorElement = element.parentNode.parentNode.querySelector('span.cx-error')
            errorElement.style.display = element.value ? 'none' : 'block';
        }

        // Bind events when document is ready
        $j(document).ready(function() {
            document.getElementById("globalArgumentsLink").addEventListener("click", toggleGlobalArguments);
            document.querySelectorAll('.required').forEach((el) => el.addEventListener("keyup", validateRequiredField));
        })

    </script>

    <tr>
        <th>
            <label for="${optionsBean.useDefaultServer}">Use Global Settings for AST Server.<br>
              Server URL: ${propertiesBean.properties[optionsBean.globalAstServerUrl]} <br>
        </th>
        <td>
            <c:set var="onclick">
                $('serverOverrideSection').toggle();
            </c:set>
            <props:checkboxProperty name="${optionsBean.useDefaultServer}" onclick="${onclick}"/>
        </td>
    </tr>

  <tbody id="serverOverrideSection" ${hideServerOverrideSection}>
  <tr>
    <th><label for="${optionsBean.serverUrl}.text">AST Server Url:<l:star/></label></th>
    <td>
      <props:textProperty
              name="${optionsBean.serverUrl}"
              className="longField required"
              id="${optionsBean.serverUrl}.text" />

        <span class="cx-error" id="serverUrlError" style="display: ${displayServerUrlError}">The server Url must be specified</span>
        <span class="smallNote">AST Server Url</span>
    </td>
  </tr>

    <tr>
      <th><label for="${optionsBean.authenticationUrl}.text">AST Authentication Url:</label></th>
      <td>
        <props:textProperty name="${optionsBean.authenticationUrl}" className="longField" id="${optionsBean.authenticationUrl}.text"/>
        <span class="smallNote">AST Authentication Url</span>
      </td>
    </tr>

    <tr>
      <th><label for="${optionsBean.tenant}.text">Tenant:</label></th>
      <td>
         <props:textProperty name="${optionsBean.tenant}" className="longField" id="${optionsBean.tenant}.text"/>
         <span class="smallNote">Tenant</span>
      </td>
    </tr>

    <tr>
      <th><label for="${optionsBean.astClientId}.text">AST Client Id:</label></th>
      <td>
        <props:textProperty name="${optionsBean.astClientId}" className="longField" id="${optionsBean.astClientId}.text"/>
        <span class="smallNote">AST Client Id</span>
      </td>
    </tr>

    <tr>
      <th><label>AST Secret:</label><l:star/></th>
      <td>
        <props:passwordProperty name="${optionsBean.astSecret}" className="longField"/>
        <span class="smallNote">The Secret obtained from AST.</span>
        <span class="error" id="error_${optionsBean.astSecret}"></span>
      </td>
    </tr>
    </tbody>

  <tr>
    <th><label for="${optionsBean.projectName}.text">Project name:</label></th>
    <td>
      <props:textProperty name="${optionsBean.projectName}" className="longField" id="${optionsBean.projectName}.text"/>
      <span class="smallNote">Project Name for AST</span>
    </td>
  </tr>

    <tr>
        <th><label for="${optionsBean.branchName}.text">Branch name:</label></th>
        <td>
            <props:textProperty name="${optionsBean.branchName}"
                                className="longField required" id="${optionsBean.branchName}.text" />

            <span class="cx-error" id="branchError" style="display: none">The branch name must be specified</span>
            <span class="smallNote">Branch Name for AST</span>
        </td>
    </tr>

    <tr>
        <th><label for="${optionsBean.useGlobalAdditionalParameters}">Use global additional parameters.<br></label></th>
        <td>
            <c:set var="onclick">
                $('additionalParametersOverrideSection').toggle();
            </c:set>
            <props:checkboxProperty name="${optionsBean.useGlobalAdditionalParameters}" onclick="${onclick}"/>
        </td>
    </tr>

    <tbody id="additionalParametersOverrideSection" ${hideAdditionalParametersOverrideSection}>
    <tr>
        <th><label for="${optionsBean.additionalParameters}.text">Additional parameters:</label></th>
        <td>
            <props:multilineProperty name="${optionsBean.additionalParameters}" linkTitle=""
                                     cols="50" rows="5" expanded="true"
                                     className="cx-textarea" />
            <span class="smallNote">Refer to the
                <a href="https://github.com/CheckmarxDev/ast-cli">Checkmarx AST CLI help page</a>
                for information on additional parameters.</span>
        </td>
    </tr>
    </tbody>

    <tr>
        <td style="vertical-align: top">
            <a id="globalArgumentsLink" href="#">Show global parameters</a>
        </td>
        <td>
            <textarea style="background-color: lightgrey; display: none" disabled class="cx-textarea" rows="5"
                      id="globalArgumentsTextArea">${propertiesBean.properties[optionsBean.globalAdditionalParameters]}
            </textarea>
        </td>
    </tr>

</l:settingsGroup>



