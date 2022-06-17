package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxParams;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.util.PropertiesUtil;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class CheckmarxAdminConfig {
    private final ServerPaths serverPaths;
    private final Properties properties = new Properties();
    private static final Logger log = Logger.getLogger(CheckmarxAdminConfig.class);


    public CheckmarxAdminConfig(@NotNull final ServerPaths serverPaths) throws IOException {
        this.serverPaths = serverPaths;

        File configFile = getConfigFile();
        if (!configFile.isFile()) {
            initConfigFile(configFile);
        }
        loadConfiguration(configFile);
    }

    private void initConfigFile(@NotNull final File configFile) throws IOException {

        for (String conf : CheckmarxParams.GLOBAL_CONFIGS) {
            this.properties.put(conf, "");
        }

        this.properties.put(CheckmarxParams.GLOBAL_AST_SERVER_URL, "");
        this.properties.put(CheckmarxParams.GLOBAL_AST_AUTHENTICATION_URL, "");
        this.properties.put(CheckmarxParams.GLOBAL_AST_TENANT, "");
        this.properties.put(CheckmarxParams.GLOBAL_AST_CLIENT_ID, "");
        this.properties.put(CheckmarxParams.GLOBAL_AST_SECRET, "");
        this.properties.put(CheckmarxParams.GLOBAL_ADDITIONAL_PARAMETERS, "");

        PropertiesUtil.storeProperties(this.properties, configFile, "");
    }

    private void loadConfiguration(@NotNull File configFile) throws IOException {
        try (FileReader fileReader = new FileReader(configFile, StandardCharsets.UTF_8)) {
            this.properties.load(fileReader);
            for (String conf : CheckmarxParams.GLOBAL_CONFIGS) {
                if (this.properties.get(conf) == null) {
                    this.properties.put(conf, "");
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            log.error(fileNotFoundException.getMessage());
        }
    }

    private File getConfigFile() {
        return new File(this.serverPaths.getConfigDir(), "checkmarx-ast-plugin.properties");
    }

    public void persistConfiguration() throws IOException {
        PropertiesUtil.storeProperties(this.properties, getConfigFile(), "");
    }

    public String getConfiguration(String key) {
        return this.properties.get(key).toString();
    }

    public void setConfiguration(String key, String val) {
        this.properties.put(key, val);
    }
}
