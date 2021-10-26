package com.checkmarx.teamcity.common;

public class CheckmarxScanRunnerConstants {

    public static final String TRUE = "true";

    public final static String REPORT_NAME = "Checkmarx_ast_report";
    public final static String REPORT_HTML_NAME = "Checkmarx_ast_report.html";
    public final static String SCAN_OUTPUT_LOG_TEXT = "checkmarxASTScan.txt";

    public static final String RUNNER_TYPE = "checkmarxScan";
    public static final String RUNNER_DISPLAY_NAME = "Checkmarx AST Scan";
    public static final String RUNNER_DESCRIPTION = "Build Runner to scan the source code with Checkmarx AST engine.";

    public static final String PROJECT_NAME = "projectName";
    public static final String BRANCH_NAME = "branchName";
    public static final String SERVER_URL = "serverUrl";
    public static final String AUTHENTICATION_URL = "authenticationUrl";
    public static final String ADDITIONAL_PARAMETERS = "additionalParameters";
    public static final String AST_CLIENT_ID = "astClientId";
    public static final String AST_SECRET = "secure:astSecret";


    public String getProjectName() {
        return PROJECT_NAME;
    }

    public String getBranchName() {
        return BRANCH_NAME;
    }

    public String getServerUrl() {
        return SERVER_URL;
    }

    public String getAuthenticationUrl() {
        return AUTHENTICATION_URL;
    }

    public String getAdditionalParameters() {
        return ADDITIONAL_PARAMETERS;
    }

    public String getAstClientId() {
        return AST_CLIENT_ID;
    }

    public String getAstSecret() {
        return AST_SECRET;
    }

}
