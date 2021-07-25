<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<div>
  <h2>Apica Load Test Summary</h2>
  <p>No results to show</p>
</div>
<div>
  <%@include file="checkmarx_ast_report.html" %>
</div>