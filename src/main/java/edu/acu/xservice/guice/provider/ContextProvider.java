
package edu.acu.xservice.guice.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.xythos.security.api.Context;
import edu.acu.xservice.filter.ContextPerRequestFilter;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author hgm02a
 */
public class ContextProvider implements Provider<Context>{

	@Inject
	private HttpServletRequest request;

	public ContextProvider() {
		
	}

	@Override
	public Context get() {
		return (Context) request.getAttribute(ContextPerRequestFilter.XYTHOS_CONTEXT);
	}
	
}
