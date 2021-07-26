package com.checkmarx.teamcity.agent.commands;


import com.checkmarx.teamcity.common.CheckmarxScanConfig;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import com.checkmarx.teamcity.common.PluginUtils;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

import static com.checkmarx.teamcity.common.CheckmarxParams.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class CheckmarxScanCommand extends CheckmarxBuildServiceAdapter {

    private static final Logger LOG = Logger.getLogger(CheckmarxScanCommand.class);
    private static CheckmarxScanConfig scanConfig;

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {

        //reference https://codingsight.com/implementing-a-teamcity-plugin/
        AgentRunningBuild agentRunningBuild = getRunnerContext().getBuild();
        // something logic with build instance

        BuildProgressLogger logger = agentRunningBuild.getBuildLogger();
        // something logic with logger instance (output information)
        Map<String, String> sharedConfigParameters = agentRunningBuild.getSharedConfigParameters();


        File workingDirectory = getWorkingDirectory(); // get working directory
        Map<String, String> runnerParameters = getRunnerParameters(); // get runner parameters

        BuildRunnerContext buildRunnerContext = getBuildRunnerContext();

        //Getting all the parameters
        scanConfig = new CheckmarxScanConfig();
        if (TRUE.equals(runnerParameters.get(USE_DEFAULT_SERVER))) {
            scanConfig.setServerUrl(validateNotEmpty(sharedConfigParameters.get(GLOBAL_AST_SERVER_URL), GLOBAL_AST_SERVER_URL));
            scanConfig.setClientId(validateNotEmpty(sharedConfigParameters.get(GLOBAL_AST_CLIENT_ID), GLOBAL_AST_CLIENT_ID));
            scanConfig.setAstSecret(PluginUtils.decrypt(validateNotEmpty(sharedConfigParameters.get(GLOBAL_AST_SECRET),GLOBAL_AST_SECRET)));
        }
        else {
            scanConfig.setServerUrl(validateNotEmpty(runnerParameters.get(SERVER_URL), SERVER_URL));
            scanConfig.setClientId(validateNotEmpty(runnerParameters.get(AST_CLIENT_ID), AST_CLIENT_ID));
            scanConfig.setAstSecret(PluginUtils.decrypt(validateNotEmpty(runnerParameters.get(AST_SECRET), AST_SECRET)));
        }


        LOG.info("-----------------------Checkmarx: Initiating the Scan Command------------------------");
        String checkmarxCliToolPath = getCheckmarxCliToolPath();

        String checkmarxAstSecret = scanConfig.getAstSecret();
        if (nullIfEmpty(checkmarxAstSecret) == null) {
            throw new RunBuildException("Checkmarx API secret was not defined. Please configure the build properly and retry.");
        }
        Map<String, String> envVars = new HashMap<>(getEnvironmentVariables());
        envVars.put("CX_CLIENT_SECRET", checkmarxAstSecret);

        ///////saving a file for mock results
        String buildTempDirectory = getBuild().getBuildTempDirectory().getAbsolutePath();
   //    String checkmarxMockReportHtml = Paths.get(buildTempDirectory, "checkmarx-mock-report.html").toFile().getAbsolutePath();

        File htmlFile = new File(buildTempDirectory, CheckmarxScanRunnerConstants.REPORT_HTML_NAME);
        try {
            FileUtils.writeStringToFile(htmlFile, PluginUtils.getHtmlText());
        } catch (IOException e) {
            logger.error("Failed to generate full html report: " + e.getMessage());
        }
        //// mock results end here


        String sourceDir = getWorkingDirectory().getAbsolutePath();
        return new SimpleProgramCommandLine(envVars, getWorkingDirectory().getAbsolutePath(), checkmarxCliToolPath, getArguments());
    }

    @Override
    public void beforeProcessStarted() {
        getBuild().getBuildLogger().message("Scanning with Checkmarx...");
    }

    @Override
    List<String> getArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.add("scan");
        arguments.add("create");

        //Set Origin
        arguments.add("--agent");
        arguments.add("TeamCity");

        String projectName = getRunnerParameters().get(PROJECT_NAME);
        if (nullIfEmpty(projectName) != null) {
            arguments.add("--project-name");
            arguments.add(projectName);
        }

        arguments.add("--sources");
        arguments.add(".");

        arguments.add("--base-uri");
        arguments.add(scanConfig.getServerUrl());

        arguments.add("--client-id");
        arguments.add(scanConfig.getClientId());

        if (TRUE.equals(getRunnerParameters().get(USE_GLOBAL_FILE_FILTERS))) {
            AgentRunningBuild agentRunningBuild = getRunnerContext().getBuild();
            scanConfig.setZipFileFilters(validateNotEmpty(agentRunningBuild.getSharedConfigParameters().get(GLOBAL_ZIP_FILTERS), GLOBAL_ZIP_FILTERS));
        }
        else {
            scanConfig.setZipFileFilters(validateNotEmpty(getRunnerParameters().get(ZIP_FILE_FILTERS), ZIP_FILE_FILTERS));
        }
        arguments.add("--filter");
        arguments.add(scanConfig.getZipFileFilters());

        String additionalParameters = getRunnerParameters().get(ADDITIONAL_PARAMETERS);
        if (nullIfEmpty(additionalParameters) != null) {
            arguments.addAll(asList(additionalParameters.split("\\s+")));
        }

        return arguments;
    }

    private static String validateNotEmpty(String param, String paramName) throws InvalidParameterException {
        if (param == null || param.length() == 0) {
            throw new InvalidParameterException("Parameter [" + paramName + "] must not be empty");
        }
        return param;
    }

}
