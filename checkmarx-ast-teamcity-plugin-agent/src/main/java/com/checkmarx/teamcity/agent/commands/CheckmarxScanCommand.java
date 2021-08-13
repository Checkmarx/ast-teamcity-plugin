package com.checkmarx.teamcity.agent.commands;


import com.checkmarx.teamcity.common.CheckmarxScanConfig;
import com.checkmarx.teamcity.common.PluginUtils;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class CheckmarxScanCommand extends CheckmarxBuildServiceAdapter {

    private static final Logger LOG = Logger.getLogger(CheckmarxScanCommand.class);
    private static CheckmarxScanConfig scanConfig;

    private static String validateNotEmpty(String param, String paramName) throws InvalidParameterException {
        if (param == null || param.length() == 0) {
            throw new InvalidParameterException("Parameter [" + paramName + "] must not be empty");
        }
        return param;
    }

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

        scanConfig = PluginUtils.resolveConfiguration(runnerParameters, sharedConfigParameters);

        LOG.info("-----------------------Checkmarx: Initiating the Scan Command------------------------");
        String checkmarxCliToolPath = getCheckmarxCliToolPath();

        Map<String, String> envVars = new HashMap<>(getEnvironmentVariables());
        envVars.put("CX_CLIENT_SECRET", scanConfig.getAstSecret());

        String sourceDir = getWorkingDirectory().getAbsolutePath();
        return new SimpleProgramCommandLine(envVars, getWorkingDirectory().getAbsolutePath(), checkmarxCliToolPath, getArguments());
    }

    @Override
    public void beforeProcessStarted() {
        getBuild().getBuildLogger().message("Scanning with Checkmarx AST CLI ... ");
    }

    @Override
    public void afterProcessFinished() {
        getBuild().getBuildLogger().message("Scanning completed with Checkmarx AST CLI.");
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

        arguments.add("--agent");
        arguments.add("TeamCity");

        arguments.add("--project-name");
        arguments.add(scanConfig.getProjectName());

        arguments.add("--file-source");
        arguments.add(".");

        if (nullIfEmpty(scanConfig.getZipFileFilters()) != null) {
            arguments.add("--file-filter");
            arguments.add(scanConfig.getZipFileFilters());
        }

        if (nullIfEmpty(scanConfig.getAdditionalParameters()) != null) {
            arguments.addAll(asList(scanConfig.getAdditionalParameters().split("\\s+")));
        }

        return arguments;
    }

}
