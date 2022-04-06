package com.checkmarx.teamcity.agent.commands;

import com.checkmarx.teamcity.common.CheckmarxScanParamRetriever;
import com.checkmarx.teamcity.common.CheckmarxScanConfig;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.String.format;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class CheckmarxResultsCommand extends CheckmarxBuildServiceAdapter {
    private static final Logger LOG = Logger.getLogger(CheckmarxResultsCommand.class);

    private static CheckmarxScanConfig scanConfig;
    private static String scanId;


    @Override
    public void beforeProcessStarted() {
        getBuild().getBuildLogger().message("Generating Checkmarx AST Scan summary...");
    }

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {

        scanConfig = initExecutionCall();
        Map<String, String> envVars = new HashMap<>(getEnvironmentVariables());
        envVars.put("CX_CLIENT_SECRET", scanConfig.getAstSecret());

        /////saving a file for  results
        String buildTempDirectory = getBuild().getBuildTempDirectory().getAbsolutePath();
        Path astScanOutput = Paths.get(buildTempDirectory, CheckmarxScanRunnerConstants.SCAN_OUTPUT_LOG_TEXT);
        if (!astScanOutput.toFile().exists()) {
            throw new TeamCityRuntimeException(format("Cannot find the file '%s'", astScanOutput.toString()));
        } else {
            scanId = CheckmarxScanParamRetriever.scanIDRetriever(astScanOutput.toString(),"Scan ID:");
            LOG.warn("scanId retrieved for results: " + scanId);
        }

        String checkmarxCliToolPath = getCheckmarxCliToolPath();

        return new SimpleProgramCommandLine(envVars,
                getWorkingDirectory().getAbsolutePath(),
                checkmarxCliToolPath,
                getArguments());
    }

    @Override
    List<String> getArguments() {
        List<String> arguments = new ArrayList<>();
        arguments.add("results");
        arguments.add("show");

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

        arguments.add("--scan-id");
        arguments.add(scanId);

        arguments.add("--report-format");
        arguments.add("summaryHTML");

        arguments.add("--output-name");
        arguments.add(CheckmarxScanRunnerConstants.REPORT_NAME);

        String buildTempDirectory = getBuild().getBuildTempDirectory().getAbsolutePath();
        arguments.add("--output-path");
        arguments.add(buildTempDirectory);

        return arguments;
    }
}
