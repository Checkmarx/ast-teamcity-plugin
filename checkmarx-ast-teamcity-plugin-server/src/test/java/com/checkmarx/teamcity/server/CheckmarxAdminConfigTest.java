package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxParams;
import com.checkmarx.teamcity.common.PluginUtils;
import jetbrains.buildServer.serverSide.ServerPaths;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;

public class CheckmarxAdminConfigTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testGlobalSettingsPersistence() throws IOException {

        String actualAstServerUrl = "http://testServerUrl";
        String actualAstAuthenticationUrl = "http://testAuthUrl";
        String actualAstClientId = "testClientId";
        String actualAstSecret = "testSecret";
        String actualAstTenant = "testTenant";
        String actualZipFilters = "testFilter";

        String systemDir = (tempFolder.newFolder("systemDir")).toString();
        String configDir = (tempFolder.newFolder("configDir")).toString();
        String backupDir = (tempFolder.newFolder("backupDir")).toString();
        String importDir = (tempFolder.newFolder("importDir")).toString();


        CheckmarxAdminConfig adminConfig = new CheckmarxAdminConfig(new ServerPaths(systemDir, configDir, backupDir, importDir));


        adminConfig.setConfiguration(CheckmarxParams.GLOBAL_AST_SERVER_URL, actualAstServerUrl);
        adminConfig.setConfiguration(CheckmarxParams.GLOBAL_AST_AUTHENTICATION_URL, actualAstAuthenticationUrl);
        adminConfig.setConfiguration(CheckmarxParams.GLOBAL_AST_CLIENT_ID, actualAstClientId);
        adminConfig.setConfiguration(CheckmarxParams.GLOBAL_AST_SECRET, PluginUtils.encrypt(actualAstSecret));
        adminConfig.setConfiguration(CheckmarxParams.GLOBAL_AST_TENANT, actualAstTenant);

        adminConfig.persistConfiguration();

        Assert.assertEquals(adminConfig.getConfiguration(CheckmarxParams.GLOBAL_AST_SERVER_URL), actualAstServerUrl);
        Assert.assertEquals(adminConfig.getConfiguration(CheckmarxParams.GLOBAL_AST_AUTHENTICATION_URL), actualAstAuthenticationUrl);
        Assert.assertEquals(adminConfig.getConfiguration(CheckmarxParams.GLOBAL_AST_CLIENT_ID), actualAstClientId);
        Assert.assertEquals(PluginUtils.decrypt(adminConfig.getConfiguration(CheckmarxParams.GLOBAL_AST_SECRET)), actualAstSecret);
        Assert.assertEquals(adminConfig.getConfiguration(CheckmarxParams.GLOBAL_AST_TENANT), actualAstTenant);

    }
}
