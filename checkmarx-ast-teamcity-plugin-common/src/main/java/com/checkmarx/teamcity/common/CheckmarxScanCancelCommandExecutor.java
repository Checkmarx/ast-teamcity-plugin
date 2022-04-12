package com.checkmarx.teamcity.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildProgressLogger;

public class CheckmarxScanCancelCommandExecutor {

  private static final String CLI_ARGUMENTS = "scan cancel --scan-id";
  
  public void cancelExecution(String scanID, String cliPath, BuildProgressLogger buildProgressLogger) {
    try {
      String command = cliPath + " " + CLI_ARGUMENTS + " " + scanID;
      buildProgressLogger.message("Cancelling Checkmarx scan with command: " + command);
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
}
