package com.checkmarx.teamcity.agent.commands;

import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import jetbrains.buildServer.agent.*;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckmarxScanCreateCommandTest {

    @Mock
    private BuildRunnerContext context;

    @Mock
    private AgentRunningBuild build;

    @Mock
    private BuildAgentConfiguration agentConfiguration;

    @Mock
    private BuildProgressLogger logger;

    @Mock
    private FlowLogger flowLogger;

    private CheckmarxScanCreateCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup basic mocks
        when(context.getBuild()).thenReturn(build);
        when(build.getBuildLogger()).thenReturn(logger);
        when(logger.getFlowLogger(anyString())).thenReturn(flowLogger);
        when(context.getWorkingDirectory()).thenReturn(new File("."));

        // Setup runner parameters with all required fields
        Map<String, String> runnerParams = new HashMap<>();
        runnerParams.put(CheckmarxScanRunnerConstants.SERVER_URL, "https://ast.checkmarx.net");
        runnerParams.put(CheckmarxScanRunnerConstants.PROJECT_NAME, "test-project");
        runnerParams.put(CheckmarxScanRunnerConstants.BRANCH_NAME, "main");
        runnerParams.put(CheckmarxScanRunnerConstants.AST_CLIENT_ID, "test-client-id");
        runnerParams.put(CheckmarxScanRunnerConstants.AST_SECRET, "test-client-secret");
        when(context.getRunnerParameters()).thenReturn(runnerParams);

        // Setup shared config parameters
        Map<String, String> sharedParams = new HashMap<>();
        sharedParams.put("teamcity.build.checkoutDir", context.getWorkingDirectory().getAbsolutePath());
        when(build.getSharedConfigParameters()).thenReturn(sharedParams);

        // Setup build parameters
        Map<String, String> buildParams = new HashMap<>();
        buildParams.put("teamcity.build.id", "123");
        when(build.getBuildId()).thenReturn(123L);

        // Setup agent configuration
        when(agentConfiguration.getSystemInfo()).thenReturn(mock(BuildAgentSystemInfo.class));
        when(build.getAgentConfiguration()).thenReturn(agentConfiguration);
        when(agentConfiguration.getAgentToolsDirectory()).thenReturn(new File("tools"));

        command = new CheckmarxScanCreateCommand();
        try {
            command.initialize(build, context);
        } catch (Exception e) {
            fail("Failed to initialize command: " + e.getMessage());
        }
    }

    @Test
    void beforeProcessStarted_ShouldLogMessage() {
        command.beforeProcessStarted();
        verify(logger).message("Scanning with Checkmarx AST CLI ... ");
    }

    @Test
    void afterProcessFinished_ShouldLogMessage() {
        command.afterProcessFinished();
        verify(logger).message("Scanning completed with Checkmarx AST CLI.");
    }
}