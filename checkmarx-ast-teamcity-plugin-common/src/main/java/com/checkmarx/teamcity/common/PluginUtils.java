package com.checkmarx.teamcity.common;

import jetbrains.buildServer.serverSide.crypt.EncryptUtil;
import org.apache.commons.lang3.StringUtils;

public class PluginUtils {

    public static String encrypt(String password) throws RuntimeException{
        String encPassword = "";
        if(!EncryptUtil.isScrambled(password)) {
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
                encStr =  password;
            }
        }
        return encStr;
    }

}
