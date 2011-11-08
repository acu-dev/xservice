
package edu.acu.xservice.filter;

import com.google.inject.Singleton;
import com.xythos.common.api.NetworkAddress;
import com.xythos.common.api.VirtualServer;
import com.xythos.common.api.XythosException;
import com.xythos.security.api.AuthenticationException;
import com.xythos.security.api.SessionManager;
import com.xythos.security.api.UserBase;
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


/**
 *
 * @author hgm02a
 */
@Singleton
public class UserBaseFilter implements Filter {
	
	private static final Logger logger = LoggerFactory.getLogger(UserBaseFilter.class);
	
    public static final String XYTHOS_USER_BASE = "com.xythos.security.api.UserBase";

	public void init(FilterConfig filterConfig) throws ServletException {
		logger.trace("init");
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		logger.trace("doFilter");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
		
		UserBase user = (UserBase) session.getAttribute(XYTHOS_USER_BASE);
		
        if (user == null) {
			
			VirtualServer vServer = NetworkAddress.findVirtualServer(request);
			
			try {
				// Get Xythos user from authentication response
				user = SessionManager.getUserFromAuthentication(request);
			} catch (AuthenticationException e) {
				logger.debug("Failed to authenticate with username {}", request.getRemoteUser(), e);
			} catch (XythosException e) {
				logger.debug("Error authenticating", e);
			}
			
			if (user == null) {
				// Send authentication challenge
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.addHeader("WWW-Authenticate", "Basic realm=\"" + vServer.getName() + "\"");
				return;
			}
			
			// Store user in session
			session.setAttribute(XYTHOS_USER_BASE, user);
			
			
			// Create user if they don't exist
			
//			if(user == null) {
//				try {
//					user = PrincipalManager.findUser(username, VirtualServer.getDefaultVirtualServer().getName());
//
//					try {
//						if (AcuXythosUtil.isNewUser(user)) {
//							logger.debug("User is new!");
//							AcuXythosUtil.setupNewUser(user);
//						}
//						session.setAttribute(XYTHOS_USER_BASE, user);
//					} catch (XythosException e) {
//						log.error("Error checking whether '" + username + "' is a new user", e);
//					}
//
//				} catch (XythosException e) {
//					log.error("Error finding Userbase for " + username, e);
//					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error finding user " + username);
//					return;
//				}
//			}
        }
		
		chain.doFilter(servletRequest, servletResponse);
	}

	public void destroy() {
		logger.trace("destroy");
	}
	
}
