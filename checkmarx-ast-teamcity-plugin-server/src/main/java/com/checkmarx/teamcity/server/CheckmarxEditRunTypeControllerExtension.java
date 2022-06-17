package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxParams;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import com.checkmarx.teamcity.common.PluginUtils;
import jetbrains.buildServer.controllers.ActionErrors;
import jetbrains.buildServer.controllers.BasePropertiesBean;
import jetbrains.buildServer.controllers.StatefulObject;
import jetbrains.buildServer.controllers.admin.projects.BuildRunnerBean;
import jetbrains.buildServer.controllers.admin.projects.BuildTypeForm;
import jetbrains.buildServer.controllers.admin.projects.EditRunTypeControllerExtension;
import jetbrains.buildServer.serverSide.BuildTypeSettings;
import jetbrains.buildServer.serverSide.SBuildServer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CheckmarxEditRunTypeControllerExtension implements EditRunTypeControllerExtension
{
    private static final String TC_BUILD_BRANCH = "%teamcity.build.branch%";

    private final CheckmarxAdminConfig adminConfig;

    public CheckmarxEditRunTypeControllerExtension(@NotNull final SBuildServer server,
                                            @NotNull final CheckmarxAdminConfig adminConfig) {
        server.registerExtension(EditRunTypeControllerExtension.class, CheckmarxScanRunnerConstants.RUNNER_TYPE, this);
        this.adminConfig = adminConfig;
    }
    @Override
    public void fillModel(@NotNull HttpServletRequest httpServletRequest, @NotNull BuildTypeForm buildTypeForm, @NotNull Map<String, Object> model) {

        Map<String, String> properties = new HashMap<>();
        final BuildRunnerBean buildRunnerBean = buildTypeForm.getBuildRunnerBean();
        try {
            Method propertiesBeanMethod = BuildRunnerBean.class.getDeclaredMethod("getPropertiesBean");
            BasePropertiesBean basePropertiesBean = (BasePropertiesBean) propertiesBeanMethod.invoke(buildRunnerBean);
            properties = basePropertiesBean.getProperties();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        //put default project name as the build name
        if(StringUtils.isEmpty(properties.get(CheckmarxParams.PROJECT_NAME))) {
            properties.put(CheckmarxParams.PROJECT_NAME, buildTypeForm.getName());
        }

        // points branch name to the default team city's branch environment variable
        if(StringUtils.isEmpty(properties.get(CheckmarxParams.BRANCH_NAME))) {
            properties.put(CheckmarxParams.BRANCH_NAME, TC_BUILD_BRANCH);
        }

        //put all global properties to the config page
        for (String conf : CheckmarxParams.GLOBAL_CONFIGS) {
            properties.put(conf, adminConfig.getConfiguration(conf));
        }

        model.put(CheckmarxParams.USE_DEFAULT_SERVER, properties.get(CheckmarxParams.USE_DEFAULT_SERVER));
        model.put(CheckmarxParams.SERVER_URL, properties.get(CheckmarxParams.SERVER_URL));
        model.put(CheckmarxParams.AST_CLIENT_ID, properties.get(CheckmarxParams.AST_CLIENT_ID));
        model.put(CheckmarxParams.AST_SECRET, properties.get(CheckmarxParams.AST_SECRET));
        model.put(CheckmarxParams.GLOBAL_AST_SERVER_URL, adminConfig.getConfiguration(CheckmarxParams.GLOBAL_AST_SERVER_URL));
        model.put(CheckmarxParams.GLOBAL_AST_CLIENT_ID, adminConfig.getConfiguration(CheckmarxParams.GLOBAL_AST_CLIENT_ID));
        model.put(CheckmarxParams.GLOBAL_AST_SECRET, adminConfig.getConfiguration(CheckmarxParams.GLOBAL_AST_SECRET));
        model.put(CheckmarxParams.GLOBAL_ADDITIONAL_PARAMETERS, adminConfig.getConfiguration(CheckmarxParams.GLOBAL_ADDITIONAL_PARAMETERS));


    }

    @Override
    public void updateState(@NotNull HttpServletRequest httpServletRequest, @NotNull BuildTypeForm buildTypeForm) {

    }

    @Nullable
    @Override
    public StatefulObject getState(@NotNull HttpServletRequest httpServletRequest, @NotNull BuildTypeForm buildTypeForm) {
        return null;
    }

    @NotNull
    @Override
    public ActionErrors validate(@NotNull HttpServletRequest httpServletRequest, @NotNull BuildTypeForm buildTypeForm) {
        Map<String, String> properties = new HashMap<>();
        final BuildRunnerBean buildRunnerBean = buildTypeForm.getBuildRunnerBean();
        try {
            Method propertiesBeanMethod = BuildRunnerBean.class.getDeclaredMethod("getPropertiesBean");
            BasePropertiesBean basePropertiesBean = (BasePropertiesBean) propertiesBeanMethod.invoke(buildRunnerBean);
            properties = basePropertiesBean.getProperties();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        String astSecret = properties.get(CheckmarxParams.AST_SECRET);

        try {
            if(astSecret != null) {
                astSecret = PluginUtils.encrypt(astSecret);
            }
        } catch (RuntimeException e) {
            astSecret = "";
        }
        properties.put(CheckmarxParams.AST_SECRET, astSecret);

        return new ActionErrors();
    }

    @Override
    public void updateBuildType(@NotNull HttpServletRequest httpServletRequest, @NotNull BuildTypeForm buildTypeForm, @NotNull BuildTypeSettings buildTypeSettings, @NotNull ActionErrors actionErrors) {

    }
}
