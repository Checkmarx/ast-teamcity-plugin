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
import java.util.List;
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
        environmentVariables.put("PATH", "/usr/local/bin");

        scanId = "1234567890abcdef1234567890abcdef12345678";
    }

    @Test
    void testSuccessfulCancelExecution(@TempDir Path tempDir) throws IOException {
        // Create a mock CLI file
        Path cliPath = tempDir.resolve("cx");
        Files.write(cliPath, "#!/bin/sh\nexit 0".getBytes());
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
        // Create a mock CLI file that exits with error
        Path cliPath = tempDir.resolve("cx");
        Files.write(cliPath, "#!/bin/sh\nexit 1".getBytes());
        File cliFile = cliPath.toFile();
        cliFile.setExecutable(true);

        // Execute and xverify exception
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

        verify(buildProgressLogger).message("Cancelling Checkmarx scan for scanID " + scanId);
        verify(buildProgressLogger).message(contains("Error canceling:"));
    }

    @Test
    void testPopulateScanCancelArguments() throws Exception {

        String cliPath = "/path/to/cx";
        List<String> arguments = executor.populateScanCancelArguments(scanConfig, cliPath, scanId);

        // Verify arguments
        assertEquals(cliPath, arguments.get(0));
        assertEquals("scan", arguments.get(1));
        assertEquals("cancel", arguments.get(2));
        assertEquals("--scan-id", arguments.get(3));
        assertEquals(scanId, arguments.get(4));
        assertEquals("--base-uri", arguments.get(5));
        assertEquals(scanConfig.getServerUrl(), arguments.get(6));
        assertEquals("--client-id", arguments.get(7));
        assertEquals(scanConfig.getClientId(), arguments.get(8));
    }
}