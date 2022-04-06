package com.checkmarx.teamcity.agent.commands;

import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Collections.singletonList;

public class CheckmarxVersionCommand extends CheckmarxBuildServiceAdapter {

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {
        String checkmarxCliToolPath = getCheckmarxCliToolPath();

        return new SimpleProgramCommandLine(getBuildParameters().getEnvironmentVariables(),
                getWorkingDirectory().getAbsolutePath(),
                checkmarxCliToolPath,
                getArguments());
    }

    @Override
    public void beforeProcessStarted() {
        getBuild().getBuildLogger().message("Determining Checkmarx AST CLI version...");
    }

    @Override
    public boolean isCommandLineLoggingEnabled() {
        return false;
    }

    @Override
    List<String> getArguments() {
        return singletonList("version");
    }


}
