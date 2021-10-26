package com.checkmarx.teamcity.common;

public abstract class CheckmarxParams {

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String USE_DEFAULT_SERVER = "cxUseDefaultServer";
    public static final String USE_GLOBAL_ADDITIONAL_PARAMETERS = "useGlobalAdditionalParameters";


    public static final String SERVER_URL = "serverUrl";
    public static final String AUTHENTICATION_URL = "authenticationUrl";
    public static final String AST_CLIENT_ID = "astClientId";
    public static final String AST_SECRET = "secure:astSecret";
    public static final String TENANT = "tenant";
    public static final String ADDITIONAL_PARAMETERS = "additionalParameters";
    public static final String PROJECT_NAME = "projectName";
    public static final String BRANCH_NAME = "branchName";


    //Global Config
    public static final String GLOBAL_AST_SERVER_URL = "globalAstServerUrl";
    public static final String GLOBAL_AST_AUTHENTICATION_URL = "globalAstAuthenticationUrl";
    public static final String GLOBAL_AST_CLIENT_ID = "globalAstClientId";
    public static final String GLOBAL_AST_SECRET = "globalAstSecret";
    public static final String GLOBAL_AST_TENANT = "globalAstTenant";
    public static final String GLOBAL_ADDITIONAL_PARAMETERS = "globalAdditionalParameters";

    public static final String[] GLOBAL_CONFIGS = {
            GLOBAL_AST_SERVER_URL, GLOBAL_AST_AUTHENTICATION_URL, GLOBAL_AST_CLIENT_ID, GLOBAL_AST_SECRET, GLOBAL_AST_TENANT,
            GLOBAL_ADDITIONAL_PARAMETERS
    };

}
