package com.checkmarx.teamcity.common.runner;

public enum Platform {
    LINUX(""),
    MAC_OS("-mac"),
    WINDOWS(".exe");

    private final String suffix;

    Platform(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return suffix;
    }
}
