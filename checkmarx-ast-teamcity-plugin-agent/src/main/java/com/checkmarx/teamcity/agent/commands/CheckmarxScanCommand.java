package com.checkmarx.teamcity.agent.commands;


import com.checkmarx.teamcity.common.CheckmarxScanConfig;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import com.checkmarx.teamcity.common.PluginUtils;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class CheckmarxScanCommand extends CheckmarxBuildServiceAdapter {

    private static final Logger LOG = Logger.getLogger(CheckmarxScanCommand.class);
    private static CheckmarxScanConfig scanConfig;

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {

        // reference https://codingsight.com/implementing-a-teamcity-plugin/
        AgentRunningBuild agentRunningBuild = getRunnerContext().getBuild();
        // something logic with build instance

        BuildProgressLogger logger = agentRunningBuild.getBuildLogger();
        // something logic with logger instance (output information)
        Map<String, String> sharedConfigParameters = agentRunningBuild.getSharedConfigParameters();

        Map<String, String> runnerParameters = getRunnerParameters(); // get runner parameters

        scanConfig = PluginUtils.resolveConfiguration(runnerParameters, sharedConfigParameters);

        LOG.info("-----------------------Checkmarx: Initiating the Scan Command------------------------");
        String checkmarxCliToolPath = getCheckmarxCliToolPath();

        setExecutePermission(checkmarxCliToolPath);

        String checkmarxAstSecret = scanConfig.getAstSecret();
        if (nullIfEmpty(checkmarxAstSecret) == null) {
            throw new RunBuildException("Checkmarx API secret was not defined. Please configure the build properly and retry.");
        }
        Map<String, String> envVars = new HashMap<>(getEnvironmentVariables());
        envVars.put("CX_CLIENT_SECRET", checkmarxAstSecret);

        // saving a file for mock results
        String buildTempDirectory = getBuild().getBuildTempDirectory().getAbsolutePath();
        // String checkmarxMockReportHtml = Paths.get(buildTempDirectory, "checkmarx-mock-report.html").toFile().getAbsolutePath();

        File htmlFile = new File(buildTempDirectory, CheckmarxScanRunnerConstants.REPORT_HTML_NAME);
        try {
            FileUtils.writeStringToFile(htmlFile, PluginUtils.getHtmlText());
        } catch (IOException e) {
            logger.error("Failed to generate full html report: " + e.getMessage());
        }
        // mock results end here

        return new SimpleProgramCommandLine(envVars, getWorkingDirectory().getAbsolutePath(), checkmarxCliToolPath, getArguments());
    }

    @Override
    public void beforeProcessStarted() {
        getBuild().getBuildLogger().message("Scanning with Checkmarx AST CLI " + CheckmarxScanRunnerConstants.AST_CLI_VERSION);
    }

    @Override
    List<String> getArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.add("scan");
        arguments.add("create");

        arguments.add("--base-uri");
        arguments.add(scanConfig.getServerUrl());

        if (nullIfEmpty(scanConfig.getAuthenticationUrl()) != null) {
            arguments.add("--base-auth-uri");
            arguments.add(scanConfig.getAuthenticationUrl());
        }

        if (nullIfEmpty(scanConfig.getTenant()) != null) {
            arguments.add("--tenant");
            arguments.add(scanConfig.getTenant());
        }

        arguments.add("--client-id");
        arguments.add(scanConfig.getClientId());

        //Set Origin
        arguments.add("--agent");
        arguments.add("TeamCity");

        arguments.add("--project-name");
        arguments.add(scanConfig.getProjectName());

        arguments.add("-s");
        arguments.add(".");

        if (nullIfEmpty(scanConfig.getAdditionalParameters()) != null) {
            arguments.addAll(asList(scanConfig.getAdditionalParameters().split("\\s+")));
        }

        return arguments;
    }

    private static void setExecutePermission(String checkmarxCliToolPath) throws RunBuildException {
        final File cxExecutable = new File(checkmarxCliToolPath);
        if (!SystemUtils.IS_OS_WINDOWS && cxExecutable.isFile()) {
            boolean result = cxExecutable.setExecutable(true, false);

            if (!result) {
                throw new RunBuildException(format("Could not set executable flag for the file: %s", cxExecutable.getName()));
            }
        }
    }
}
