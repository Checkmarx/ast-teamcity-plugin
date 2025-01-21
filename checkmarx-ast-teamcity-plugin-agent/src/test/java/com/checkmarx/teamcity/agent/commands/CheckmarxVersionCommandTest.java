package com.checkmarx.teamcity.agent.commands;

import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.runner.BuildServiceAdapter;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckmarxVersionCommandTest {

    @Mock
    private BuildRunnerContext context;

    @Mock
    private AgentRunningBuild build;

    @Mock
    private BuildAgentConfiguration agentConfiguration;

    @Mock
    private BuildAgentSystemInfo systemInfo;

    @Mock
    private BuildProgressLogger logger;

    @Mock
    private FlowLogger flowLogger;

    private CheckmarxVersionCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mocks
        when(context.getBuild()).thenReturn(build);
        when(build.getBuildLogger()).thenReturn(logger);
        when(logger.getFlowLogger(anyString())).thenReturn(flowLogger);
        when(context.getWorkingDirectory()).thenReturn(new File("."));

        // Agent configuration mocks
        when(build.getAgentConfiguration()).thenReturn(agentConfiguration);
        when(agentConfiguration.getSystemInfo()).thenReturn(systemInfo);
        when(agentConfiguration.getAgentToolsDirectory()).thenReturn(new File("tools"));
        when(systemInfo.isWindows()).thenReturn(true);

        command = new CheckmarxVersionCommand();
        try {
            command.initialize(build, context);
        } catch (Exception e) {
            fail("Failed to initialize command: " + e.getMessage());
        }
    }

    @Test
    void isCommandLineLoggingEnabled_ShouldReturnFalse() {
        assertFalse(command.isCommandLineLoggingEnabled());
    }

    @Test
    void beforeProcessStarted_ShouldLogMessage() {
        command.beforeProcessStarted();
        verify(logger).message("Determining Checkmarx AST CLI version...");
    }
}