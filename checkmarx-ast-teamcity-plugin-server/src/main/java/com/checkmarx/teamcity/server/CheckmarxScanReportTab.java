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
import jetbrains.buildServer.web.reportTabs.ReportTabUtil;
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

    public CheckmarxScanReportTab(@NotNull PagePlaces pagePlaces, @NotNull SBuildServer server, @NotNull final PluginDescriptor pluginDescriptor) {
        super(TAB_TITLE, TAB_CODE, pagePlaces, server);

        setIncludeUrl(pluginDescriptor.getPluginResourcesPath("checkmarxScanReport.jsp"));
   //     setIncludeUrl("/artifactsViewer.jsp");
        setPosition(PositionConstraint.after("artifacts"));

    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> map, @NotNull HttpServletRequest httpServletRequest, @NotNull SBuild sBuild) {
      //  map.put("startPage", getHtmlReport(sBuild));
    //   map.put("content", getHtmlReport(sBuild));

        String checkmarxScanHtmlReportPath = TEAMCITY_ARTIFACTS_DIR + separator + CheckmarxScanRunnerConstants.RUNNER_DISPLAY_NAME + separator + CheckmarxScanRunnerConstants.REPORT_HTML_NAME;
        BuildArtifact artifact = sBuild.getArtifacts(BuildArtifactsViewMode.VIEW_HIDDEN_ONLY).getArtifact(checkmarxScanHtmlReportPath);
        if(artifact != null) {
            try {
                String s = IOUtils.toString(artifact.getInputStream());
                map.put("content", "Hello world!");

            } catch (IOException e) {
                map.put("content", "Failed to get the report: " + e.getMessage());

            }
        } else {
            map.put("content", "Failed to get the report");
        }

    }

    @Override
    protected boolean isAvailable(@NotNull HttpServletRequest request, @NotNull SBuild build) {
        SBuildType buildType = build.getBuildType();
        if (buildType == null || !build.isFinished()) {
            return false;
        }
        return buildType.getRunnerTypes().contains(CheckmarxScanRunnerConstants.RUNNER_TYPE);
    }

    private String getHtmlReport(SBuild sBuild) {
        String checkmarxScanHtmlReportPath = TEAMCITY_ARTIFACTS_DIR + separator + CheckmarxScanRunnerConstants.RUNNER_DISPLAY_NAME + separator + CheckmarxScanRunnerConstants.REPORT_HTML_NAME;
        BuildArtifact artifact = sBuild.getArtifacts(BuildArtifactsViewMode.VIEW_HIDDEN_ONLY).getArtifact(checkmarxScanHtmlReportPath);
        return artifact != null ? artifact.getRelativePath() : CheckmarxScanRunnerConstants.REPORT_HTML_NAME;
    }



}
