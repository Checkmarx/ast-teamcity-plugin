<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@include file="/include.jsp" %>

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
        onInvalid_globalAstServerUrlError: function (elem) {
          $("invalid_globalAstServerUrl").innerHTML = sanitizeJS(elem.firstChild.nodeValue);
          SettingsForm.highlightErrorField($("globalAstServerUrl"));
        },
        onInvalid_globalAstClientIdError: function (elem) {
          $("invalid_globalAstClientId").innerHTML = sanitizeJS(elem.firstChild.nodeValue);
          SettingsForm.highlightErrorField($("globalAstClientId"));
        },
        onInvalid_globalAstSecretError: function (elem) {
          $("invalid_globalAstSecret").innerHTML = sanitizeJS(elem.firstChild.nodeValue);
          SettingsForm.highlightErrorField($("globalAstSecret"));
        },

        onInvalid_globalAstAuthenticationUrlError: function (elem) {
          $("invalid_globalAstAuthenticationUrl").innerHTML = sanitizeJS(elem.firstChild.nodeValue);
          SettingsForm.highlightErrorField($("globalAstAuthenticationUrl"));
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


<div>
  <bs:refreshable containerId="generalSettings" pageUrl="${pageUrl}">

    <bs:messages key="settingsSaved"/>

    <form id="globalSettingsForm" action="<c:url value='/admin/checkmarxAstSettings.html'/>" method="post"
          onsubmit="{return SettingsForm.save()}">

      <table class="runnerFormTable">
        <tr class="groupingTitle">
          <td colspan="2">Checkmarx AST Server</td>
        </tr>

        <tr>
          <th><label for="globalAstServerUrl">AST Server URL<l:star/></label></th>
          <td>
            <forms:textField name="globalAstServerUrl" value="${globalAstServerUrl}" className="longField"/>
            <span class="error" id="invalid_globalAstServerUrl"></span>
          </td>
        </tr>

        <tr>
          <th><label for="globalAstAuthenticationUrl">AST Authentication Server URL
            <bs:helpIcon iconTitle="External Authentication Url for AST"/></label></th>
          <td>
            <forms:textField name="globalAstAuthenticationUrl" value="${globalAstAuthenticationUrl}" className="longField"/>
            <span class="error" id="invalid_globalAstAuthenticationUrl"></span>
          </td>
        </tr>

        <tr>
          <th><label for="globalAstTenant">Tenant
            <bs:helpIcon iconTitle="Tenant name for the account."/></label></th>
          <td>
            <forms:textField name="globalAstTenant" value="${globalAstTenant}" className="longField"/>
          </td>
        </tr>

        <tr>
          <th><label for="globalAstClientId">Client Id<l:star/></label></th>
          <td>
            <forms:textField name="globalAstClientId" value="${globalAstClientId}" className="longField"/>
            <span class="error" id="invalid_globalAstClientId"></span>
          </td>
        </tr>

        <tr>
          <th><label for="globalAstSecret">Secret<l:star/></label></th>
          <td>
            <input type="password" id="globalAstSecret" name="globalAstSecret" value="${globalAstSecret}" class="longField"/>
            <span class="error" id="invalid_globalAstSecret"></span>
          </td>
        </tr>

        <tr class="groupingTitle">
          <td colspan="2">Checkmarx Scan Settings</td>
        </tr>

        <tr>
          <th><label for="globalAdditionalParameters">Additional Parameters
            <bs:helpIcon iconTitle="Refer to the <a href=\"https://github.com/CheckmarxDev/ast-cli\">Checkmarx AST CLI help page</a> for information on additional parameters."/></label></th>
          </th>
          <td>
            <textarea name="globalAdditionalParameters" wrap="off" class="longField" style="height: 10em">${globalAdditionalParameters}</textarea>
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