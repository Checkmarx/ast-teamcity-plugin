package com.checkmarx.teamcity.common;

public class CheckmarxScanRunnerConstants {

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String USE_DEFAULT_SERVER = "cxUseDefaultServer";

    public static final String RUNNER_TYPE = "checkmarxScan";
    public static final String RUNNER_DISPLAY_NAME = "Checkmarx AST Scan";
    public static final String RUNNER_DESCRIPTION = "Build Runner to scan the source code with Checkmarx AST engine.";

    public static final String PROJECT_NAME = "projectName";
    public static final String SERVER_URL = "serverUrl";
    public static final String AUTHENTICATION_URL = "authenticationUrl";
    public static final String ADDITIONAL_PARAMETERS = "additionalParameters";
    public static final String ZIP_FILE_FILTERS = "zipFileFilters";
    public static final String AST_CLIENT_ID = "astClientId";
    public static final String AST_SECRET = "secure:astSecret";
    public static final String VERSION = "version";

    //Global Config
    public static final String GLOBAL_AST_SERVER_URL = "globalAstServerUrl";
    public static final String GLOBAL_AST_AUTHENTICATION_URL = "globalAstAuthenticationUrl";
    public static final String GLOBAL_AST_CLIENT_ID = "globalAstClientId";
    public static final String GLOBAL_AST_SECRET = "globalAstSecret";
    public static final String GLOBAL_AST_TENANT_NAME = "globalAstTenantName";
    public static final String GLOBAL_ZIP_FILTERS = "globalZipFilters";

    public static final String[] GLOBAL_CONFIGS = {
            GLOBAL_AST_SERVER_URL, GLOBAL_AST_AUTHENTICATION_URL, GLOBAL_AST_CLIENT_ID, GLOBAL_AST_SECRET, GLOBAL_AST_TENANT_NAME,
            GLOBAL_ZIP_FILTERS
    };


    public static final String SAST_SCAN_ENABLED = "sastScanEnabled";
    public static final String SCA_SCAN_ENABLED = "scaScanEnabled";
    public static final String KICS_ENABLED = "kicsEnabled";
    public static final String CONTAINER_SCAN_ENABLED = "containerScanEnabled";

    public static final String DEFAULT_ZIP_FILE_FILTER_PATTERN =
            "!**/_cvs/**/*, !**/.svn/**/*,   !**/.hg/**/*,   !**/.git/**/*,  !**/.bzr/**/*, !**/bin/**/*,\n" +
                    "!**/obj/**/*,  !**/backup/**/*, !**/.idea/**/*, !**/*.DS_Store, !**/*.ipr,     !**/*.iws,\n" +
                    "!**/*.bak,     !**/*.tmp,       !**/*.aac,      !**/*.aif,      !**/*.iff,     !**/*.m3u,   !**/*.mid, !**/*.mp3,\n" +
                    "!**/*.mpa,     !**/*.ra,        !**/*.wav,      !**/*.wma,      !**/*.3g2,     !**/*.3gp,   !**/*.asf, !**/*.asx,\n" +
                    "!**/*.avi,     !**/*.flv,       !**/*.mov,      !**/*.mp4,      !**/*.mpg,     !**/*.rm,    !**/*.swf, !**/*.vob,\n" +
                    "!**/*.wmv,     !**/*.bmp,       !**/*.gif,      !**/*.jpg,      !**/*.png,     !**/*.psd,   !**/*.tif, !**/*.swf,\n" +
                    "!**/*.jar,     !**/*.zip,       !**/*.rar,      !**/*.exe,      !**/*.dll,     !**/*.pdb,   !**/*.7z,  !**/*.gz,\n" +
                    "!**/*.tar.gz,  !**/*.tar,       !**/*.gz,       !**/*.ahtm,     !**/*.ahtml,   !**/*.fhtml, !**/*.hdm,\n" +
                    "!**/*.hdml,    !**/*.hsql,      !**/*.ht,       !**/*.hta,      !**/*.htc,     !**/*.htd,   !**/*.war, !**/*.ear,\n" +
                    "!**/*.htmls,   !**/*.ihtml,     !**/*.mht,      !**/*.mhtm,     !**/*.mhtml,   !**/*.ssi,   !**/*.stm,\n" +
                    "!**/*.stml,    !**/*.ttml,      !**/*.txn,      !**/*.xhtm,     !**/*.xhtml,   !**/*.class, !**/node_modules/**/*, !**/*.iml\n";


    public String getProjectName() {
        return PROJECT_NAME;
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

    public String getAstClientId() { return AST_CLIENT_ID; }

    public String getAstSecret() { return AST_SECRET; }

    public String getSastScanEnabled() { return SAST_SCAN_ENABLED; }

    public String getScaScanEnabled() { return SCA_SCAN_ENABLED; }

    public String getKicsEnabled() { return KICS_ENABLED; }

    public String getContainerScanEnabled() { return CONTAINER_SCAN_ENABLED; }

    public String getZipFileFilters() { return ZIP_FILE_FILTERS; }

    public String getVersion() { return VERSION; }
}
