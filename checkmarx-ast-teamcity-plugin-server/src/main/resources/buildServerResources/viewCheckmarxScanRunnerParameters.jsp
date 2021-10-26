<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="constants" class="com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants"/>


<div class="parameter">
  Server Url: <strong><props:displayValue name="${constants.serverUrl}" emptyValue="not specified"/></strong>
</div>
<div class="parameter">
  Project name: <strong><props:displayValue name="${constants.projectName}" emptyValue="not specified"/></strong>
</div>
<div class="parameter">
  Branch name: <strong><props:displayValue name="${constants.branchName}" emptyValue="not specified"/></strong>
</div>
<div class="parameter">
  Additional parameters: <strong><props:displayValue name="${constants.additionalParameters}" emptyValue="none specified"/></strong>
</div>


