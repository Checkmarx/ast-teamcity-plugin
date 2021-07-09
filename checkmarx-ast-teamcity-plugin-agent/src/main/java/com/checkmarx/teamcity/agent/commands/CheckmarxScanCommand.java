package com.checkmarx.teamcity.agent.commands;


import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants.*;
import static java.util.Arrays.asList;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class CheckmarxScanCommand extends CheckmarxBuildServiceAdapter {

    private static final Logger LOG = Logger.getLogger(CheckmarxScanCommand.class);

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {

        LOG.info("-----------------------Checkmarx: Reached the BuildServiceAdapter------------------------");
        String checkmarxCliToolPath = getCheckmarxCliToolPath();

        String checkmarxAstSecret = getRunnerParameters().get(AST_SECRET);
        if (nullIfEmpty(checkmarxAstSecret) == null) {
            throw new RunBuildException("Checkmarx API key was not defined. Please configure the build properly and retry.");
        }
        Map<String, String> envVars = new HashMap<>(getEnvironmentVariables());
        envVars.put("CX_APIKEY", checkmarxAstSecret);

        //  boolean result = submitDetailsToWrapper(LOG, checkmarxApiKey, checkmarxCliToolPath, (getWorkingDirectory().getAbsolutePath()));

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

        String serverUrl = getRunnerParameters().get(SERVER_URL);
        if (nullIfEmpty(serverUrl) != null) {
            arguments.add("--base-uri");
            arguments.add(serverUrl);
        }

        String projectName = getRunnerParameters().get(PROJECT_NAME);
        if (nullIfEmpty(projectName) != null) {
            arguments.add("--project-name");
            arguments.add(projectName);
        }

        arguments.add("--sources");
        arguments.add(".");

        arguments.add("--scan-types");
        arguments.add("sast");

        String apiKey = getRunnerParameters().get(AST_SECRET);
        if (nullIfEmpty(apiKey) != null) {
            arguments.add("--apikey");
            arguments.add(apiKey);
        }

        String fileFilters = getRunnerParameters().get(ZIP_FILE_FILTERS);
        if (nullIfEmpty(fileFilters) != null) {
            arguments.add("--filter");
            arguments.add(fileFilters);
        }

        String additionalParameters = getRunnerParameters().get(ADDITIONAL_PARAMETERS);
        if (nullIfEmpty(additionalParameters) != null) {
            arguments.addAll(asList(additionalParameters.split("\\s+")));
        }



//
//        String severityThreshold = getRunnerParameters().get(ZIP_FILE_FILTERS);
//        arguments.add("--filters " + "\""+ severityThreshold + "\"");
//

//
//        String additionalParameters = getRunnerParameters().get(ADDITIONAL_PARAMETERS);
//        if (nullIfEmpty(additionalParameters) != null) {
//            arguments.addAll(asList(additionalParameters.split("\\s+")));
//        }

        return arguments;
    }

}
