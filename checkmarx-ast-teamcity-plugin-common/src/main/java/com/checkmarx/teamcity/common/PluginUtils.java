package com.checkmarx.teamcity.common;

import jetbrains.buildServer.serverSide.crypt.EncryptUtil;
import org.apache.commons.lang3.StringUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.checkmarx.teamcity.common.CheckmarxParams.*;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class PluginUtils {

    public static String encrypt(String password) throws RuntimeException {
        String encPassword = "";
        if (!EncryptUtil.isScrambled(password)) {
            encPassword = EncryptUtil.scramble(password);
        } else {
            encPassword = password;
        }
        return encPassword;
    }

    public static String decrypt(String password) throws RuntimeException {
        String encStr = "";
        if (StringUtils.isNotEmpty(password)) {
            if (EncryptUtil.isScrambled(password)) {
                encStr = EncryptUtil.unscramble(password);
            } else {
                encStr = password;
            }
        }
        return encStr;
    }

    public static CheckmarxScanConfig resolveConfiguration(Map<String, String> runnerParameters, Map<String, String> sharedConfigParameters) {
        CheckmarxScanConfig scanConfig = new CheckmarxScanConfig();

        if (TRUE.equals(runnerParameters.get(USE_DEFAULT_SERVER))) {
            scanConfig.setServerUrl(validateNotEmpty(sharedConfigParameters.get(GLOBAL_AST_SERVER_URL), GLOBAL_AST_SERVER_URL));
            scanConfig.setClientId(validateNotEmpty(sharedConfigParameters.get(GLOBAL_AST_CLIENT_ID), GLOBAL_AST_CLIENT_ID));
            scanConfig.setAstSecret(decrypt(validateNotEmpty(sharedConfigParameters.get(GLOBAL_AST_SECRET), GLOBAL_AST_SECRET)));
            scanConfig.setAuthenticationUrl((sharedConfigParameters.get(GLOBAL_AST_AUTHENTICATION_URL)));
            scanConfig.setTenant((sharedConfigParameters.get(GLOBAL_AST_TENANT)));
        } else {
            scanConfig.setServerUrl(validateNotEmpty(runnerParameters.get(SERVER_URL), SERVER_URL));
            scanConfig.setClientId(validateNotEmpty(runnerParameters.get(AST_CLIENT_ID), AST_CLIENT_ID));
            scanConfig.setAstSecret(decrypt(validateNotEmpty(runnerParameters.get(AST_SECRET), AST_SECRET)));
            scanConfig.setAuthenticationUrl((runnerParameters.get(AUTHENTICATION_URL)));
            scanConfig.setTenant((runnerParameters.get(TENANT)));
        }

        if (TRUE.equals(runnerParameters.get(USE_GLOBAL_ADDITIONAL_PARAMETERS))) {
            scanConfig.setAdditionalParameters((sharedConfigParameters.get(GLOBAL_ADDITIONAL_PARAMETERS)));
        } else {
            scanConfig.setAdditionalParameters(runnerParameters.get(ADDITIONAL_PARAMETERS));
        }

        scanConfig.setProjectName(validateNotEmpty(runnerParameters.get(PROJECT_NAME), PROJECT_NAME));
        scanConfig.setBranchName(validateNotEmpty(runnerParameters.get(BRANCH_NAME), BRANCH_NAME));

        return scanConfig;

    }

    public static List<String> getAuthenticationFlags(CheckmarxScanConfig scanConfig) {
        List<String> arguments = new ArrayList<>();

        arguments.add("--base-uri");
        arguments.add(scanConfig.getServerUrl());

        if (nullIfEmpty(scanConfig.getAuthenticationUrl()) != null) {
            arguments.add("--base-auth-uri");
            arguments.add(scanConfig.getAuthenticationUrl());
        }

        if (nullIfEmpty(scanConfig.getTenant()) != null) {
            arguments.add("--tenant");
            arguments.add(scanConfig.getTenant());
        }

        arguments.add("--client-id");
        arguments.add(scanConfig.getClientId());

        return arguments;
    }

    private static String validateNotEmpty(String param, String paramName) throws InvalidParameterException {
        if (param == null || param.length() == 0) {
            throw new InvalidParameterException("Parameter [" + paramName + "] must not be empty");
        }
        return param;
    }


}
