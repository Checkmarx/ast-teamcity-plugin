package com.checkmarx.teamcity.server;

import com.checkmarx.teamcity.common.CheckmarxParams;
import com.checkmarx.teamcity.common.PluginUtils;
import jetbrains.buildServer.controllers.ActionErrors;
import jetbrains.buildServer.controllers.BaseFormXmlController;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.serverSide.crypt.RSACipher;
import jetbrains.buildServer.util.StringUtil;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class CheckmarxAdminPageController extends BaseFormXmlController {

    public static final String INVALID = "invalid_";
    private final static Logger LOG = Logger.getLogger(CheckmarxAdminPageController.class);
    private final CheckmarxAdminConfigBase checkmarxAdminConfig;

    public CheckmarxAdminPageController(@NotNull final CheckmarxAdminConfigBase checkmarxAdminConfig) {
        this.checkmarxAdminConfig = checkmarxAdminConfig;
    }


    // Spring 6.x compatibility - implement required handleRequestInternal method
    @Override
    protected ModelAndView handleRequestInternal(jakarta.servlet.http.HttpServletRequest jakartaRequest,
                                                 jakarta.servlet.http.HttpServletResponse jakartaResponse) throws Exception {
        // Convert Jakarta servlet objects to javax.servlet objects for TeamCity compatibility
        HttpServletRequest javaxRequest = new JavaxRequestAdapter(jakartaRequest);
        HttpServletResponse javaxResponse = new JavaxResponseAdapter(jakartaResponse);

        if ("GET".equalsIgnoreCase(jakartaRequest.getMethod())) {
            return doGet(javaxRequest, javaxResponse);
        } else if ("POST".equalsIgnoreCase(jakartaRequest.getMethod())) {
            // For POST requests, we need to handle XML response differently
            return null; // Let the base class handle POST through doPost
        }
        return null;
    }

    @Override
    protected ModelAndView doGet(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) {
        return null;
    }

    @Override
    protected void doPost(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull Element xmlResponse) {

        final ActionErrors actionErrors = validateForm(httpServletRequest);
        if (actionErrors.hasErrors()) {
            actionErrors.serialize(xmlResponse);
            return;
        }

        for (String config : CheckmarxParams.GLOBAL_CONFIGS) {
            checkmarxAdminConfig.setConfiguration(config, StringUtil.emptyIfNull(httpServletRequest.getParameter(config)));
        }

        String encryptedSecret = ensurePasswordEncryption(httpServletRequest, "encryptedGlobalAstSecret");
        checkmarxAdminConfig.setConfiguration(CheckmarxParams.GLOBAL_AST_SECRET, encryptedSecret);

        try {
            checkmarxAdminConfig.persistConfiguration();
        } catch (IOException e) {
            Loggers.SERVER.error("Failed to persist global configurations", e);
        }
        getOrCreateMessages(httpServletRequest).addMessage("settingsSaved", "Global settings saved for Checkmarx AST Plugin.");

    }

    private String ensurePasswordEncryption(HttpServletRequest request, String requestParamName) {
        String password = RSACipher.decryptWebRequestData(request.getParameter(requestParamName));
        return PluginUtils.encrypt(password);
    }


    private ActionErrors validateForm(final HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();

        String globalAstServerUrl = request.getParameter(CheckmarxParams.GLOBAL_AST_SERVER_URL);
        if (com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces(CheckmarxParams.GLOBAL_AST_SERVER_URL)) {
            errors.addError(INVALID + CheckmarxParams.GLOBAL_AST_SERVER_URL, "AST Server URL must not be empty");
        } else {
            try {
                new URL(globalAstServerUrl);
            } catch (MalformedURLException e) {
                errors.addError(INVALID + CheckmarxParams.GLOBAL_AST_SERVER_URL, "AST Server Url is not valid.");
            }
        }

        return errors;
    }
    // Adapter classes to bridge jakarta.servlet to javax.servlet
    private static class JavaxRequestAdapter implements HttpServletRequest {
        private final jakarta.servlet.http.HttpServletRequest jakartaRequest;

        public JavaxRequestAdapter(jakarta.servlet.http.HttpServletRequest jakartaRequest) {
            this.jakartaRequest = jakartaRequest;
        }

        @Override
        public String getParameter(String name) {
            return jakartaRequest.getParameter(name);
        }

        @Override
        public String[] getParameterValues(String name) {
            return jakartaRequest.getParameterValues(name);
        }

        @Override
        public java.util.Map<String, String[]> getParameterMap() {
            return jakartaRequest.getParameterMap();
        }

        @Override
        public String getHeader(String name) {
            return jakartaRequest.getHeader(name);
        }

        @Override
        public String getMethod() {
            return jakartaRequest.getMethod();
        }

        @Override
        public String getRequestURI() {
            return jakartaRequest.getRequestURI();
        }

        @Override
        public String getContextPath() {
            return jakartaRequest.getContextPath();
        }

        @Override
        public Object getAttribute(String name) {
            return jakartaRequest.getAttribute(name);
        }

        @Override
        public void setAttribute(String name, Object value) {
            jakartaRequest.setAttribute(name, value);
        }

        // Add minimal implementation for other required methods
        @Override
        public String getAuthType() {
            return jakartaRequest.getAuthType();
        }

        @Override
        public javax.servlet.http.Cookie[] getCookies() {
            jakarta.servlet.http.Cookie[] jakartaCookies = jakartaRequest.getCookies();
            if (jakartaCookies == null) return null;

            javax.servlet.http.Cookie[] javaxCookies = new javax.servlet.http.Cookie[jakartaCookies.length];
            for (int i = 0; i < jakartaCookies.length; i++) {
                javaxCookies[i] = new javax.servlet.http.Cookie(jakartaCookies[i].getName(), jakartaCookies[i].getValue());
            }
            return javaxCookies;
        }

        // Implement other required methods with minimal functionality
        @Override
        public long getDateHeader(String name) {
            return jakartaRequest.getDateHeader(name);
        }

        @Override
        public java.util.Enumeration<String> getHeaders(String name) {
            return jakartaRequest.getHeaders(name);
        }

        @Override
        public java.util.Enumeration<String> getHeaderNames() {
            return jakartaRequest.getHeaderNames();
        }

        @Override
        public int getIntHeader(String name) {
            return jakartaRequest.getIntHeader(name);
        }

        @Override
        public String getPathInfo() {
            return jakartaRequest.getPathInfo();
        }

        @Override
        public String getPathTranslated() {
            return jakartaRequest.getPathTranslated();
        }

        @Override
        public String getQueryString() {
            return jakartaRequest.getQueryString();
        }

        @Override
        public String getRemoteUser() {
            return jakartaRequest.getRemoteUser();
        }

        @Override
        public boolean isUserInRole(String role) {
            return jakartaRequest.isUserInRole(role);
        }

        @Override
        public java.security.Principal getUserPrincipal() {
            return jakartaRequest.getUserPrincipal();
        }

        @Override
        public String getRequestedSessionId() {
            return jakartaRequest.getRequestedSessionId();
        }

        @Override
        public StringBuffer getRequestURL() {
            return new StringBuffer(jakartaRequest.getRequestURL().toString());
        }

        @Override
        public String getServletPath() {
            return jakartaRequest.getServletPath();
        }

        @Override
        public javax.servlet.http.HttpSession getSession(boolean create) {
            return null; // Simplified
        }

        @Override
        public javax.servlet.http.HttpSession getSession() {
            return null; // Simplified
        }

        @Override
        public String changeSessionId() {
            return jakartaRequest.changeSessionId();
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return jakartaRequest.isRequestedSessionIdValid();
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return jakartaRequest.isRequestedSessionIdFromCookie();
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return jakartaRequest.isRequestedSessionIdFromURL();
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return jakartaRequest.isRequestedSessionIdFromURL();
        }

        @Override
        public boolean authenticate(javax.servlet.http.HttpServletResponse response) {
            return false;
        }

        @Override
        public void login(String username, String password) throws javax.servlet.ServletException {
        }

        @Override
        public void logout() throws javax.servlet.ServletException {
        }

        @Override
        public java.util.Collection<javax.servlet.http.Part> getParts() {
            return null;
        }

        @Override
        public javax.servlet.http.Part getPart(String name) {
            return null;
        }

        @Override
        public <T extends javax.servlet.http.HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
            return null;
        }

        // ServletRequest methods
        @Override
        public java.util.Enumeration<String> getAttributeNames() {
            return jakartaRequest.getAttributeNames();
        }

        @Override
        public String getCharacterEncoding() {
            return jakartaRequest.getCharacterEncoding();
        }

        @Override
        public void setCharacterEncoding(String env) {
            try {
                jakartaRequest.setCharacterEncoding(env);
            } catch (java.io.UnsupportedEncodingException e) {
                // Handle the exception silently for this adapter
            }
        }

        @Override
        public int getContentLength() {
            return jakartaRequest.getContentLength();
        }

        @Override
        public long getContentLengthLong() {
            return jakartaRequest.getContentLengthLong();
        }

        @Override
        public String getContentType() {
            return jakartaRequest.getContentType();
        }

        @Override
        public javax.servlet.ServletInputStream getInputStream() {
            return null;
        }

        @Override
        public java.util.Enumeration<String> getParameterNames() {
            return jakartaRequest.getParameterNames();
        }

        @Override
        public String getProtocol() {
            return jakartaRequest.getProtocol();
        }

        @Override
        public String getScheme() {
            return jakartaRequest.getScheme();
        }

        @Override
        public String getServerName() {
            return jakartaRequest.getServerName();
        }

        @Override
        public int getServerPort() {
            return jakartaRequest.getServerPort();
        }

        @Override
        public java.io.BufferedReader getReader() {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return jakartaRequest.getRemoteAddr();
        }

        @Override
        public String getRemoteHost() {
            return jakartaRequest.getRemoteHost();
        }

        @Override
        public void removeAttribute(String name) {
            jakartaRequest.removeAttribute(name);
        }

        @Override
        public java.util.Locale getLocale() {
            return jakartaRequest.getLocale();
        }

        @Override
        public java.util.Enumeration<java.util.Locale> getLocales() {
            return jakartaRequest.getLocales();
        }

        @Override
        public boolean isSecure() {
            return jakartaRequest.isSecure();
        }

        @Override
        public javax.servlet.RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public String getRealPath(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return jakartaRequest.getRemotePort();
        }

        @Override
        public String getLocalName() {
            return jakartaRequest.getLocalName();
        }

        @Override
        public String getLocalAddr() {
            return jakartaRequest.getLocalAddr();
        }

        @Override
        public int getLocalPort() {
            return jakartaRequest.getLocalPort();
        }

        @Override
        public javax.servlet.ServletContext getServletContext() {
            return null;
        }

        @Override
        public javax.servlet.AsyncContext startAsync() {
            return null;
        }

        @Override
        public javax.servlet.AsyncContext startAsync(javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse) {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return jakartaRequest.isAsyncStarted();
        }

        @Override
        public boolean isAsyncSupported() {
            return jakartaRequest.isAsyncSupported();
        }

        @Override
        public javax.servlet.AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public javax.servlet.DispatcherType getDispatcherType() {
            return null;
        }
    }

    private static class JavaxResponseAdapter implements HttpServletResponse {
        private final jakarta.servlet.http.HttpServletResponse jakartaResponse;

        public JavaxResponseAdapter(jakarta.servlet.http.HttpServletResponse jakartaResponse) {
            this.jakartaResponse = jakartaResponse;
        }

        @Override
        public void setContentType(String type) {
            jakartaResponse.setContentType(type);
        }

        @Override
        public void setStatus(int status) {
            jakartaResponse.setStatus(status);
        }

        @Override
        public void setHeader(String name, String value) {
            jakartaResponse.setHeader(name, value);
        }

        // Add minimal implementation for other required methods
        @Override
        public void addCookie(javax.servlet.http.Cookie cookie) {
        }

        @Override
        public boolean containsHeader(String name) {
            return jakartaResponse.containsHeader(name);
        }

        @Override
        public String encodeURL(String url) {
            return jakartaResponse.encodeURL(url);
        }

        @Override
        public String encodeRedirectURL(String url) {
            return jakartaResponse.encodeRedirectURL(url);
        }

        @Override
        public String encodeUrl(String url) {
            return jakartaResponse.encodeURL(url);
        }

        @Override
        public String encodeRedirectUrl(String url) {
            return jakartaResponse.encodeRedirectURL(url);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            jakartaResponse.sendError(sc, msg);
        }

        @Override
        public void sendError(int sc) throws IOException {
            jakartaResponse.sendError(sc);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            jakartaResponse.sendRedirect(location);
        }

        @Override
        public void setDateHeader(String name, long date) {
            jakartaResponse.setDateHeader(name, date);
        }

        @Override
        public void addDateHeader(String name, long date) {
            jakartaResponse.addDateHeader(name, date);
        }

        @Override
        public void addHeader(String name, String value) {
            jakartaResponse.addHeader(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
            jakartaResponse.setIntHeader(name, value);
        }

        @Override
        public void addIntHeader(String name, int value) {
            jakartaResponse.addIntHeader(name, value);
        }

        @Override
        public void setStatus(int sc, String sm) {
            jakartaResponse.setStatus(sc);
        }

        @Override
        public int getStatus() {
            return jakartaResponse.getStatus();
        }

        @Override
        public String getHeader(String name) {
            return jakartaResponse.getHeader(name);
        }

        @Override
        public java.util.Collection<String> getHeaders(String name) {
            return jakartaResponse.getHeaders(name);
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return jakartaResponse.getHeaderNames();
        }

        // ServletResponse methods
        @Override
        public String getCharacterEncoding() {
            return jakartaResponse.getCharacterEncoding();
        }

        @Override
        public String getContentType() {
            return jakartaResponse.getContentType();
        }

        @Override
        public javax.servlet.ServletOutputStream getOutputStream() {
            return null;
        }

        @Override
        public java.io.PrintWriter getWriter() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {
            jakartaResponse.setCharacterEncoding(charset);
        }

        @Override
        public void setContentLength(int len) {
            jakartaResponse.setContentLength(len);
        }

        @Override
        public void setContentLengthLong(long len) {
            jakartaResponse.setContentLengthLong(len);
        }

        @Override
        public void setBufferSize(int size) {
            jakartaResponse.setBufferSize(size);
        }

        @Override
        public int getBufferSize() {
            return jakartaResponse.getBufferSize();
        }

        @Override
        public void flushBuffer() throws IOException {
            jakartaResponse.flushBuffer();
        }

        @Override
        public void resetBuffer() {
            jakartaResponse.resetBuffer();
        }

        @Override
        public boolean isCommitted() {
            return jakartaResponse.isCommitted();
        }

        @Override
        public void reset() {
            jakartaResponse.reset();
        }

        @Override
        public void setLocale(java.util.Locale loc) {
            jakartaResponse.setLocale(loc);
        }

        @Override
        public java.util.Locale getLocale() {
            return jakartaResponse.getLocale();
        }
    }
}
