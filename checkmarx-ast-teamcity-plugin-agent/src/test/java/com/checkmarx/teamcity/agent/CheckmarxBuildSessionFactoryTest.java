package com.checkmarx.teamcity.agent;

import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import jetbrains.buildServer.agent.AgentBuildRunnerInfo;
import jetbrains.buildServer.agent.BuildAgentConfiguration;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class CheckmarxBuildSessionFactoryTest {

    @Mock
    private ArtifactsWatcher artifactsWatcher;

    @Mock
    private BuildRunnerContext buildRunnerContext;

    @Mock
    private BuildAgentConfiguration buildAgentConfiguration;

    private CheckmarxBuildSessionFactory factory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        factory = new CheckmarxBuildSessionFactory(artifactsWatcher);
    }

    @Test
    void createSession_ShouldReturnNewSession() {
        assertNotNull(factory.createSession(buildRunnerContext));
    }

    @Test
    void getBuildRunnerInfo_ShouldReturnCorrectType() {
        AgentBuildRunnerInfo info = factory.getBuildRunnerInfo();
        assertEquals(CheckmarxScanRunnerConstants.RUNNER_TYPE, info.getType());
    }

    @Test
    void getBuildRunnerInfo_ShouldAlwaysBeAbleToRun() {
        AgentBuildRunnerInfo info = factory.getBuildRunnerInfo();
        assertTrue(info.canRun(buildAgentConfiguration));
    }
}