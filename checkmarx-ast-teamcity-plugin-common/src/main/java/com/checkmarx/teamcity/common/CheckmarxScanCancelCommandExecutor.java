package com.checkmarx.teamcity.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.intellij.util.containers.hash.HashMap;

import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildProgressLogger;

public class CheckmarxScanCancelCommandExecutor {

  public void cancelExecution(String scanID, String cliPath, BuildProgressLogger buildProgressLogger, CheckmarxScanConfig scanConfig, Map<String, String> environmentVariables) {
    try {
      Map<String,String> envVars = populateEnvironmentvariables(environmentVariables);
      List<String> arguments = populateScanCancelArguments(scanConfig, scanID,buildProgressLogger,envVars);
      String command = cliPath + " " + String.join(" ", arguments);
      buildProgressLogger.message("Cancelling Checkmarx scan for scanID " + scanID);
      ProcessBuilder cancelScanProcessBuilder = new ProcessBuilder(command.split(" "));
      cancelScanProcessBuilder.environment().putAll(envVars);
      //Process process = Runtime.getRuntime().exec(command);
      Process process = cancelScanProcessBuilder.start();
      BufferedReader in = new BufferedReader(new
              InputStreamReader(process.getInputStream()));
      BufferedReader err = new BufferedReader(new
              InputStreamReader(process.getErrorStream()));
      int exitValue = process.waitFor();

      String line = null;
      while ((line = in.readLine()) != null) {
        buildProgressLogger.message(line);
      }
      while ((line = err.readLine()) != null) {
        buildProgressLogger.message(line);
      }

      buildProgressLogger.message("Cancel finish with exit code: " + exitValue);
    } catch (IOException | InterruptedException e) {
      buildProgressLogger.message("Error canceling: " + e.getMessage());
      throw new TeamCityRuntimeException("Unable to cancel the scan for scanID: " + scanID + ":" + e.getMessage());
    }
   }

  private Map<String, String> populateEnvironmentvariables(Map<String, String> environmentVariables) {
    if(environmentVariables != null) {
      Map<String,String> envVars = new HashMap<>();
      envVars.putAll(environmentVariables);
      envVars.put("CX_CLIENT_ID", environmentVariables.get("CX_CLIENT_ID"));
      envVars.put("CX_CLIENT_SECRET", environmentVariables.get("CX_CLIENT_SECRET"));
      return envVars;
    }
    else{
    return new HashMap<>();
    }
  }

  private List<String> populateScanCancelArguments(CheckmarxScanConfig scanConfig, String scanID, BuildProgressLogger buildProgressLogger, Map<String, String> envVars) {
    List<String> scanArguments = new ArrayList<>();
    scanArguments.add("scan");
    scanArguments.add("cancel");
    scanArguments.add("--scan-id");
    scanArguments.add(scanID);
    if(envVars == null) {
      buildProgressLogger.message("Recived empty environment variables, hence setting it to inline parameters");
      scanArguments.add("--client-id");
      scanArguments.add(scanConfig.getClientId());
      scanArguments.add("--client-secret");
      scanArguments.add(scanConfig.getAstSecret());
    }
    return scanArguments;
  }
}
