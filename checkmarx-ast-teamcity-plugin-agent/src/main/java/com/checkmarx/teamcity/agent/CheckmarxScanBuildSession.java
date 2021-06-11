package com.checkmarx.teamcity.agent;

import com.checkmarx.teamcity.agent.commands.CheckmarxBuildServiceAdapter;
import com.checkmarx.teamcity.agent.commands.CheckmarxScanCommand;
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
import static jetbrains.buildServer.util.PropertiesUtil.getBoolean;

public class CheckmarxScanBuildSession implements MultiCommandBuildSession {

    private final ArtifactsWatcher artifactsWatcher;
    private final BuildRunnerContext buildRunnerContext;

    private Iterator<CommandExecutionAdapter> buildSteps;
    private CommandExecutionAdapter lastCommand;

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
            lastCommand = buildSteps.next();
            return lastCommand;
        }
        return null;
    }

    @Nullable
    @Override
    public BuildFinishedStatus sessionFinished() {

        String buildTempDirectory = buildRunnerContext.getBuild().getBuildTempDirectory().getAbsolutePath();
        Path checkmarxScanReport = Paths.get(buildTempDirectory, "Test_Checkmarx.txt");
        artifactsWatcher.addNewArtifactsPath(checkmarxScanReport.toAbsolutePath().toString() + " => " + TEAMCITY_ARTIFACTS_DIR + separator + "Checkmarx");

        return lastCommand.getResult();
    }

    private Iterator<CommandExecutionAdapter> getBuildSteps() {
        List<CommandExecutionAdapter> steps = new ArrayList<>(3);
        String buildTempDirectory = buildRunnerContext.getBuild().getBuildTempDirectory().getAbsolutePath();

        CheckmarxScanCommand checkmarxScanCommand = new CheckmarxScanCommand();
        steps.add(addCommand(checkmarxScanCommand, Paths.get(buildTempDirectory, "Test_Checkmarx.txt")));
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
