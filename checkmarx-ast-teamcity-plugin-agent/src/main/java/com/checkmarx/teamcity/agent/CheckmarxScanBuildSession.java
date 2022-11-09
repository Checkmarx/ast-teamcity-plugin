package com.checkmarx.teamcity.agent;

import com.checkmarx.teamcity.agent.commands.*;
import com.checkmarx.teamcity.common.CheckmarxParams;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.CommandExecution;
import jetbrains.buildServer.agent.runner.MultiCommandBuildSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.io.File.separator;
import static java.util.Objects.requireNonNull;
import static jetbrains.buildServer.ArtifactsConstants.TEAMCITY_ARTIFACTS_DIR;

public class CheckmarxScanBuildSession implements MultiCommandBuildSession {

    private final ArtifactsWatcher artifactsWatcher;
    private final BuildRunnerContext buildRunnerContext;

    private Iterator<CommandExecutionAdapter> buildSteps;
    private CommandExecutionAdapter lastCommand;

    private List<BuildFinishedStatus> resultList  = new ArrayList<>();

    public CheckmarxScanBuildSession(@NotNull ArtifactsWatcher artifactsWatcher, @NotNull BuildRunnerContext buildRunnerContext) {
        this.artifactsWatcher = artifactsWatcher;
        this.buildRunnerContext = requireNonNull(buildRunnerContext);
    }

    @Override
    public void sessionStarted() {
        buildSteps = getBuildSteps();
    }

    @Nullable
    @Override
    public CommandExecution getNextCommand() {
        if (buildSteps.hasNext()) {
            BuildFinishedStatus lastCommandStatus = lastCommand != null ? lastCommand.getResult(): null;
            resultList.add(lastCommandStatus);
            lastCommand = buildSteps.next();
            return lastCommand;
        }
        return null;
    }

    @Nullable
    @Override
    public BuildFinishedStatus sessionFinished() {
        String buildTempDirectory = buildRunnerContext.getBuild().getBuildTempDirectory().getAbsolutePath();
        Path checkmarxScanReport = Paths.get(buildTempDirectory, CheckmarxScanRunnerConstants.REPORT_HTML_NAME);

        if (checkmarxScanReport.toFile().exists()) {
            artifactsWatcher.addNewArtifactsPath(checkmarxScanReport.toAbsolutePath().toString() + " => " + TEAMCITY_ARTIFACTS_DIR + separator + CheckmarxScanRunnerConstants.RUNNER_DISPLAY_NAME);
        }
        return resultList.contains(BuildFinishedStatus.FINISHED_FAILED) ? BuildFinishedStatus.FINISHED_FAILED : lastCommand.getResult();
    }

    private Iterator<CommandExecutionAdapter> getBuildSteps() {
        List<CommandExecutionAdapter> steps = new ArrayList<>(3);
        String buildTempDirectory = buildRunnerContext.getBuild().getBuildTempDirectory().getAbsolutePath();

        CheckmarxVersionCommand checkmarxVersionCommand = new CheckmarxVersionCommand();
        steps.add(addCommand(checkmarxVersionCommand, Paths.get(buildTempDirectory, CheckmarxScanRunnerConstants.SCAN_OUTPUT_LOG_TEXT)));


        CheckmarxScanCreateCommand checkmarxScanCreateCommand = new CheckmarxScanCreateCommand();
        steps.add(addCommand(checkmarxScanCreateCommand, Paths.get(buildTempDirectory, CheckmarxScanRunnerConstants.SCAN_OUTPUT_LOG_TEXT)));

        String additionalParameters = buildRunnerContext.getRunnerParameters().get(CheckmarxParams.ADDITIONAL_PARAMETERS);
        if (additionalParameters != null && additionalParameters.contains("--async")) {
            buildRunnerContext.getBuild().getBuildLogger().message(" =====WARNING=====");
            buildRunnerContext.getBuild().getBuildLogger().message(" Since \"--async\" is used, result summary wont be available.");
            buildRunnerContext.getBuild().getBuildLogger().message(" =================");

        } else {
            CheckmarxResultsCommand checkmarxResultsCommand = new CheckmarxResultsCommand();
            steps.add(addCommand(checkmarxResultsCommand, Paths.get(buildTempDirectory, CheckmarxScanRunnerConstants.SCAN_OUTPUT_LOG_TEXT)));
        }
        return steps.iterator();
    }

    private <T extends CheckmarxBuildServiceAdapter> CommandExecutionAdapter addCommand(T buildService, Path commandOutputPath) {
        try {
            buildService.initialize(buildRunnerContext.getBuild(), buildRunnerContext);
        } catch (RunBuildException ex) {
            throw new TeamCityRuntimeException(ex);
        }
        return new CommandExecutionAdapter(buildService, commandOutputPath);
    }
}
