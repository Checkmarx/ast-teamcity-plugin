package com.checkmarx.teamcity.common.runner;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import static java.util.Arrays.asList;
import static com.checkmarx.teamcity.common.runner.Platform.*;

public final class Runners {

    private static final TreeMap<String, RunnerVersion> AVAILABLE_RUNNERS = new TreeMap<>();
    private static final String DEFAULT_VERSION = "2.0.0";

    // all bundled versions should be initialized here
    static {
        AVAILABLE_RUNNERS.put(DEFAULT_VERSION, new RunnerVersion(DEFAULT_VERSION, new HashSet<>(asList(LINUX, MAC_OS, WINDOWS))) {
            @Override
            public String getCheckmarxCliToolPath(Platform platform) {
                if (platform == null) {
                    return "cx";
                }
                return "cx" + platform.getSuffix();
            }

        });
    }

    public static RunnerVersion getRunner(String version) {
        return AVAILABLE_RUNNERS.get(version);
    }

    public static RunnerVersion getDefaultRunner() {
        return AVAILABLE_RUNNERS.get(DEFAULT_VERSION);
    }

    public static String getDefaultRunnerVersion() {
        return DEFAULT_VERSION;
    }

    public Set<String> getVersions() {
        return AVAILABLE_RUNNERS.descendingKeySet();
    }

}
