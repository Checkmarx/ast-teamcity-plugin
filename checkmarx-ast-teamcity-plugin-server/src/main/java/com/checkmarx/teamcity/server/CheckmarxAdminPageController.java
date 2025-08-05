package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxParams;
import com.checkmarx.teamcity.common.PluginUtils;
import jetbrains.buildServer.controllers.ActionErrors;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.util.StringUtil;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Controller
public class CheckmarxAdminPageController {

    public static final String INVALID = "invalid_";
    private final static Logger LOG = Logger.getLogger(CheckmarxAdminPageController.class);
    private final CheckmarxAdminConfigBase checkmarxAdminConfig;

    public CheckmarxAdminPageController(@NotNull final CheckmarxAdminConfigBase checkmarxAdminConfig) {
        this.checkmarxAdminConfig = checkmarxAdminConfig;
    }

    @RequestMapping(value = "/admin/checkmarxAstSettings.html", method = RequestMethod.GET)
    public void handleGet(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {
        // Handle GET request - typically just return the form page
        response.getWriter().write("OK");
    }

    @RequestMapping(value = "/admin/checkmarxAstSettings.html", method = RequestMethod.POST)
    public void handlePost(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws IOException {

        final ActionErrors actionErrors = validateForm(request);
        if (actionErrors.hasErrors()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Validation errors occurred");
            return;
        }

        for (String config : CheckmarxParams.GLOBAL_CONFIGS) {
            checkmarxAdminConfig.setConfiguration(config, StringUtil.emptyIfNull(request.getParameter(config)));
        }

        String encryptedSecret = ensurePasswordEncryption(request, "encryptedGlobalAstSecret");
        checkmarxAdminConfig.setConfiguration(CheckmarxParams.GLOBAL_AST_SECRET, encryptedSecret);

        try {
            checkmarxAdminConfig.persistConfiguration();
            response.getWriter().write("Settings saved successfully");
        } catch (IOException e) {
            Loggers.SERVER.error("Failed to persist global configurations", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error saving settings");
        }
    }

    private String ensurePasswordEncryption(HttpServletRequest request, String requestParamName) {
        String password = RSACipher.decryptWebRequestData(request.getParameter(requestParamName));
        return PluginUtils.encrypt(password);
    }


    private ActionErrors validateForm(final HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();

        String globalAstServerUrl = request.getParameter(CheckmarxParams.GLOBAL_AST_SERVER_URL);
        if (com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces(CheckmarxParams.GLOBAL_AST_SERVER_URL)) {
            errors.addError(INVALID + CheckmarxParams.GLOBAL_AST_SERVER_URL, "AST Server URL must not be empty");
        } else {
            try {
                new URL(globalAstServerUrl);
            } catch (MalformedURLException e) {
                errors.addError(INVALID + CheckmarxParams.GLOBAL_AST_SERVER_URL, "AST Server Url is not valid.");
            }
        }

        return errors;
    }
}
