package com.checkmarx.teamcity.common;

import jetbrains.buildServer.TeamCityRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CheckmarxScanParamRetrieverTest {

    @Test
    void testScanIDRetriever(@TempDir Path tempDir) throws IOException {
        // Create a temporary log file
        Path logFile = tempDir.resolve("test.log");
        String content = String.join("\n", Arrays.asList(
            "Some log line",
                "Scan ID:   15c6c32e-3b2e-4af2-a631-ded308de19d7 is Completed",
            "Another log line"
        ));
        Files.write(logFile, content.getBytes());

        String scanId = CheckmarxScanParamRetriever.scanIDRetriever(
            logFile.toString(),
            "Scan ID:"
        );

        assertEquals("15c6c32e-3b2e-4af2-a631-ded308de19d7", scanId);
    }

    @Test
    void testScanIDRetrieverWithNoMatch(@TempDir Path tempDir) throws IOException {
        // Create a temporary log file with no matching scan ID
        Path logFile = tempDir.resolve("test.log");
        String content = String.join("\n", Arrays.asList(
            "Some log line",
            "Another log line"
        ));
        Files.write(logFile, content.getBytes());

        String scanId = CheckmarxScanParamRetriever.scanIDRetriever(
            logFile.toString(),
            "Scan ID:"
        );

        assertNull(scanId);
    }

    @Test
    void testScanIDRetrieverWithInvalidFile() {
        assertThrows(TeamCityRuntimeException.class, () ->
            CheckmarxScanParamRetriever.scanIDRetriever(
                "non-existent-file.log",
                "Scan ID:"
            )
        );
    }

    @Test
    void testScanIDRetrieverWithLongerId(@TempDir Path tempDir) throws IOException {
        // Create a temporary log file with a longer ID that should be truncated
        Path logFile = tempDir.resolve("test.log");
        String content = String.join("\n", Arrays.asList(
            "Some log line",
            "Scan ID:   15c6c32e-3b2e-4af2-a631-ded308de19d7 is Completed",
            "Another log line"
        ));
        Files.write(logFile, content.getBytes());

        String scanId = CheckmarxScanParamRetriever.scanIDRetriever(
            logFile.toString(),
            "Scan ID:"
        );

        assertEquals("15c6c32e-3b2e-4af2-a631-ded308de19d7", scanId);
        assertEquals(36, scanId.length());
    }
}