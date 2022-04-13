package com.checkmarx.teamcity.agent;

import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import jetbrains.buildServer.agent.runner.MultiCommandBuildSession;
import jetbrains.buildServer.agent.runner.MultiCommandBuildSessionFactory;
import org.jetbrains.annotations.NotNull;

public class CheckmarxBuildSessionFactory implements MultiCommandBuildSessionFactory {

    private final ArtifactsWatcher artifactsWatcher;

    public CheckmarxBuildSessionFactory(@NotNull ArtifactsWatcher artifactsWatcher) {
        this.artifactsWatcher = artifactsWatcher;
    }

    @NotNull
    @Override
    public MultiCommandBuildSession createSession(@NotNull BuildRunnerContext buildRunnerContext) {
        return new CheckmarxScanBuildSession(artifactsWatcher, buildRunnerContext);
    }

    @NotNull
    @Override
    public AgentBuildRunnerInfo getBuildRunnerInfo() {
        return new AgentBuildRunnerInfo() {
            @NotNull
            @Override
            public String getType() {
                return CheckmarxScanRunnerConstants.RUNNER_TYPE;
            }

            @Override
            public boolean canRun(@NotNull BuildAgentConfiguration buildAgentConfiguration) {
                return true;
            }
        };
    }
}
