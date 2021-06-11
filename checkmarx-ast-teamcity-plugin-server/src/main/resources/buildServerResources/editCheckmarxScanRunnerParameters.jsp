<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="constants" class="com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants"/>
<jsp:useBean id="runners" class="com.checkmarx.teamcity.common.runner.Runners"/>

<l:settingsGroup title="Checkmarx Scan Settings">

  <tr>
    <th><label for="${constants.serverUrl}.text">AST Server Url:</label></th>
    <td>
      <props:textProperty name="${constants.serverUrl}" className="longField" id="${constants.serverUrl}.text"/>
      <span class="smallNote">AST Server Url</span>
    </td>
  </tr>

    <tr>
      <th><label>Checkmarx API key:</label><l:star/></th>
      <td>
        <props:passwordProperty name="${constants.apiKey}" className="longField"/>
        <span class="smallNote">The API Key for AST authentication.</span>
        <span class="error" id="error_${constants.apiKey}"></span>
      </td>
    </tr>

  <tr>
    <th><label for="${constants.projectName}.text">Project name:</label></th>
    <td>
      <props:textProperty name="${constants.projectName}" className="longField" id="${constants.projectName}.text"/>
      <span class="smallNote">Project Name for AST</span>
    </td>
  </tr>

 <tr>
    <th><label>Scanners:</label></th>
    <td>
        <props:checkboxProperty name="${constants.sastScanEnabled}"/>
        <label for="${constants.sastScanEnabled}">SAST</label>
        <br>
        <props:checkboxProperty name="${constants.scaScanEnabled}"/>
        <label for="${constants.scaScanEnabled}">SCA</label>
        <br>
        <props:checkboxProperty name="${constants.kicsEnabled}"/>
        <label for="${constants.kicsEnabled}">KICS</label>
        <br>
        <props:checkboxProperty name="${constants.containerScanEnabled}"/>
        <label for="${constants.containerScanEnabled}">Container Scan</label>
    </td>
  </tr>



  <tr>
    <th><label for="${constants.zipFileFilters}.text">Zip File Filters:</label></th>
    <td>
      <props:textProperty name="${constants.zipFileFilters}" className="longField" expandable="true" id="${constants.zipFileFilters}.text"/>
      <span class="smallNote">File filters to be used while zipping the source code.</span>
    </td>
  </tr>


  <tr>
    <th><label for="${constants.additionalParameters}.text">Additional parameters:</label></th>
    <td>
      <props:textProperty name="${constants.additionalParameters}" className="longField" expandable="true" id="${constants.additionalParameters}.text"/>
      <span class="smallNote">Refer to the <a href="https://github.com/CheckmarxDev/ast-cli">Checkmarx AST CLI help page</a> for information on additional arguments.</span>
    </td>
  </tr>

   <tr>
      <th><label for="${constants.version}.select">Checkmarx CLI version:</label><l:star/></th>
      <td>
        <props:selectProperty name="${constants.version}" className="mediumField" enableFilter="false" id="${constants.version}.select">
          <c:forEach items="${runners.versions}" var="checkmarxCliVersion">
            <props:option value="${checkmarxCliVersion}">${checkmarxCliVersion}</props:option>
          </c:forEach>
        </props:selectProperty>
        <span class="error" id="error_${constants.version}"></span>
      </td>
    </tr>

</l:settingsGroup>



