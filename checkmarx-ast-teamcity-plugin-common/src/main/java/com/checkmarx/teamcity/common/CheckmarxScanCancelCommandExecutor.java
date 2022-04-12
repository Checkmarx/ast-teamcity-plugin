package com.checkmarx.teamcity.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildProgressLogger;

public class CheckmarxScanCancelCommandExecutor {

  
  public void cancelExecution(String scanID, String cliPath, BuildProgressLogger buildProgressLogger, CheckmarxScanConfig scanConfig) {
    try {
      List<String> arguments = populateScanCancelArguments(scanConfig, scanID);
      String command = cliPath + " " + String.join(" ", arguments);
      buildProgressLogger.message("Cancelling Checkmarx scan for scanID " + scanID);
      Process process = Runtime.getRuntime().exec(command);
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

  private List<String> populateScanCancelArguments(CheckmarxScanConfig scanConfig, String scanID) {
    List<String> scanArguments = new ArrayList<>();
    scanArguments.add("scan");
    scanArguments.add("cancel");
    scanArguments.add("--scan-id");
    scanArguments.add(scanID);
    scanArguments.add("--client-id");
    scanArguments.add(scanConfig.getClientId());
    scanArguments.add("--client-secret");
    scanArguments.add(scanConfig.getAstSecret());
    return scanArguments;
  }
}
