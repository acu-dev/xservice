
package edu.acu.xservice.guice.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.xythos.security.api.Context;
import edu.acu.xservice.filter.ContextPerRequestFilter;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hgm02a
 */
public class ContextProvider implements Provider<Context>{
	
	private static final Logger logger = LoggerFactory.getLogger(ContextProvider.class);

	@Inject
	private HttpServletRequest request;

	public ContextProvider() {
		
	}

	@Override
	public Context get() {
		logger.debug("get()");
		Context context = (Context) request.getAttribute(ContextPerRequestFilter.XYTHOS_CONTEXT);
		//return (Context) request.getAttribute(ContextPerRequestFilter.XYTHOS_CONTEXT);
		if (context == null)
			logger.error("Request context is null!");
		return context;
	}
	
}
