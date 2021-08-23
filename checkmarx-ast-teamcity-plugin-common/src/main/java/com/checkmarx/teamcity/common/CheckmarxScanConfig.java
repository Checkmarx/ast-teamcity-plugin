package com.checkmarx.teamcity.common;

import java.io.Serializable;

public class CheckmarxScanConfig implements Serializable {

    private String serverUrl;
    private String authenticationUrl;
    private String tenant;
    private String projectName;
    private String clientId;
    private String astSecret;
    private String additionalParameters;


    public CheckmarxScanConfig() {
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public void setServerUrl(final String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getAuthenticationUrl() { return this.authenticationUrl; }

    public void setAuthenticationUrl(final String authenticationUrl) {
        this.authenticationUrl = authenticationUrl;
    }

    public String getTenant() {
        return this.tenant;
    }

    public void setTenant(final String tenant) {
        this.tenant = tenant;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getAstSecret() {
        return this.astSecret;
    }

    public void setAstSecret(final String astSecret) {
        this.astSecret = astSecret;
    }

    public String getAdditionalParameters() {
        return this.additionalParameters;
    }

    public void setAdditionalParameters(final String additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

}
