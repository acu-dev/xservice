
package edu.acu.xservice;

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
	
	@GET
	public Response getInfo()
	{
		return Response.ok().build();
	}
}
