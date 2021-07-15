package com.checkmarx.teamcity.agent.commands;

import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import com.checkmarx.teamcity.common.runner.Platform;
import com.checkmarx.teamcity.common.runner.RunnerVersion;
import com.checkmarx.teamcity.common.runner.Runners;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildAgentSystemInfo;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;

public abstract class CheckmarxBuildServiceAdapter extends BuildServiceAdapter {

    private static final Logger LOG = Logger.getLogger(CheckmarxBuildServiceAdapter.class);

    abstract List<String> getArguments();

    @NotNull
    public final BuildRunnerContext getBuildRunnerContext() {
        return getRunnerContext();
    }

    String getCheckmarxCliToolPath() {
        LOG.info("Getting the CLI Tool path ................");
        String version = getRunnerParameters().get(CheckmarxScanRunnerConstants.VERSION);
        RunnerVersion runner = Runners.getRunner(version);
        if (runner == null) {
            LOG.warn(format("Checkmarx CLI runner with version '%s' was not found. Default runner will be used.", version));
            version = Runners.getDefaultRunnerVersion();
            runner = Runners.getDefaultRunner();
        }

        String agentToolsDirectory = getAgentConfiguration().getAgentToolsDirectory().getAbsolutePath();
        Platform platform = detectAgentPlatform();
        Path checkmarxCLIToolPath = Paths.get(agentToolsDirectory, "checkmarx-ast-teamcity-plugin-runner", "bin", version, runner.getCheckmarxCliToolPath(platform));
        if (!checkmarxCLIToolPath.toFile().exists()) {
            throw new TeamCityRuntimeException(format("Could not found '%s'", checkmarxCLIToolPath.toString()));
        }
        return checkmarxCLIToolPath.toString();
    }

    private Platform detectAgentPlatform() {
        LOG.info("Detecting the operating system.....");
        BuildAgentSystemInfo buildAgentSystemInfo = getAgentConfiguration().getSystemInfo();
        if (buildAgentSystemInfo.isUnix() && !buildAgentSystemInfo.isMac()) {
            return Platform.LINUX;
        } else if (buildAgentSystemInfo.isMac()) {
            return Platform.MAC_OS;
        } else if (buildAgentSystemInfo.isWindows()) {
            return Platform.WINDOWS;
        } else {
            throw new TeamCityRuntimeException("Could not detect OS on build agent: " + getAgentConfiguration().getName());
        }
    }

    @Override
    public boolean isCommandLineLoggingEnabled() {
        return true;
    }

    @NotNull
    @Override
    public BuildFinishedStatus getRunResult(int exitCode) {
        if (exitCode == 0) {
            return BuildFinishedStatus.FINISHED_SUCCESS;
        }
        return getBuild().getFailBuildOnExitCode() ? BuildFinishedStatus.FINISHED_WITH_PROBLEMS : BuildFinishedStatus.FINISHED_SUCCESS;
    }
}
