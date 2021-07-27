package com.checkmarx.teamcity.server;

import org.jetbrains.annotations.NotNull;
import com.checkmarx.teamcity.common.CheckmarxScanRunnerConstants.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.checkmarx.teamcity.common.CheckmarxParams.*;


public class CheckmarxOptions {

    public static final Logger log = LoggerFactory.getLogger(CheckmarxOptions.class);

    @NotNull
    public String getUseDefaultServer() {
        return USE_DEFAULT_SERVER;
    }

    @NotNull
    public String getUseGlobalFileFilters() {
        return USE_GLOBAL_FILE_FILTERS;
    }

    @NotNull
    public String getGlobalAstServerUrl() {
        return GLOBAL_AST_SERVER_URL;
    }

    @NotNull
    public String getGlobalClientId() {
        return GLOBAL_AST_CLIENT_ID;
    }

    @NotNull
    public String getNoDisplay() {
        return "style='display:none'";
    }

    @NotNull
    public String getServerUrl() {
        return SERVER_URL;
    }

    @NotNull
    public String getAuthenticationUrl() {
        return AUTHENTICATION_URL;
    }

    @NotNull
    public String getTenant() { return TENANT; }

    @NotNull
    public String getAstClientId() {
        return AST_CLIENT_ID;
    }

    @NotNull
    public String getAstSecret() {
        return AST_SECRET;
    }

    @NotNull
    public String getProjectName() {
        return PROJECT_NAME;
    }

    @NotNull
    public String getZipFileFilters() {
        return ZIP_FILE_FILTERS;
    }

    @NotNull
    public String getAdditionalParameters() {
        return ADDITIONAL_PARAMETERS;
    }


}