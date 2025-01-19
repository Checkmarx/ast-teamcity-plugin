package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxParams;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.serverSide.RunTypeRegistry;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckmarxScanRunTypeTest {

    @Mock
    private PluginDescriptor pluginDescriptor;

    @Mock
    private RunTypeRegistry runTypeRegistry;

    private CheckmarxScanRunType scanRunType;

    @BeforeEach
    void setUp() {
        scanRunType = new CheckmarxScanRunType(runTypeRegistry, pluginDescriptor);
        verify(runTypeRegistry).registerRunType(scanRunType);
    }

    @Test
    void testRunTypeBasicProperties() {
        when(pluginDescriptor.getPluginResourcesPath("editCheckmarxScanRunnerParameters.jsp"))
            .thenReturn("checkmarx/editScan.jsp");
        when(pluginDescriptor.getPluginResourcesPath("viewCheckmarxScanRunnerParameters.jsp"))
            .thenReturn("checkmarx/viewScan.jsp");

        assertEquals(CheckmarxScanRunnerConstants.RUNNER_TYPE, scanRunType.getType());
        assertEquals(CheckmarxScanRunnerConstants.RUNNER_DISPLAY_NAME, scanRunType.getDisplayName());
        assertEquals(CheckmarxScanRunnerConstants.RUNNER_DESCRIPTION, scanRunType.getDescription());
        assertEquals("checkmarx/editScan.jsp", scanRunType.getEditRunnerParamsJspFilePath());
        assertEquals("checkmarx/viewScan.jsp", scanRunType.getViewRunnerParamsJspFilePath());
    }

    @Test
    void testGetDefaultRunnerProperties() {
        Map<String, String> defaults = scanRunType.getDefaultRunnerProperties();
        assertNotNull(defaults);
        assertTrue(defaults.isEmpty());
    }

    @Test
    void testPropertiesProcessorWithNullProperties() {
        PropertiesProcessor processor = scanRunType.getRunnerPropertiesProcessor();
        Collection<InvalidProperty> errors = processor.process(null);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testPropertiesProcessorWithValidCustomServerProperties() {
        PropertiesProcessor processor = scanRunType.getRunnerPropertiesProcessor();

        Map<String, String> properties = new HashMap<>();
        properties.put(CheckmarxParams.USE_DEFAULT_SERVER, "false");
        properties.put(CheckmarxParams.SERVER_URL, "https://test.checkmarx.com");
        properties.put(CheckmarxParams.BRANCH_NAME, "main");

        Collection<InvalidProperty> errors = processor.process(properties);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testPropertiesProcessorWithMissingServerUrl() {
        PropertiesProcessor processor = scanRunType.getRunnerPropertiesProcessor();

        Map<String, String> properties = new HashMap<>();
        properties.put(CheckmarxParams.USE_DEFAULT_SERVER, "false");
        properties.put(CheckmarxParams.BRANCH_NAME, "main");

        Collection<InvalidProperty> errors = processor.process(properties);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream()
            .anyMatch(error -> error.getPropertyName().equals(CheckmarxParams.SERVER_URL)));
    }

    @Test
    void testPropertiesProcessorWithMissingBranchName() {
        PropertiesProcessor processor = scanRunType.getRunnerPropertiesProcessor();

        Map<String, String> properties = new HashMap<>();
        properties.put(CheckmarxParams.USE_DEFAULT_SERVER, "false");
        properties.put(CheckmarxParams.SERVER_URL, "https://test.checkmarx.com");

        Collection<InvalidProperty> errors = processor.process(properties);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream()
            .anyMatch(error -> error.getPropertyName().equals(CheckmarxParams.BRANCH_NAME)));
    }

    @Test
    void testPropertiesProcessorWithDefaultServer() {
        PropertiesProcessor processor = scanRunType.getRunnerPropertiesProcessor();

        Map<String, String> properties = new HashMap<>();
        properties.put(CheckmarxParams.USE_DEFAULT_SERVER, CheckmarxScanRunnerConstants.TRUE);
        properties.put(CheckmarxParams.BRANCH_NAME, "main");

        Collection<InvalidProperty> errors = processor.process(properties);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testPropertiesProcessorWithDefaultServerMissingBranch() {
        PropertiesProcessor processor = scanRunType.getRunnerPropertiesProcessor();

        Map<String, String> properties = new HashMap<>();
        properties.put(CheckmarxParams.USE_DEFAULT_SERVER, CheckmarxScanRunnerConstants.TRUE);

        Collection<InvalidProperty> errors = processor.process(properties);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream()
            .anyMatch(error -> error.getPropertyName().equals(CheckmarxParams.BRANCH_NAME)));
    }
}