package com.checkmarx.teamcity.common.runner;


import java.util.Set;


public abstract class RunnerVersion {
    private final String version;
    private final Set<Platform> platforms;

    RunnerVersion(String version, Set<Platform> platforms) {
        this.version = (version);
        this.platforms = (platforms);
    }

    /**
     * Returns the path to <code>Checkmarx AST</code> CLI binary file
     */
    public abstract String getCheckmarxCliToolPath(Platform platform);


}
