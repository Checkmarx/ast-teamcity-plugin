package com.checkmarx.teamcity.server.compatibility;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Compatibility adapter to bridge between javax.servlet and jakarta.servlet APIs
 * for TeamCity 2024.12 and Spring 6.x compatibility.
 */
public class ServletCompatibilityAdapter {

    /**
     * Adapts a Jakarta servlet request to work with TeamCity's javax servlet expectations
     */
    public static Object adaptJakartaToJavax(jakarta.servlet.http.HttpServletRequest jakartaRequest) {
        return new JavaxRequestWrapper(jakartaRequest);
    }

    /**
     * Adapts a Jakarta servlet response to work with TeamCity's javax servlet expectations
     */
    public static Object adaptJakartaToJavax(jakarta.servlet.http.HttpServletResponse jakartaResponse) {
        return new JavaxResponseWrapper(jakartaResponse);
    }

    /**
     * Wrapper class to adapt Jakarta HttpServletRequest to javax.servlet expectations
     */
    private static class JavaxRequestWrapper {
        private final jakarta.servlet.http.HttpServletRequest jakartaRequest;

        public JavaxRequestWrapper(jakarta.servlet.http.HttpServletRequest jakartaRequest) {
            this.jakartaRequest = jakartaRequest;
        }

        // Delegate all method calls to the Jakarta request
        public String getParameter(String name) {
            return jakartaRequest.getParameter(name);
        }

        public String[] getParameterValues(String name) {
            return jakartaRequest.getParameterValues(name);
        }

        public Map<String, String[]> getParameterMap() {
            return jakartaRequest.getParameterMap();
        }

        public String getHeader(String name) {
            return jakartaRequest.getHeader(name);
        }

        public String getMethod() {
            return jakartaRequest.getMethod();
        }

        public String getRequestURI() {
            return jakartaRequest.getRequestURI();
        }

        public String getContextPath() {
            return jakartaRequest.getContextPath();
        }

        public Object getAttribute(String name) {
            return jakartaRequest.getAttribute(name);
        }

        public void setAttribute(String name, Object value) {
            jakartaRequest.setAttribute(name, value);
        }

        // Add other methods as needed
    }

    /**
     * Wrapper class to adapt Jakarta HttpServletResponse to javax.servlet expectations
     */
    private static class JavaxResponseWrapper {
        private final jakarta.servlet.http.HttpServletResponse jakartaResponse;

        public JavaxResponseWrapper(jakarta.servlet.http.HttpServletResponse jakartaResponse) {
            this.jakartaResponse = jakartaResponse;
        }

        public void setContentType(String type) {
            jakartaResponse.setContentType(type);
        }

        public void setStatus(int status) {
            jakartaResponse.setStatus(status);
        }

        public void setHeader(String name, String value) {
            jakartaResponse.setHeader(name, value);
        }

        // Add other methods as needed
    }
}
