package com.checkmarx.teamcity.common;

import java.io.IOException;

import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildProgressLogger;

public class CheckmarxScanCancelCommandExecutor {
  
  public void cancelExecution(String scanID, String cliPath, String cliArgs, BuildProgressLogger buildProgressLogger) {
    try {
      String command = cliPath + " " + cliArgs + " " + scanID;
      buildProgressLogger.message("Cancelling Checkmarx scan with command: " + command);
      Process process = Runtime.getRuntime().exec(command);
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      throw new TeamCityRuntimeException("Unable to cancel the scan for scanID: " + scanID + ":" + e.getMessage());
    }
   }
}
