package com.checkmarx.teamcity.server;

public interface CheckmarxAdminConfigBase {
    String getConfiguration(String key);
    void setConfiguration(String key, String val);
    void persistConfiguration() throws java.io.IOException;
}