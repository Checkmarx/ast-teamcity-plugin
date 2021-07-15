package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxParams;
import jetbrains.buildServer.serverSide.BuildStartContext;
import jetbrains.buildServer.serverSide.BuildStartContextProcessor;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

public class CheckmarxBuildStartContextProcessor implements BuildStartContextProcessor {
    private final CheckmarxAdminConfig cxAdminConfig;
    private final PluginDescriptor pluginDescriptor;

    public CheckmarxBuildStartContextProcessor(@NotNull final CheckmarxAdminConfig cxAdminConfig,
                                        final PluginDescriptor pluginDescriptor) {
        this.cxAdminConfig = cxAdminConfig;
        this.pluginDescriptor = pluginDescriptor;
    }

    @Override
    public void updateParameters(@NotNull final BuildStartContext buildStartContext) {

        for (String config : CheckmarxParams.GLOBAL_CONFIGS) {
            buildStartContext.addSharedParameter(config, this.cxAdminConfig.getConfiguration(config));
        }

    }
}
