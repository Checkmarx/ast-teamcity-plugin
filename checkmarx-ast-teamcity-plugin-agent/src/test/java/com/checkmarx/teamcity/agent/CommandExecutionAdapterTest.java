package com.checkmarx.teamcity.agent;

import com.checkmarx.teamcity.agent.commands.CheckmarxBuildServiceAdapter;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.BuildFinishedStatus;
import jetbrains.buildServer.agent.BuildRunnerContext;
import jetbrains.buildServer.agent.AgentRunningBuild;
import jetbrains.buildServer.agent.BuildProgressLogger;
import jetbrains.buildServer.agent.FlowLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CommandExecutionAdapterTest {

    @Mock
    private CheckmarxBuildServiceAdapter buildService;

    @Mock
    private BuildRunnerContext context;

    @Mock
    private AgentRunningBuild build;

    @Mock
    private BuildProgressLogger logger;

    @Mock
    private FlowLogger flowLogger;

    private Path commandOutputPath;
    private CommandExecutionAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mocks
        when(buildService.getBuildRunnerContext()).thenReturn(context);
        when(context.getBuild()).thenReturn(build);
        when(build.getBuildLogger()).thenReturn(logger);
        when(logger.getFlowLogger(anyString())).thenReturn(flowLogger);
        when(buildService.getListeners()).thenReturn(new ArrayList<>());

        commandOutputPath = Paths.get("temp", "output.txt");
        adapter = new CommandExecutionAdapter(buildService, commandOutputPath);
    }

    @Test
    void processFinished_ShouldSetSuccessStatus() throws RunBuildException {
        when(buildService.getRunResult(0)).thenReturn(BuildFinishedStatus.FINISHED_SUCCESS);

        adapter.processFinished(0);

        assertEquals(BuildFinishedStatus.FINISHED_SUCCESS, adapter.getResult());
        verify(buildService).afterProcessSuccessfullyFinished();
    }

    @Test
    void processFinished_ShouldSetFailedStatus() throws RunBuildException {
        when(buildService.getRunResult(1)).thenReturn(BuildFinishedStatus.FINISHED_FAILED);

        adapter.processFinished(1);

        assertEquals(BuildFinishedStatus.FINISHED_FAILED, adapter.getResult());
        verify(buildService, never()).afterProcessSuccessfullyFinished();
    }
}