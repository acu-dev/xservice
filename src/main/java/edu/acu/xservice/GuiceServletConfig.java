
package edu.acu.xservice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hgm02a
 */
public class GuiceServletConfig extends GuiceServletContextListener {

    private final Logger log = LoggerFactory.getLogger(GuiceServletConfig.class);

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new ServletModule() {
                    @Override
                    protected void configureServlets() {
						
						// Xythos user filter
						filter("/*").through(UserBaseFilter.class);

						// Session context filter
                        filter("/*").through(ContextPerRequestFilter.class);

						// Servlets
						serve("/version").with(VersionServlet.class);
						serve("/info").with(InfoServlet.class);
                        serve("/detail/comment/*").with(CommentServlet.class);
                        serve("/detail/subscription/*").with(SubscriptionServlet.class);
                        serve("/detail/*").with(DetailServlet.class);
                        serve("/directory/*").with(DirectoryServlet.class);
                        serve("/search/*").with(SearchServlet.class);
                    }
                });
    }

}
