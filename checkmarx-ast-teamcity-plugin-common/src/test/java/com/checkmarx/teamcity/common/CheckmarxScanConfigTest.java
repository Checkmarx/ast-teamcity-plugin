package com.checkmarx.teamcity.common;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckmarxScanConfigTest {

    @Test
    void testAdditionalParametersParser() {
        CheckmarxScanConfig config = new CheckmarxScanConfig();

        // Test with quoted and unquoted parameters
        config.setAdditionalParameters("--param1 value1 --param2 \"value 2\" --flag");

        List<String> params = config.getAdditionalParameters();
        assertEquals(5, params.size());
        assertEquals("--param1", params.get(0));
        assertEquals("value1", params.get(1));
        assertEquals("--param2", params.get(2));
        assertEquals("value 2", params.get(3));
        assertEquals("--flag", params.get(4));
    }

    @Test
    void testEmptyAdditionalParameters() {
        CheckmarxScanConfig config = new CheckmarxScanConfig();
        config.setAdditionalParameters("");

        List<String> params = config.getAdditionalParameters();
        assertTrue(params.isEmpty());
    }

    @Test
    void testNullAdditionalParameters() {
        CheckmarxScanConfig config = new CheckmarxScanConfig();
        config.setAdditionalParameters(null);

        List<String> params = config.getAdditionalParameters();
        assertTrue(params.isEmpty());
    }

    @Test
    void testBasicGettersAndSetters() {
        CheckmarxScanConfig config = new CheckmarxScanConfig();

        config.setServerUrl("https://test.checkmarx.net");
        config.setAuthenticationUrl("https://auth.checkmarx.net");
        config.setTenant("test-tenant");
        config.setProjectName("test-project");
        config.setBranchName("main");
        config.setClientId("test-client");
        config.setAstSecret("test-secret");

        assertEquals("https://test.checkmarx.net", config.getServerUrl());
        assertEquals("https://auth.checkmarx.net", config.getAuthenticationUrl());
        assertEquals("test-tenant", config.getTenant());
        assertEquals("test-project", config.getProjectName());
        assertEquals("main", config.getBranchName());
        assertEquals("test-client", config.getClientId());
        assertEquals("test-secret", config.getAstSecret());
    }
}