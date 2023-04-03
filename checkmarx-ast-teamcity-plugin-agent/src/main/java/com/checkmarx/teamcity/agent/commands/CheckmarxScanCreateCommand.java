package com.checkmarx.teamcity.agent.commands;


import com.checkmarx.teamcity.common.CheckmarxScanConfig;
import com.checkmarx.teamcity.common.PluginUtils;
import jetbrains.buildServer.RunBuildException;
import jetbrains.buildServer.agent.runner.ProgramCommandLine;
import jetbrains.buildServer.agent.runner.SimpleProgramCommandLine;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static jetbrains.buildServer.util.StringUtil.nullIfEmpty;

public class CheckmarxScanCreateCommand extends CheckmarxBuildServiceAdapter {

    private static final Logger LOG = Logger.getLogger(CheckmarxScanCreateCommand.class);
    private CheckmarxScanConfig scanConfig;

    @NotNull
    @Override
    public ProgramCommandLine makeProgramCommandLine() throws RunBuildException {

        scanConfig = initExecutionCall();
        LOG.info("----------------------- Checkmarx: Initiating the Scan Command ------------------------");
        String checkmarxCliToolPath = getCheckmarxCliToolPath();

        Map<String, String> envVars = new HashMap<>(getEnvironmentVariables());
        envVars.put("CX_CLIENT_SECRET", scanConfig.getAstSecret());

        return new SimpleProgramCommandLine(envVars,
                                            getWorkingDirectory().getAbsolutePath(),
                                            checkmarxCliToolPath,
                                            getArguments());
    }

    @Override
    public void beforeProcessStarted() {
        getBuild().getBuildLogger().message("Scanning with Checkmarx AST CLI ... ");
    }

    @Override
    public void afterProcessFinished() {
        getBuild().getBuildLogger().message("Scanning completed with Checkmarx AST CLI.");
    }

    @Override
    List<String> getArguments() {
        List<String> arguments = new ArrayList<>();

        arguments.add("scan");
        arguments.add("create");

        arguments.addAll(PluginUtils.getAuthenticationFlags(scanConfig));

        arguments.add("--agent");
        arguments.add("TeamCity");

        arguments.add("--project-name");
        arguments.add(scanConfig.getProjectName());

        arguments.add("--branch");
        arguments.add(scanConfig.getBranchName());

        arguments.add("-s");
        arguments.add(".");

        arguments.addAll(scanConfig.getAdditionalParameters());

        return arguments;
    }
}
