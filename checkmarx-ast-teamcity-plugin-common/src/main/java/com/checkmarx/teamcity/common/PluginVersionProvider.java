package com.checkmarx.teamcity.common;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PluginVersionProvider {

    private static final Logger LOG = Logger.getLogger(PluginVersionProvider.class);
    private static final String VERSION;

    static {
        VERSION = loadVersion();
    }

    private static String loadVersion() {
        try (InputStream is = PluginVersionProvider.class.getResourceAsStream("/version.properties")) {
            if (is == null) {
                LOG.warn("version.properties not found in classpath");
                return "";
            }
            Properties props = new Properties();
            props.load(is);
            String version = props.getProperty("plugin.version", "");
            LOG.info("Plugin version loaded: " + version);
            return version;
        } catch (IOException e) {
            LOG.warn("Failed to load plugin version: " + e.getMessage(), e);
            return "";
        }
    }

    public static String getPluginVersion() {
        return VERSION;
    }
}
