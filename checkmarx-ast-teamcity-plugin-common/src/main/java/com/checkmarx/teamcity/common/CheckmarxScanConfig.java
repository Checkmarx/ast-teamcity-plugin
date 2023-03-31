package com.checkmarx.teamcity.common;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckmarxScanConfig implements Serializable {

    private static final Pattern pattern = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    private String serverUrl;
    private String authenticationUrl;
    private String tenant;
    private String projectName;
    private String branchName;
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

    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
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

    public void setAdditionalParameters(final String additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    public List<String> getAdditionalParameters() {
        List<String> additionalParametersList = new ArrayList<>();
        if (StringUtils.isNotBlank(this.additionalParameters)) {
            Matcher m = pattern.matcher(additionalParameters);
            while (m.find()) {
                additionalParametersList.add(m.group(1).replaceAll("\"", ""));
            }
        }
        return additionalParametersList;
    }
}
