package com.checkmarx.teamcity.common;

import jetbrains.buildServer.TeamCityRuntimeException;
import jetbrains.buildServer.agent.BuildProgressLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckmarxScanCancelCommandExecutorTest {

    @Mock
    private BuildProgressLogger buildProgressLogger;

    private CheckmarxScanCancelCommandExecutor executor;
    private CheckmarxScanConfig scanConfig;
    private Map<String, String> environmentVariables;
    private String scanId;

    @BeforeEach
    void setUp() {
        executor = new CheckmarxScanCancelCommandExecutor();
        scanConfig = new CheckmarxScanConfig();
        scanConfig.setServerUrl("https://test.checkmarx.com");
        scanConfig.setClientId("test-client");
        scanConfig.setAstSecret("test-secret");

        environmentVariables = new HashMap<>();
        environmentVariables.put("PATH", System.getenv("PATH"));

        scanId = "1234567890abcdef1234567890abcdef12345678";
    }

    @Test
    void testSuccessfulCancelExecution(@TempDir Path tempDir) throws IOException {
        // Create a mock CLI file based on OS
        Path cliPath;
        String content;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            cliPath = tempDir.resolve("cx.bat");
            content = "@echo off\nexit 0";
        } else {
            cliPath = tempDir.resolve("cx");
            content = "#!/bin/sh\nexit 0";
        }
        
        Files.write(cliPath, content.getBytes());
        File cliFile = cliPath.toFile();
        cliFile.setExecutable(true);

        // Execute
        executor.cancelExecution(
            scanId,
            cliFile.getAbsolutePath(),
            buildProgressLogger,
            scanConfig,
            environmentVariables
        );

        // Verify
        verify(buildProgressLogger).message("Cancelling Checkmarx scan for scanID " + scanId);
        verify(buildProgressLogger).message("Scan cancel finish with exit code: 0");
    }

    @Test
    void testFailedCancelExecution(@TempDir Path tempDir) throws IOException {
        // Create a mock CLI file that exits with error based on OS
        Path cliPath;
        String content;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            cliPath = tempDir.resolve("cx.bat");
            content = "@echo off\nexit 1";
        } else {
            cliPath = tempDir.resolve("cx");
            content = "#!/bin/sh\nexit 1";
        }
        
        Files.write(cliPath, content.getBytes());
        File cliFile = cliPath.toFile();
        cliFile.setExecutable(true);

        // Execute and verify exception
        assertThrows(TeamCityRuntimeException.class, () ->
            executor.cancelExecution(
                scanId,
                cliFile.getAbsolutePath(),
                buildProgressLogger,
                scanConfig,
                environmentVariables
            )
        );

        // Verify
        verify(buildProgressLogger).message("Cancelling Checkmarx scan for scanID " + scanId);
        verify(buildProgressLogger).message("Scan cancel finish with exit code: 1");
    }

    @Test
    void testCancelExecutionWithInvalidCliPath() {
        String invalidCliPath = "/invalid/path/to/cx";

        assertThrows(TeamCityRuntimeException.class, () ->
            executor.cancelExecution(
                scanId,
                invalidCliPath,
                buildProgressLogger,
                scanConfig,
                environmentVariables
            )
        );

        // Verify
        verify(buildProgressLogger).message("Cancelling Checkmarx scan for scanID " + scanId);
        verify(buildProgressLogger).message(contains("Error canceling:"));
    }

    @Test
    void testCancelExecutionWithNullScanId() {
        assertThrows(IllegalArgumentException.class, () ->
            executor.cancelExecution(
                null,
                "some/path/to/cx",
                buildProgressLogger,
                scanConfig,
                environmentVariables
            )
        );
    }
}