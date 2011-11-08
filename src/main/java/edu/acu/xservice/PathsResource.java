
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
 * REST service to provide a list of default paths xDrive should configure on
 * setup. Paths should have been loaded at startup.
 * 
 * @author cjs00c
 */
@Produces("application/json")
@Path("/paths")
public class PathsResource {
	
	private static final Logger log = LoggerFactory.getLogger(PathsResource.class);
	
	private final List<Map<String, String>> paths;
	
	@Inject
    private PathsResource(@Named("XdrivePaths") List<Map<String, String>> xdrivePaths) throws EntryException {
		this.paths = xdrivePaths;
    }
	
	@GET
	public Response getInfo()
	{
		return Response.ok(paths).build();
	}
	
}
