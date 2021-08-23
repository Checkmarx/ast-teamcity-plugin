package com.checkmarx.teamcity.agent.commands;

import com.checkmarx.teamcity.common.CheckmarxScanConfig;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import com.checkmarx.teamcity.common.PluginUtils;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static java.lang.String.format;

public class CheckmarxResultsCommand extends CheckmarxBuildServiceAdapter {

    private static CheckmarxScanConfig scanConfig;
    private static String scanId;


    @Override
    public void beforeProcessStarted() {
        getBuild().getBuildLogger().message("Generating Checkmarx AST Scan summary...");
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

        Map<String, String> runnerParameters = getRunnerParameters(); // get runner parameters

        scanConfig = PluginUtils.resolveConfiguration(runnerParameters, sharedConfigParameters);

        Map<String, String> envVars = new HashMap<>(getEnvironmentVariables());
        envVars.put("CX_CLIENT_SECRET", scanConfig.getAstSecret());

        /////saving a file for  results
        String buildTempDirectory = getBuild().getBuildTempDirectory().getAbsolutePath();
        File astScanOutput = Paths.get(buildTempDirectory, CheckmarxScanRunnerConstants.SCAN_OUTPUT_LOG_TEXT).toFile();

        if (!astScanOutput.exists()) {
            throw new TeamCityRuntimeException(format("Cannot find the file '%s'", astScanOutput.toPath().toString()));
        } else {
            try {
                scanId = parseTheOutputForScanId(astScanOutput);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String checkmarxCliToolPath = getCheckmarxCliToolPath();

        return new SimpleProgramCommandLine(envVars,
                getWorkingDirectory().getAbsolutePath(),
                checkmarxCliToolPath,
                getArguments());

    }

    private String parseTheOutputForScanId(File astScanOutput) throws IOException {

        String searchString = "Scan ID:";
        String scanId = "";

        try {
            Scanner scanner = new Scanner(astScanOutput);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int searchIndex = line.indexOf(searchString);

                if (searchIndex != -1) {

                    String uuidSubstring = line.substring(searchIndex, (searchIndex + 50));
                    int colonIndex = uuidSubstring.indexOf(':');
                    scanId = (uuidSubstring.substring(colonIndex + 1)).trim();

                }
            }
        } catch (FileNotFoundException e) {
            throw new TeamCityRuntimeException(format("Cannot find the file '%s'", astScanOutput.toPath().toString()));
        }
        return scanId;
    }


    @Override
    List<String> getArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.add("result");
        arguments.add("summary");

        arguments.add("--base-uri");
        arguments.add(scanConfig.getServerUrl());

        arguments.add("--client-id");
        arguments.add(scanConfig.getClientId());

        arguments.add("--scan-id");
        arguments.add(scanId);


        String buildTempDirectory = getBuild().getBuildTempDirectory().getAbsolutePath();
        File htmlFile = new File(buildTempDirectory, CheckmarxScanRunnerConstants.REPORT_HTML_NAME);
        String reportFilePath = htmlFile.toPath().toAbsolutePath().toString();

        arguments.add("--target");
        arguments.add(reportFilePath);

        return arguments;
    }
}
