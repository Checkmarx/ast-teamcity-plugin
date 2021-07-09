package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import com.checkmarx.teamcity.common.PluginUtils;
import jetbrains.buildServer.controllers.ActionErrors;
import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.util.StringUtil;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;



public class CheckmarxAdminPageController extends BaseFormXmlController {

    private final static Logger LOG = Logger.getLogger(CheckmarxAdminPageController.class);

   // private final ServerPaths myServerPaths;
    private final CheckmarxAdminConfig checkmarxAdminConfig;
    public static final String INVALID = "invalid_";

//    public CheckmarxAdminPageController(final ServerPaths myServerPaths) {
//        this.myServerPaths = myServerPaths;
//    }

    public CheckmarxAdminPageController(final CheckmarxAdminConfig checkmarxAdminConfig) {
        this.checkmarxAdminConfig = checkmarxAdminConfig;
    }

    @Override
    protected ModelAndView doGet(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) {
        return null;
    }

    @Override
    protected void doPost(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull Element xmlResponse) {

        final ActionErrors actionErrors = validateForm(httpServletRequest);
        if(actionErrors.hasErrors()) {
            actionErrors.serialize(xmlResponse);
            return;
        }

        for (String config : CheckmarxScanRunnerConstants.GLOBAL_CONFIGS) {
            checkmarxAdminConfig.setConfiguration(config, StringUtil.emptyIfNull(httpServletRequest.getParameter(config)));
        }

        String encryptedSecret = ensurePasswordEncryption(httpServletRequest, "encryptedGlobalAstSecret");
        checkmarxAdminConfig.setConfiguration(CheckmarxScanRunnerConstants.GLOBAL_AST_SECRET, encryptedSecret);

        try {
            checkmarxAdminConfig.persistConfiguration();
        } catch (IOException e) {
            Loggers.SERVER.error("Failed to persist global configurations", e);
        }
        getOrCreateMessages(httpServletRequest).addMessage("settingsSaved", "Global settings saved for Checkmarx AST Plugin.");

    }

    private String ensurePasswordEncryption(HttpServletRequest request, String requestParamName) {
        String password = RSACipher.decryptWebRequestData(request.getParameter(requestParamName));
        return PluginUtils.encrypt(password);
    }


    private ActionErrors validateForm(final HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();

        String globalAstServerUrl = request.getParameter(CheckmarxScanRunnerConstants.GLOBAL_AST_SERVER_URL);
        if (com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces(CheckmarxScanRunnerConstants.GLOBAL_AST_SERVER_URL)) {
            errors.addError(INVALID + CheckmarxScanRunnerConstants.GLOBAL_AST_SERVER_URL, "AST Server URL must not be empty");
        } else {
            try {
                new URL(globalAstServerUrl);
            } catch (MalformedURLException e) {
                errors.addError(INVALID + CheckmarxScanRunnerConstants.GLOBAL_AST_SERVER_URL, "AST Server Url is not valid.");
            }
        }

        return errors;
    }
}
