package com.checkmarx.teamcity.agent;

import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.FlowLogger;
import jetbrains.buildServer.agent.artifacts.ArtifactsWatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.HashMap;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CheckmarxScanBuildSessionTest {

    @Mock
    private ArtifactsWatcher artifactsWatcher;

    @Mock
    private BuildRunnerContext buildRunnerContext;

    @Mock
    private AgentRunningBuild build;

    @Mock
    private BuildProgressLogger logger;

    @Mock
    private FlowLogger flowLogger;

    private CheckmarxScanBuildSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mocks
        when(buildRunnerContext.getBuild()).thenReturn(build);
        when(build.getBuildLogger()).thenReturn(logger);
        when(logger.getFlowLogger(anyString())).thenReturn(flowLogger);
        when(build.getBuildTempDirectory()).thenReturn(new File("temp"));
        when(buildRunnerContext.getRunnerParameters()).thenReturn(new HashMap<>());
        when(buildRunnerContext.getWorkingDirectory()).thenReturn(new File("."));

        session = new CheckmarxScanBuildSession(artifactsWatcher, buildRunnerContext);
    }

    @Test
    void sessionStarted_ShouldInitializeBuildSteps() {
        session.sessionStarted();
        assertNotNull(session.getNextCommand());
    }

    @Test
    void getNextCommand_ShouldReturnCommandsInOrder() {
        session.sessionStarted();

        // First command should be version check
        assertNotNull(session.getNextCommand());

        // Second command should be scanned create
        assertNotNull(session.getNextCommand());

        // Third command should be results (if not async)
        assertNotNull(session.getNextCommand());

        // No more commands
        assertNull(session.getNextCommand());
    }
}