package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxParams;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class CheckmarxAdminPage extends AdminPage {
    private final CheckmarxAdminConfig checkmarxAdminConfig;

    protected CheckmarxAdminPage(@NotNull PagePlaces pagePlaces, @NotNull PluginDescriptor descriptor, @NotNull final WebControllerManager controllerManager, @NotNull final CheckmarxAdminConfig checkmarxAdminConfig) {
        super(pagePlaces);
        this.checkmarxAdminConfig = checkmarxAdminConfig;
        setPluginName(CheckmarxScanRunnerConstants.RUNNER_TYPE);
        setIncludeUrl(descriptor.getPluginResourcesPath("adminPage.jsp"));
        setTabTitle(CheckmarxScanRunnerConstants.RUNNER_DISPLAY_NAME);
        register();
        controllerManager.registerController("/admin/checkmarxAstSettings.html", new CheckmarxAdminPageController(checkmarxAdminConfig));
    }

    @Override
    public boolean isAvailable(@NotNull HttpServletRequest request) {
        return super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
    }

    @NotNull
    @Override
    public String getGroup() {
        return INTEGRATIONS_GROUP;
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {

        for (String conf : CheckmarxParams.GLOBAL_CONFIGS) {
            model.put(conf, checkmarxAdminConfig.getConfiguration(conf));
        }

        model.put("hexEncodedPublicKey", RSACipher.getHexEncodedPublicKey());
    }
}
