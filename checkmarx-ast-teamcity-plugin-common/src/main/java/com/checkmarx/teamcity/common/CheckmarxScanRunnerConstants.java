package com.checkmarx.teamcity.common;

public class CheckmarxScanRunnerConstants {

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public final static String REPORT_HTML_NAME = "checkmarx_ast_report.html";

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

    public String getZipFileFilters() { return ZIP_FILE_FILTERS; }

}
