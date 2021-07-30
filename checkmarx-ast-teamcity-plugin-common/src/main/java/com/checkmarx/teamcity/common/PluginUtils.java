package com.checkmarx.teamcity.common;

import jetbrains.buildServer.serverSide.crypt.EncryptUtil;
import org.apache.commons.lang3.StringUtils;

import java.security.InvalidParameterException;
import java.util.Map;

import static com.checkmarx.teamcity.common.CheckmarxParams.*;

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

    public static CheckmarxScanConfig resolveConfiguration(Map<String, String> runnerParameters, Map<String, String> sharedConfigParameters){
       CheckmarxScanConfig scanConfig = new CheckmarxScanConfig();

        if (TRUE.equals(runnerParameters.get(USE_DEFAULT_SERVER))) {
            scanConfig.setServerUrl(validateNotEmpty(sharedConfigParameters.get(GLOBAL_AST_SERVER_URL), GLOBAL_AST_SERVER_URL));
            scanConfig.setClientId(validateNotEmpty(sharedConfigParameters.get(GLOBAL_AST_CLIENT_ID), GLOBAL_AST_CLIENT_ID));
            scanConfig.setAstSecret(decrypt(validateNotEmpty(sharedConfigParameters.get(GLOBAL_AST_SECRET),GLOBAL_AST_SECRET)));
            scanConfig.setAuthenticationUrl((sharedConfigParameters.get(GLOBAL_AST_AUTHENTICATION_URL)));
            scanConfig.setTenant((sharedConfigParameters.get(GLOBAL_AST_TENANT)));
        }
        else {
            scanConfig.setServerUrl(validateNotEmpty(runnerParameters.get(SERVER_URL), SERVER_URL));
            scanConfig.setClientId(validateNotEmpty(runnerParameters.get(AST_CLIENT_ID), AST_CLIENT_ID));
            scanConfig.setAstSecret(decrypt(validateNotEmpty(runnerParameters.get(AST_SECRET), AST_SECRET)));
            scanConfig.setAuthenticationUrl((runnerParameters.get(AUTHENTICATION_URL)));
            scanConfig.setTenant((runnerParameters.get(TENANT)));
        }

        if (TRUE.equals(runnerParameters.get(USE_GLOBAL_ADDITIONAL_PARAMETERS))) {
            scanConfig.setAdditionalParameters((sharedConfigParameters.get(GLOBAL_ADDITIONAL_PARAMETERS)));
        }
        else
        {
            scanConfig.setAdditionalParameters(runnerParameters.get(ADDITIONAL_PARAMETERS));
        }

        scanConfig.setProjectName(validateNotEmpty(runnerParameters.get(PROJECT_NAME), PROJECT_NAME));

       return scanConfig;

    }

    private static String validateNotEmpty(String param, String paramName) throws InvalidParameterException {
        if (param == null || param.length() == 0) {
            throw new InvalidParameterException("Parameter [" + paramName + "] must not be empty");
        }
        return param;
    }

    public static String getHtmlText() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\">\n" +
                "    <meta http-equiv=\"Content-Language\" content=\"en-us\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <title>Checkmarx test report</title>\n" +
                "    <style type=\"text/css\">\n" +
                "        * {\n" +
                "            box-sizing: border-box;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .bg-green {\n" +
                "            background-color: #f9ae4d;\n" +
                "        }\n" +
                "\n" +
                "        .bg-grey {\n" +
                "            background-color: #bdbdbd;\n" +
                "        }\n" +
                "\n" +
                "        .bg-kicks {\n" +
                "            background-color: #008e96 !important;\n" +
                "        }\n" +
                "\n" +
                "        .bg-red {\n" +
                "            background-color: #f1605d;\n" +
                "        }\n" +
                "\n" +
                "        .bg-sast {\n" +
                "            background-color: #1165b4 !important;\n" +
                "        }\n" +
                "\n" +
                "        .bg-sca {\n" +
                "            background-color: #0fcdc2 !important;\n" +
                "        }\n" +
                "\n" +
                "        .header-row .cx-info .data .calendar-svg {\n" +
                "            margin-right: 8px;\n" +
                "        }\n" +
                "\n" +
                "        .header-row .cx-info .data .scan-svg svg {\n" +
                "            -webkit-transform: scale(0.43);\n" +
                "            margin-top: -9px;\n" +
                "            transform: scale(0.43);\n" +
                "        }\n" +
                "\n" +
                "        .header-row .cx-info .data .scan-svg {\n" +
                "            margin-left: -10px;\n" +
                "        }\n" +
                "\n" +
                "        .header-row .cx-info .data svg path {\n" +
                "            fill: #565360;\n" +
                "        }\n" +
                "\n" +
                "        .header-row .cx-info .data {\n" +
                "            color: #565360;\n" +
                "            display: flex;\n" +
                "            margin-right: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .header-row .cx-info {\n" +
                "            display: flex;\n" +
                "            font-size: 13px;\n" +
                "        }\n" +
                "\n" +
                "        .header-row {\n" +
                "            -ms-flex-pack: justify;\n" +
                "            -webkit-box-pack: justify;\n" +
                "            display: flex;\n" +
                "            height: 30px;\n" +
                "            justify-content: space-between;\n" +
                "            margin-bottom: 5px;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "        }\n" +
                "\n" +
                "        .cx-cx-main {\n" +
                "            align-items: center;\n" +
                "            display: flex;\n" +
                "            flex-flow: row wrap;\n" +
                "            justify-content: space-around;\n" +
                "            left: 0;\n" +
                "            position: relative;\n" +
                "            top: 0;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "\n" +
                "        .progress {\n" +
                "            background-color: #e9ecef;\n" +
                "            display: flex;\n" +
                "            height: 1em;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .progress-bar {\n" +
                "            -ms-flex-direction: column;\n" +
                "            -ms-flex-pack: center;\n" +
                "            -webkit-box-direction: normal;\n" +
                "            -webkit-box-orient: vertical;\n" +
                "            -webkit-box-pack: center;\n" +
                "            background-color: grey;\n" +
                "            color: #FFF;\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "            font-size: 11px;\n" +
                "            justify-content: center;\n" +
                "            text-align: center;\n" +
                "            white-space: nowrap;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        .top-row .element {\n" +
                "            margin: 0 3rem 6rem;\n" +
                "        }\n" +
                "\n" +
                "        .top-row .risk-level-tile .value {\n" +
                "            display: inline-block;\n" +
                "            font-size: 32px;\n" +
                "            font-weight: 700;\n" +
                "            margin-top: 20px;\n" +
                "            text-align: center;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "\n" +
                "        .top-row .risk-level-tile {\n" +
                "            -webkit-box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);\n" +
                "            background: #fff;\n" +
                "            border: 1px solid #dad8dc;\n" +
                "            border-radius: 4px;\n" +
                "            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);\n" +
                "            color: #565360;\n" +
                "            min-height: 120px;\n" +
                "            width: 24.5%;\n" +
                "        }\n" +
                "\n" +
                "        .top-row .risk-level-tile.high {\n" +
                "            background: #f1605d;\n" +
                "            color: #fcfdff;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        .chart .total {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: 700;\n" +
                "        }\n" +
                "\n" +
                "        .chart .bar-chart {\n" +
                "            margin-left: 10px;\n" +
                "            padding-top: 7px;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "\n" +
                "        .legend {\n" +
                "            color: #95939b;\n" +
                "            float: left;\n" +
                "            padding-right: 10px;\n" +
                "            text-transform: capitalize;\n" +
                "        }\n" +
                "\n" +
                "\n" +
                "        .chart .total {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: 700;\n" +
                "        }\n" +
                "\n" +
                "        .chart .bar-chart {\n" +
                "            margin-left: 10px;\n" +
                "            padding-top: 7px;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "\n" +
                "        .top-row .vps-tile .legend {\n" +
                "            color: #95939b;\n" +
                "            float: left;\n" +
                "            padding-right: 10px;\n" +
                "            text-transform: capitalize;\n" +
                "        }\n" +
                "\n" +
                "        .chart .engines-bar-chart {\n" +
                "            margin-bottom: 6px;\n" +
                "            margin-top: 7px;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "\n" +
                "        .legend {\n" +
                "            color: #95939b;\n" +
                "            text-transform: capitalize;\n" +
                "            float: left;\n" +
                "            padding-right: 10px;\n" +
                "        }\n" +
                "\n" +
                "        .top-row {\n" +
                "            -ms-flex-pack: justify;\n" +
                "            -webkit-box-pack: justify;\n" +
                "            align-items: center;\n" +
                "            display: flex;\n" +
                "            justify-content: space-evenly;\n" +
                "            padding: 20px;\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "\n" +
                "        .bar-chart .progress .progress-bar.bg-danger {\n" +
                "            background-color: #f1605d !important;\n" +
                "        }\n" +
                "\n" +
                "        .bar-chart .progress .progress-bar.bg-success {\n" +
                "            background-color: #bdbdbd !important;\n" +
                "        }\n" +
                "\n" +
                "        .bar-chart .progress .progress-bar.bg-warning {\n" +
                "            background-color: #f9ae4d !important;\n" +
                "        }\n" +
                "\n" +
                "        .width-100 {\n" +
                "            width: 100%;\n" +
                "        }\n" +
                "\n" +
                "        .bar-chart .progress .progress-bar {\n" +
                "            color: #FFF;\n" +
                "            font-size: 11px;\n" +
                "            font-weight: 500;\n" +
                "            min-width: fit-content;\n" +
                "            padding: 0 3px;\n" +
                "        }\n" +
                "\n" +
                "        .bar-chart .progress .progress-bar:not(:last-child),\n" +
                "        .progress-bar:not(:last-child),\n" +
                "        .bar-chart .progress .progress-bar:not(:last-child) {\n" +
                "            border-right: 1px solid #FFF;\n" +
                "        }\n" +
                "\n" +
                "        .bar-chart .progress {\n" +
                "            background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAAECAYAAACp8Z5+AAAAIklEQVQYV2NkQAIfPnz6zwjjgzgCAnyMYAEYB8RmROaABAAU7g/W6mdTYAAAAABJRU5ErkJggg==) repeat;\n" +
                "            border: 1px solid #f0f0f0;\n" +
                "            border-radius: 3px;\n" +
                "            height: 1.5rem;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .engines-legend-dot,\n" +
                "        .severity-legend-dot {\n" +
                "            font-size: 14px;\n" +
                "            padding-left: 5px;\n" +
                "        }\n" +
                "\n" +
                "        .severity-engines-text,\n" +
                "        .severity-legend-text {\n" +
                "            float: left;\n" +
                "            height: 10px;\n" +
                "            margin-top: 5px;\n" +
                "            width: 10px;\n" +
                "        }\n" +
                "\n" +
                "        .chart {\n" +
                "            display: flex;\n" +
                "        }\n" +
                "\n" +
                "        .element .total {\n" +
                "            font-weight: 700;\n" +
                "        }\n" +
                "\n" +
                "        .top-row .element {\n" +
                "            -ms-flex-direction: column;\n" +
                "            -ms-flex-pack: justify;\n" +
                "            -webkit-box-direction: normal;\n" +
                "            -webkit-box-orient: vertical;\n" +
                "            -webkit-box-pack: justify;\n" +
                "            -webkit-box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);\n" +
                "            background: #fff;\n" +
                "            border-radius: 4px;\n" +
                "            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);\n" +
                "            color: #565360;\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "            justify-content: space-between;\n" +
                "            min-height: 120px;\n" +
                "            padding: 16px 20px;\n" +
                "            width: 24.5%;\n" +
                "        }\n" +
                "        .cx-demo { \n" +
                "            color: red;\n" +
                "            align-items: center;\n" +
                "            text-align: center;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "    </style>\n" +
                "    <script>\n" +
                "        window.addEventListener('load', function () {\n" +
                "            var totalVal = document.getElementById(\"total\").textContent;\n" +
                "            var elements = document.getElementsByClassName(\"value\");\n" +
                "            for (var i = 0; i < elements.length; i++) {\n" +
                "                var element = elements[i];\n" +
                "                var perc = ((element.textContent / totalVal) * 100);\n" +
                "                element.style.width = perc + \"%\";\n" +
                "            }\n" +
                "        }, false);\n" +
                "    </script>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <p class=\"cx-demo\">Mock Values</p>\n" +
                "    <div class=\"cx-main\">\n" +
                "        <div class=\"header-row\">\n" +
                "            <div class=\"cx-info\">\n" +
                "                <div class=\"data\">\n" +
                "                    <div class=\"scan-svg\"><svg width=\"40\" height=\"40\" fill=\"none\">\n" +
                "                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\"\n" +
                "                                d=\"M9.393 32.273c-.65.651-1.713.656-2.296-.057A16.666 16.666 0 1136.583 20h1.75v3.333H22.887a3.333 3.333 0 110-3.333h3.911a7 7 0 10-12.687 5.45c.447.698.464 1.641-.122 2.227-.586.586-1.546.591-2.038-.075A10 10 0 1129.86 20h3.368a13.331 13.331 0 00-18.33-10.652A13.334 13.334 0 009.47 29.846c.564.727.574 1.776-.077 2.427z\"\n" +
                "                                fill=\"url(#scans_svg__paint0_angular)\"></path>\n" +
                "                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\"\n" +
                "                                d=\"M9.393 32.273c-.65.651-1.713.656-2.296-.057A16.666 16.666 0 1136.583 20h1.75v3.333H22.887a3.333 3.333 0 110-3.333h3.911a7 7 0 10-12.687 5.45c.447.698.464 1.641-.122 2.227-.586.586-1.546.591-2.038-.075A10 10 0 1129.86 20h3.368a13.331 13.331 0 00-18.33-10.652A13.334 13.334 0 009.47 29.846c.564.727.574 1.776-.077 2.427z\"\n" +
                "                                fill=\"url(#scans_svg__paint1_angular)\"></path>\n" +
                "                            <defs>\n" +
                "                                <radialGradient id=\"scans_svg__paint0_angular\" cx=\"0\" cy=\"0\" r=\"1\"\n" +
                "                                    gradientUnits=\"userSpaceOnUse\"\n" +
                "                                    gradientTransform=\"matrix(1 16.50003 -16.50003 1 20 21.5)\">\n" +
                "                                    <stop offset=\"0.807\" stop-color=\"#2991F3\"></stop>\n" +
                "                                    <stop offset=\"1\" stop-color=\"#2991F3\" stop-opacity=\"0\"></stop>\n" +
                "                                </radialGradient>\n" +
                "                                <radialGradient id=\"scans_svg__paint1_angular\" cx=\"0\" cy=\"0\" r=\"1\"\n" +
                "                                    gradientUnits=\"userSpaceOnUse\"\n" +
                "                                    gradientTransform=\"matrix(1 16.50003 -16.50003 1 20 21.5)\">\n" +
                "                                    <stop offset=\"0.807\" stop-color=\"#2991F3\"></stop>\n" +
                "                                    <stop offset=\"1\" stop-color=\"#2991F3\" stop-opacity=\"0\"></stop>\n" +
                "                                </radialGradient>\n" +
                "                            </defs>\n" +
                "                        </svg></div>\n" +
                "                    <div>Scan: d2fed170-d48f-4a81-a4ed-571936b52037</div>\n" +
                "                </div>\n" +
                "                <div class=\"data\">\n" +
                "                    <div class=\"calendar-svg\"><svg width=\"12\" height=\"12\" fill=\"none\">\n" +
                "                            <path fill-rule=\"evenodd\" clip-rule=\"evenodd\"\n" +
                "                                d=\"M3.333 0h1.334v1.333h2.666V0h1.334v1.333h2c.368 0 .666.299.666.667v8.667a.667.667 0 01-.666.666H1.333a.667.667 0 01-.666-.666V2c0-.368.298-.667.666-.667h2V0zm4 2.667V4h1.334V2.667H10V10H2V2.667h1.333V4h1.334V2.667h2.666z\"\n" +
                "                                fill=\"#95939B\"></path>\n" +
                "                        </svg></div>\n" +
                "                    <div>25/05/212021, 07:05:42</div>\n" +
                "                </div>\n" +
                "                \n" +
                "                <div class=\"data\">\n" +
                "                    <a href=\"\" target=\"_blank\">More details</a>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "\n" +
                "        </div>\n" +
                "        <div class=\"top-row\">\n" +
                "            <div class=\"element risk-level-tile high\"><span class=\"value\">High Risk</span></div>\n" +
                "            <div class=\"element\">\n" +
                "                <div class=\"total\">Total Vulnerabilites</div>\n" +
                "                <div>\n" +
                "                    <div class=\"legend\"><span class=\"severity-legend-dot\">high</span>\n" +
                "                        <div class=\"severity-legend-text bg-red\"></div>\n" +
                "                    </div>\n" +
                "                    <div class=\"legend\"><span class=\"severity-legend-dot\">medium</span>\n" +
                "                        <div class=\"severity-legend-text bg-green\"></div>\n" +
                "                    </div>\n" +
                "                    <div class=\"legend\"><span class=\"severity-legend-dot\">low</span>\n" +
                "                        <div class=\"severity-legend-text bg-grey\"></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"chart\">\n" +
                "                    <div id=\"total\" class=\"total\">3171</div>\n" +
                "                    <div class=\"single-stacked-bar-chart bar-chart\">\n" +
                "                        <div class=\"progress\">\n" +
                "                            <div class=\"progress-bar bg-danger value\">94</div>\n" +
                "                            <div class=\"progress-bar bg-warning value\">227</div>\n" +
                "                            <div class=\"progress-bar bg-success value\">2977</div>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div class=\"element\">\n" +
                "                <div class=\"total\">Vulnerabilities per Scan Type</div>\n" +
                "                <div class=\"legend\">\n" +
                "                    <div class=\"legend\"><span class=\"engines-legend-dot\">SAST</span>\n" +
                "                        <div class=\"severity-engines-text bg-sast\"></div>\n" +
                "                    </div>\n" +
                "                    <div class=\"legend\"><span class=\"engines-legend-dot\">KICS</span>\n" +
                "                        <div class=\"severity-engines-text bg-kicks\"></div>\n" +
                "                    </div>\n" +
                "                    <div class=\"legend\"><span class=\"engines-legend-dot\">SCA</span>\n" +
                "                        <div class=\"severity-engines-text bg-sca\"></div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "                <div class=\"chart\">\n" +
                "                    <div class=\"single-stacked-bar-chart bar-chart\">\n" +
                "                        <div class=\"progress\">\n" +
                "                            <div class=\"progress-bar bg-sast value\">3010</div>\n" +
                "                            <div class=\"progress-bar bg-kicks value\">161</div>\n" +
                "                        </div>\n" +
                "                    </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "\n" +
                "    </div>\n" +
                "</body>";
    }

}
