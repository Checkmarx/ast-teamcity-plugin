package com.checkmarx.teamcity.common;

import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildProgressLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckmarxScanCancelCommandExecutor {

    public void cancelExecution(String scanID, String cliPath, BuildProgressLogger buildProgressLogger, CheckmarxScanConfig scanConfig, Map<String, String> environmentVariables) {
        if (scanID == null || scanID.trim().isEmpty()) {
            throw new IllegalArgumentException("scanID cannot be null or empty");
        }
        if (cliPath == null || cliPath.trim().isEmpty()) {
            throw new IllegalArgumentException("cliPath cannot be null or empty");
        }
        if (buildProgressLogger == null) {
            throw new IllegalArgumentException("buildProgressLogger cannot be null");
        }
        if (scanConfig == null) {
            throw new IllegalArgumentException("scanConfig cannot be null");
        }
        if (environmentVariables == null) {
            throw new IllegalArgumentException("environmentVariables cannot be null");
        }

        buildProgressLogger.message("Cancelling Checkmarx scan for scanID " + scanID);

        try {
            List<String> arguments = populateScanCancelArguments(scanConfig, cliPath, scanID);

            Map<String, String> envVars = new HashMap<>(environmentVariables);
            envVars.put("CX_CLIENT_SECRET", scanConfig.getAstSecret());

            ProcessBuilder cancelScanProcessBuilder = new ProcessBuilder(arguments);
            cancelScanProcessBuilder.environment().putAll(envVars);

            Process process = cancelScanProcessBuilder.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

            int exitValue = process.waitFor();

            String line;
            while ((line = in.readLine()) != null) {
                buildProgressLogger.message(line);
            }
            while ((line = err.readLine()) != null) {
                buildProgressLogger.message(line);
            }
            in.close();
            err.close();
            buildProgressLogger.message("Scan cancel finish with exit code: " + exitValue);

            if (exitValue != 0) {
                throw new TeamCityRuntimeException("Scan cancellation failed with exit code: " + exitValue);
            }
        } catch (IOException | InterruptedException e) {
            buildProgressLogger.message("Error canceling: " + e.getMessage());
            throw new TeamCityRuntimeException("Unable to cancel the scan for scanID: " + scanID + ":" + e.getMessage());
        }
    }

    public List<String> populateScanCancelArguments(CheckmarxScanConfig scanConfig, String cliPath, String scanID) {
        List<String> arguments = new ArrayList<>();
        arguments.add(cliPath);
        arguments.add("scan");
        arguments.add("cancel");
        arguments.add("--scan-id");
        arguments.add(scanID);

        arguments.addAll(PluginUtils.getAuthenticationFlags(scanConfig));

        return arguments;
    }
}