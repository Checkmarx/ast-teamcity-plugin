package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxParams;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunType;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.util.PropertiesUtil;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static jetbrains.buildServer.util.PropertiesUtil.isEmptyOrNull;

public class CheckmarxScanRunType extends RunType {

    @NotNull
    private final PluginDescriptor pluginDescriptor;

    public CheckmarxScanRunType(@NotNull final RunTypeRegistry runTypeRegistry, @NotNull PluginDescriptor pluginDescriptor) {
        this.pluginDescriptor = pluginDescriptor;
        runTypeRegistry.registerRunType(this);
    }

    @NotNull
    @Override
    public String getType() {
        return CheckmarxScanRunnerConstants.RUNNER_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return CheckmarxScanRunnerConstants.RUNNER_DISPLAY_NAME;
    }

    @NotNull
    @Override
    public String getDescription() {
        return CheckmarxScanRunnerConstants.RUNNER_DESCRIPTION;
    }

    @Nullable
    @Override
    public PropertiesProcessor getRunnerPropertiesProcessor() {
        return properties -> {
            if (properties == null) {
                return Collections.emptyList();
            }
            List<InvalidProperty> result =  new ArrayList<>(0);
            if (!CheckmarxScanRunnerConstants.TRUE.equals(properties.get(CheckmarxParams.USE_DEFAULT_SERVER))) {

                if (isEmptyOrNull(properties.get(CheckmarxParams.SERVER_URL))) {
                    result.add(new InvalidProperty(CheckmarxParams.SERVER_URL, "Server URL must not be empty"));
                }
            }

            return result;
        };

    }

    @Nullable
    @Override
    public String getEditRunnerParamsJspFilePath() {
        return pluginDescriptor.getPluginResourcesPath("editCheckmarxScanRunnerParameters.jsp");
    }

    @Nullable
    @Override
    public String getViewRunnerParamsJspFilePath() {
        return pluginDescriptor.getPluginResourcesPath("viewCheckmarxScanRunnerParameters.jsp");
    }

    @Nullable
    @Override
    public Map<String, String> getDefaultRunnerProperties() {
        Map<String, String> defaultProperties = new HashMap<>();
        defaultProperties.put(CheckmarxScanRunnerConstants.ZIP_FILE_FILTERS, CheckmarxScanRunnerConstants.DEFAULT_ZIP_FILE_FILTER_PATTERN);
        return defaultProperties;
    }

}