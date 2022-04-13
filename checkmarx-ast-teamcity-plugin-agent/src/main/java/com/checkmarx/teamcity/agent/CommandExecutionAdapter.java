package com.checkmarx.teamcity.agent;

import com.checkmarx.teamcity.agent.commands.CheckmarxBuildServiceAdapter;
import com.checkmarx.teamcity.common.CheckmarxScanCancelCommandExecutor;
import com.checkmarx.teamcity.common.CheckmarxScanConfig;
import com.checkmarx.teamcity.common.CheckmarxScanParamRetriever;
import com.checkmarx.teamcity.common.PluginUtils;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.runner.CommandExecution;
import jetbrains.buildServer.agent.runner.ProcessListener;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.TerminationAction;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class CommandExecutionAdapter implements CommandExecution {

    private static final Logger LOG = Logger.getLogger(CommandExecutionAdapter.class);

    private final CheckmarxBuildServiceAdapter buildService;
    private final Path commandOutputPath;
    private List<ProcessListener> listeners;
    private BuildFinishedStatus result;
    private static final  String SCAN_ID_SEARCH_TEXT = "Scan ID";

    public CommandExecutionAdapter(@NotNull CheckmarxBuildServiceAdapter buildService, @NotNull Path commandOutputPath) {
        this.buildService = buildService;
        this.commandOutputPath = commandOutputPath;
        listeners = buildService.getListeners();
    }

    @NotNull
    BuildFinishedStatus getResult() {
        return result;
    }

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        return buildService.makeProgramCommandLine();
    }

    @Override
    public void beforeProcessStarted() throws RunBuildException {
        buildService.beforeProcessStarted();
    }

    @NotNull
    @Override
    public TerminationAction interruptRequested() {
        buildService.getLogger().warning("Terminating the scan, sending the cancel request");
        terminateScan();
        return buildService.interrupt();
    }

    private void terminateScan() {
        Map<String,String> runnerParameters = buildService.getBuildRunnerContext().getRunnerParameters();
        Map<String,String> sharedConfigParameters = buildService.getBuildRunnerContext().getBuild().getSharedConfigParameters();
        Map<String,String> environmentVariables = buildService.getAgentConfiguration().getBuildParameters().getEnvironmentVariables();
        CheckmarxScanConfig scanConfig = PluginUtils.resolveConfiguration(runnerParameters, sharedConfigParameters);
        String scanId = CheckmarxScanParamRetriever.scanIDRetriever(commandOutputPath.toString(),SCAN_ID_SEARCH_TEXT);

        CheckmarxScanCancelCommandExecutor cancelCommand = new CheckmarxScanCancelCommandExecutor();
        cancelCommand.cancelExecution(scanId, buildService.getCheckmarxCliToolPath(), buildService.getLogger(), scanConfig, environmentVariables);
    }


    @Override
    public boolean isCommandLineLoggingEnabled() {
        return buildService.isCommandLineLoggingEnabled();
    }

    @Override
    public void onStandardOutput(@NotNull String text) {

        buildService.getBuildRunnerContext().getBuild().getBuildLogger().message(text);

        if (nullIfEmpty(text) == null) {
            return;
        }

        try {
            Files.write(commandOutputPath, text.getBytes(UTF_8), CREATE, APPEND);
        } catch (IOException ex) {
            throw new TeamCityRuntimeException(format("Could not write output into '%s'", commandOutputPath.toString()), ex);
        }
    }

    @Override
    public void onErrorOutput(@NotNull String text) {
        listeners.forEach(processListener -> processListener.onErrorOutput(text));
    }

    @Override
    public void processStarted(@NotNull String programCommandLine, @NotNull File workingDirectory) {
        listeners.forEach(processListener -> processListener.processStarted(programCommandLine, workingDirectory));
    }

    @Override
    public void processFinished(int exitCode) {
        try {
            buildService.afterProcessFinished();
            listeners.forEach(processListener -> processListener.processFinished(exitCode));
            result = buildService.getRunResult(exitCode);
            if (result == BuildFinishedStatus.FINISHED_SUCCESS) {
                buildService.afterProcessSuccessfullyFinished();
            }
            
        } catch (RunBuildException ex) {
            buildService.getLogger().warning(ex.getMessage());
            LOG.error(ex);
        }

    }

}
