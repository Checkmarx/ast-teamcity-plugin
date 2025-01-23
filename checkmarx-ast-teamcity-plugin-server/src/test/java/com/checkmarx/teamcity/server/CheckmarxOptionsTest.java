package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CheckmarxOptionsTest {

    private CheckmarxOptions options;

    @BeforeEach
    void setUp() {
        options = new CheckmarxOptions();
    }

    @Test
    void testAllGetters() {
        assertEquals(CheckmarxParams.USE_DEFAULT_SERVER, options.getUseDefaultServer());
        assertEquals(CheckmarxParams.GLOBAL_AST_SERVER_URL, options.getGlobalAstServerUrl());
        assertEquals(CheckmarxParams.GLOBAL_ADDITIONAL_PARAMETERS, options.getGlobalAdditionalParameters());
        assertEquals(CheckmarxParams.GLOBAL_AST_CLIENT_ID, options.getGlobalClientId());
        assertEquals(CheckmarxParams.SERVER_URL, options.getServerUrl());
        assertEquals(CheckmarxParams.AUTHENTICATION_URL, options.getAuthenticationUrl());
        assertEquals(CheckmarxParams.TENANT, options.getTenant());
        assertEquals(CheckmarxParams.AST_CLIENT_ID, options.getAstClientId());
        assertEquals(CheckmarxParams.AST_SECRET, options.getAstSecret());
        assertEquals(CheckmarxParams.PROJECT_NAME, options.getProjectName());
        assertEquals(CheckmarxParams.BRANCH_NAME, options.getBranchName());
        assertEquals(CheckmarxParams.USE_GLOBAL_ADDITIONAL_PARAMETERS, options.getUseGlobalAdditionalParameters());
        assertEquals(CheckmarxParams.ADDITIONAL_PARAMETERS, options.getAdditionalParameters());
    }

    @Test
    void testNoDisplay() {
        assertEquals("style='display:none'", options.getNoDisplay());
    }
}