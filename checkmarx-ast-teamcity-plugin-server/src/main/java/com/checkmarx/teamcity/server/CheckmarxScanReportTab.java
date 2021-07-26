package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import jetbrains.buildServer.serverSide.SBuild;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifact;
import jetbrains.buildServer.serverSide.artifacts.BuildArtifactsViewMode;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;
import jetbrains.buildServer.web.openapi.ViewLogTab;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static java.io.File.separator;
import static jetbrains.buildServer.ArtifactsConstants.TEAMCITY_ARTIFACTS_DIR;

public class CheckmarxScanReportTab extends ViewLogTab {

    public static final String CONTENT = "content";
    private static final String TAB_TITLE = CheckmarxScanRunnerConstants.RUNNER_DISPLAY_NAME;
    private static final String TAB_CODE = "checkmarxScanReport";

    public CheckmarxScanReportTab(@NotNull final PagePlaces pagePlaces, @NotNull final SBuildServer server, @NotNull PluginDescriptor pluginDescriptor) {
        super(TAB_TITLE, TAB_CODE, pagePlaces, server);
        setIncludeUrl(pluginDescriptor.getPluginResourcesPath("checkmarxScanReport.jsp"));
        setPosition(PositionConstraint.after("artifacts"));
        register();
    }

    @Override
    protected void fillModel(@NotNull final Map<String, Object> map, @NotNull final HttpServletRequest httpServletRequest, @NotNull final SBuild sBuild) {
        final String checkmarxScanHtmlReportPath = TEAMCITY_ARTIFACTS_DIR + separator + CheckmarxScanRunnerConstants.RUNNER_DISPLAY_NAME + separator + CheckmarxScanRunnerConstants.REPORT_HTML_NAME;
        final BuildArtifact artifact = sBuild.getArtifacts(BuildArtifactsViewMode.VIEW_HIDDEN_ONLY).getArtifact(checkmarxScanHtmlReportPath);

        if (artifact != null) {
            try {
                final String s = IOUtils.toString(artifact.getInputStream());
                map.put(CONTENT, s);

            } catch (final IOException e) {
                map.put("content", "Failed to get the report: " + e.getMessage());
            }
        } else {
            map.put("content", "Failed to get the report");
        }

    }

    @Override
    protected boolean isAvailable(@NotNull final HttpServletRequest request, @NotNull final SBuild build) {
        final SBuildType buildType = build.getBuildType();
        if (buildType == null || !build.isFinished()) {
            return false;
        }
        return buildType.getRunnerTypes().contains(CheckmarxScanRunnerConstants.RUNNER_TYPE);
    }
}
