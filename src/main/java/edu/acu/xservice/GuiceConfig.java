
package edu.acu.xservice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.xythos.security.api.Context;
import edu.acu.xservice.api.FileManager;
import edu.acu.xservice.filter.ContextPerRequestFilter;
import edu.acu.xservice.filter.UserBaseFilter;
import edu.acu.xservice.guice.provider.ContextProvider;
import edu.acu.xservice.xythos.XythosFileManager;
import flexjson.JSONDeserializer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hgm02a
 */
public class GuiceConfig extends GuiceServletContextListener {
	
	private static final Logger log = LoggerFactory.getLogger(GuiceConfig.class);
	
	@Override
	protected Injector getInjector() {
		return Guice.createInjector(
                new ServletModule() {
                    @Override
                    protected void configureServlets() {
						
						// Load custom paths for xDrive setup
						InputStream is = getClass().getResourceAsStream("/XdrivePaths.json");
						
						if (is == null)
						{
							// Use bundled default paths
							is = getClass().getResourceAsStream("/DefaultPaths.json");
						}
						
						// De-serialize JSON to validate
						List<Map<String, String>> paths = new JSONDeserializer<List<Map<String, String>>>()
								.deserialize(new InputStreamReader(is));

						// Bind to named annotation for injection
						bind(new TypeLiteral<List<Map<String, String>>>() {})
								.annotatedWith(Names.named("XdrivePaths"))
								.toInstance(paths);
						
						
						
						// Handle user auth
						filter("/*").through(UserBaseFilter.class);
						
						// Set session context
                        filter("/*").through(ContextPerRequestFilter.class);
						
						
						bind(Context.class).toProvider(ContextProvider.class);
						bind(FileManager.class).to(XythosFileManager.class);
						
						
						// Entries
						bind(EntryResource.class);
						bind(DirectoryWriter.class);
						bind(FileWriter.class);
						
						// Info
						bind(InfoResource.class);
						bind(ListWriter.class);

						serve("/*").with(GuiceContainer.class);
                    }
                },
				new JerseyServletModule());
	}

}
