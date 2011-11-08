
package edu.acu.xservice;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST service to provide info about xService.
 *
 * @author cjs00c
 */
@Produces("application/json")
@Path("/info")
public class InfoResource {
	
	private static final Logger log = LoggerFactory.getLogger(InfoResource.class);
	
	private final List<Map<String, String>> defaultPaths;
	
	@Inject
    private InfoResource(@Named("DefaultPaths") List<Map<String, String>> defaults) throws EntryException {
		this.defaultPaths = defaults;
    }
	
	@GET
	public Response getInfo()
	{
		return Response.ok(defaultPaths).build();
	}
}
