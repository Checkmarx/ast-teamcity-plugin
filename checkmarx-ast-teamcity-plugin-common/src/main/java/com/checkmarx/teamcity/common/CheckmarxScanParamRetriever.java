package com.checkmarx.teamcity.common;

import jetbrains.buildServer.TeamCityRuntimeException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

public class CheckmarxScanParamRetriever {

    private static final Logger LOG = Logger.getLogger(CheckmarxScanParamRetriever.class);
    private static final int MAX_SCAN_ID_LENGTH = 36;
    
    public static String scanIDRetriever(String filePath, String scanIDSearchParam){
        String scanID = null;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8));
            String logLine = bufferedReader.readLine();
            while(logLine != null) {
                LOG.warn("Log Line: " + logLine);
                int searchIndex = logLine.indexOf(scanIDSearchParam);
                if (searchIndex != -1) {
                    String uuidSubstring = logLine.substring(searchIndex, (searchIndex + 50));
                    LOG.warn("UUID STRING: " + uuidSubstring);
                    int colonIndex = uuidSubstring.indexOf(':');
                    scanID = (uuidSubstring.substring(colonIndex + 1)).trim();
                    //trim the scanID to 36 characters to avoid trailing characters
                    scanID = scanID.length() > MAX_SCAN_ID_LENGTH ? scanID.substring(0,MAX_SCAN_ID_LENGTH) : scanID;
                    LOG.warn("Scan ID retrieved: " + scanID);
                    break;
                }
                logLine = bufferedReader.readLine();
            }
        } catch (IOException e) {
            throw new TeamCityRuntimeException(format("Cannot find the file '%s'", filePath));
        } finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return scanID;
    }
}
