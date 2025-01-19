package com.checkmarx.teamcity.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static com.checkmarx.teamcity.common.CheckmarxParams.*;

class PluginUtilsTest {

    private Map<String, String> runnerParameters;
    private Map<String, String> sharedConfigParameters;

    @BeforeEach
    void setUp() {
        runnerParameters = new HashMap<>();
        sharedConfigParameters = new HashMap<>();
    }

    @Test
    void testEncryptDecrypt() {
        String originalPassword = "myTestPassword";
        String encrypted = PluginUtils.encrypt(originalPassword);

        assertNotEquals(originalPassword, encrypted);
        assertEquals(originalPassword, PluginUtils.decrypt(encrypted));
    }

    @Test
    void testResolveConfigurationWithDefaultServer() {
        // Setup
        runnerParameters.put(USE_DEFAULT_SERVER, TRUE);
        sharedConfigParameters.put(GLOBAL_AST_SERVER_URL, "https://global.checkmarx.net");
        sharedConfigParameters.put(GLOBAL_AST_CLIENT_ID, "global-client");
        sharedConfigParameters.put(GLOBAL_AST_SECRET, PluginUtils.encrypt("global-secret"));

        runnerParameters.put(PROJECT_NAME, "test-project");
        runnerParameters.put(BRANCH_NAME, "main");

        // Execute
        CheckmarxScanConfig config = PluginUtils.resolveConfiguration(runnerParameters, sharedConfigParameters);

        // Verify
        assertEquals("https://global.checkmarx.net", config.getServerUrl());
        assertEquals("global-client", config.getClientId());
        assertEquals("global-secret", config.getAstSecret());
        assertEquals("test-project", config.getProjectName());
        assertEquals("main", config.getBranchName());
    }

    @Test
    void testResolveConfigurationWithCustomServer() {
        // Setup
        runnerParameters.put(USE_DEFAULT_SERVER, FALSE);
        runnerParameters.put(SERVER_URL, "https://custom.checkmarx.net");
        runnerParameters.put(AST_CLIENT_ID, "custom-client");
        runnerParameters.put(AST_SECRET, PluginUtils.encrypt("custom-secret"));
        runnerParameters.put(PROJECT_NAME, "test-project");
        runnerParameters.put(BRANCH_NAME, "main");

        // Execute
        CheckmarxScanConfig config = PluginUtils.resolveConfiguration(runnerParameters, sharedConfigParameters);

        // Verify
        assertEquals("https://custom.checkmarx.net", config.getServerUrl());
        assertEquals("custom-client", config.getClientId());
        assertEquals("custom-secret", config.getAstSecret());
    }

    @Test
    void testValidateNotEmptyThrowsException() {
        assertThrows(InvalidParameterException.class, () ->
            PluginUtils.resolveConfiguration(new HashMap<>(), new HashMap<>())
        );
    }

    @Test
    void testGetAuthenticationFlags() {
        // Setup
        CheckmarxScanConfig config = new CheckmarxScanConfig();
        config.setServerUrl("https://test.checkmarx.net");
        config.setClientId("test-client");
        config.setAuthenticationUrl("https://auth.checkmarx.net");
        config.setTenant("test-tenant");

        // Execute
        List<String> flags = PluginUtils.getAuthenticationFlags(config);

        // Verify
        assertTrue(flags.contains("--base-uri"));
        assertTrue(flags.contains("https://test.checkmarx.net"));
        assertTrue(flags.contains("--base-auth-uri"));
        assertTrue(flags.contains("https://auth.checkmarx.net"));
        assertTrue(flags.contains("--tenant"));
        assertTrue(flags.contains("test-tenant"));
        assertTrue(flags.contains("--client-id"));
        assertTrue(flags.contains("test-client"));
    }
}