<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@include file="/include.jsp" %>

<style>
  .scanControlSectionTable {
    margin-left: -10px;
  }

  .cxTitle {
    text-align: center;
    font-weight: bold;
    font-size: medium;
  }

</style>

<script type="text/javascript">
    function sanitizeJS(str) {
        var temp = document.createElement('div');
        temp.textContent = str;
        return temp.innerHTML;
    }

  var SettingsForm = OO.extend(BS.AbstractPasswordForm, {
    formElement: function () {
      return $("globalSettingsForm")
    },
    save: function () {

      BS.PasswordFormSaver.save(this, this.formElement().action, OO.extend(BS.ErrorsAwareListener, {
        onInvalid_cxGlobalServerUrlError: function (elem) {
          $("invalid_cxGlobalServerUrl").innerHTML = sanitizeJS(elem.firstChild.nodeValue);
          SettingsForm.highlightErrorField($("cxGlobalServerUrl"));
        },
        onInvalid_cxGlobalUsernameError: function (elem) {
          $("invalid_cxGlobalUsername").innerHTML = sanitizeJS(elem.firstChild.nodeValue);
          SettingsForm.highlightErrorField($("cxGlobalUsername"));
        },
        onInvalid_cxGlobalPasswordError: function (elem) {
          $("invalid_cxGlobalPassword").innerHTML = sanitizeJS(elem.firstChild.nodeValue);
          SettingsForm.highlightErrorField($("cxGlobalPassword"));
        },

        onInvalid_cxGlobalScanTimeoutInMinutesError: function (elem) {
          $("invalid_cxGlobalScanTimeoutInMinutes").innerHTML = sanitizeJS(elem.firstChild.nodeValue);
          SettingsForm.highlightErrorField($("cxGlobalScanTimeoutInMinutes"));
        },

        onSuccessfulSave: function () {
          SettingsForm.enable();
        },
        onCompleteSave: function (form, responseXml, wereErrors) {
          BS.ErrorsAwareListener.onCompleteSave(form, responseXml, wereErrors);
          if (!wereErrors) {
            $('generalSettings').refresh();
            window.scrollTo(0, 0);
          }
        }
      }));
      return false;
    }
  });
</script>
<script type="text/javascript" src="<c:url value='${teamcityPluginResourcesPath}testConnection.js'/>"></script>


<div>
  <bs:refreshable containerId="generalSettings" pageUrl="${pageUrl}">

    <bs:messages key="settingsSaved"/>

    <form id="globalSettingsForm" action="<c:url value='/admin/checkmarxSettings.html'/>" method="post"
          onsubmit="{return SettingsForm.save()}">

      <table class="runnerFormTable">
        <tr class="groupingTitle">
          <td colspan="2">Checkmarx AST Server</td>
        </tr>

        <tr>
          <th><label for="cxGlobalServerUrl">AST Server URL<l:star/></label></th>
          <td>
            <forms:textField name="cxGlobalServerUrl" value="${cxGlobalServerUrl}" className="longField"/>
            <span class="error" id="invalid_cxGlobalServerUrl"></span>
          </td>
        </tr>

        <tr>
          <th><label for="cxGlobalScanTimeoutInMinutes">AST Authentication Server URL
            <bs:helpIcon iconTitle="Abort the scan if exceeds specified timeout in minutes"/></label></th>
          <td>
            <forms:textField name="cxGlobalScanTimeoutInMinutes" value="${cxGlobalScanTimeoutInMinutes}" className="longField"/>
            <span class="error" id="invalid_cxGlobalScanTimeoutInMinutes"></span>
          </td>
        </tr>

        <tr>
          <th><label for="cxGlobalExcludeFolders">Tenant Name
            <bs:helpIcon iconTitle="Comma separated list of folders to exclude from scan.</br>
                                    Entries in this list are automatically converted to exclude wildcard patterns and appended to the full pattern list provided in the advanced section"/></label></th>
          <td><forms:textField name="cxGlobalExcludeFolders" value="${cxGlobalExcludeFolders}" className="longField"/></td>
        </tr>

        <tr>
          <th><label for="cxGlobalUsername">Client Id<l:star/></label></th>
          <td>
            <forms:textField name="cxGlobalUsername" value="${cxGlobalUsername}" className="longField"/>
            <span class="error" id="invalid_cxGlobalUsername"></span>

          </td>

        </tr>

        <tr>
          <th><label for="cxGlobalPassword">Secret<l:star/></label></th>
          <td>
            <input type="password" id="cxGlobalPassword" name="cxGlobalPassword" value="${cxGlobalPassword}" class="longField"/>
            <span class="error" id="invalid_cxGlobalPassword"></span>
          </td>
        </tr>

        <tr class="groupingTitle">
          <td colspan="2">Checkmarx Scan Settings</td>
        </tr>

        <tr>
          <th><label for="cxGlobalFilterPatterns">Zip File Filters
            <bs:helpIcon iconTitle="Comma separated list of include or exclude wildcard patterns. Exclude patterns start with exclamation mark \"!\". Example: **/*.java, **/*.html, !**/test/**/XYZ*"/></label></th>
          <td><textarea rows="5" cols="50" name="cxGlobalFilterPatterns" wrap="off">${cxGlobalFilterPatterns}</textarea>
          </td>
        </tr>
      </table>

      <div class="saveButtonsBlock">
        <input class="submitButton" type="submit" value="Save">
        <input type="hidden" id="publicKey" name="publicKey"
               value="<c:out value='${hexEncodedPublicKey}'/>"/>
        <forms:saving/>
      </div>
    </form>
  </bs:refreshable>
</div>