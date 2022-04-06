package com.checkmarx.teamcity.agent.commands;

import com.checkmarx.teamcity.common.CheckmarxScanConfig;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import com.checkmarx.teamcity.common.PluginUtils;
import com.checkmarx.teamcity.common.runner.Platform;
import com.checkmarx.teamcity.common.runner.RunnerVersion;
import com.checkmarx.teamcity.common.runner.Runners;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildAgentSystemInfo;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

import java.io.File;

public abstract class CheckmarxBuildServiceAdapter extends BuildServiceAdapter {

    private static final Logger LOG = Logger.getLogger(CheckmarxBuildServiceAdapter.class);

    abstract List<String> getArguments();

    @NotNull
    public final BuildRunnerContext getBuildRunnerContext() {
        return getRunnerContext();
    }

    public String getCheckmarxCliToolPath() {
        LOG.info("Getting the CLI Tool path ................");
            String version = Runners.getDefaultRunnerVersion();
            RunnerVersion runner = Runners.getDefaultRunner();

        String agentToolsDirectory = getAgentConfiguration().getAgentToolsDirectory().getAbsolutePath();
        Platform platform = detectAgentPlatform();
        Path checkmarxCLIToolPath = Paths.get(agentToolsDirectory, "checkmarx-ast-teamcity-plugin-runner", "bin", version, runner.getCheckmarxCliToolPath(platform));
        if (!checkmarxCLIToolPath.toFile().exists()) {
            throw new TeamCityRuntimeException(format("Could not found '%s'", checkmarxCLIToolPath.toString()));
        }
        try {
            setExecutePermission(checkmarxCLIToolPath.toString());
        } catch (RunBuildException e) {
            throw new TeamCityRuntimeException("Failed to set the executable permissions" + e.getMessage());
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
        return getBuild().getFailBuildOnExitCode() ? BuildFinishedStatus.FINISHED_FAILED : BuildFinishedStatus.FINISHED_SUCCESS;
    }

    protected CheckmarxScanConfig initExecutionCall() {
        AgentRunningBuild agentRunningBuild = getRunnerContext().getBuild();
        // something logic with build instance
        // something logic with logger instance (output information)
        Map<String, String> sharedConfigParameters = agentRunningBuild.getSharedConfigParameters();

        Map<String, String> runnerParameters = getRunnerParameters(); // get runner parameters

        return PluginUtils.resolveConfiguration(runnerParameters, sharedConfigParameters);
        
    }

    void setExecutePermission(String checkmarxCliToolPath) throws RunBuildException {
        final File cxExecutable = new File(checkmarxCliToolPath);
        if (!SystemUtils.IS_OS_WINDOWS && cxExecutable.isFile()) {
            boolean result = cxExecutable.setExecutable(true, false);

            if (!result) {
                throw new RunBuildException(format("Could not set executable flag for the file: %s",
                                                   cxExecutable.getName()));
            }
        }
    }
}
