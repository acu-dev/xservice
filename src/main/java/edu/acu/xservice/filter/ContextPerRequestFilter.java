/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.acu.xservice.filter;

import com.google.inject.Singleton;
import com.xythos.common.api.XythosException;
import com.xythos.security.api.Context;
import com.xythos.security.api.ContextFactory;
import com.xythos.security.api.SessionManager;
import com.xythos.security.api.UserBase;
import com.xythos.webview.WebviewUtil;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static edu.acu.xservice.filter.UserBaseFilter.XYTHOS_USER_BASE;

/**
 *
 * @author hgm02a
 */
@Singleton
public class ContextPerRequestFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(ContextPerRequestFilter.class);
    public static final String XYTHOS_CONTEXT = "com.xythos.security.api.Context";
    public static final String XYTHOS_SESSION_ID = "edu.acu.files.XYTHOS_SESSION_ID";
    public static final String XYTHOS_SECURITY_TOKEN = "edu.acu.files.XYTHOS_SECURITY_TOKEN";

    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("init");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        logger.debug("doFilter");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();

        UserBase user = (UserBase) session.getAttribute(XYTHOS_USER_BASE);
        if (user == null) {
			logger.error("UserBase is null.  That should not be possible.");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "UserBase is null");
			return;
        }
        String username;
		try {
			username = user.getID();
		} catch (XythosException ex) {
			logger.error("Error getting username", ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error getting username");
			return;
		}

        String sessionId = (String) session.getAttribute(XYTHOS_SESSION_ID);
        if (sessionId == null) {
			try {
				sessionId = SessionManager.createSession(user, request);
			} catch (XythosException e) {
				logger.error("Error creating Xythos session for " + username, e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating Xythos Session");
				return;
			}
            session.setAttribute(XYTHOS_SESSION_ID, sessionId);
			
			String sToken = WebviewUtil.getSecurityToken(sessionId);
			session.setAttribute(XYTHOS_SECURITY_TOKEN, sToken);

			logger.debug("Xythos Security Token -> " + sToken);
        }

        logger.debug("Xythos Session Id for " + username + " -> " + sessionId);

        Context context = null;
        try {
            context = ContextFactory.create(user, null);
        } catch (XythosException e) {
            logger.error("Error creating Xythos context for user " + username, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating Xyhos Context");
            return;
        }

        if (context == null) {
            logger.error("Error creating Xythos context for user " + username);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating Xyhos Context");
            return;
        }

        request.setAttribute(XYTHOS_CONTEXT, context);

        chain.doFilter(servletRequest, servletResponse);

        try {
            context.commitContext();
        } catch (XythosException e) {
            logger.error("Error committing context for " + username, e);
        }
    }

    public void destroy() {
        logger.debug("destroy");
    }
}
