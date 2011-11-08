
package edu.acu.xservice;

import com.google.inject.Inject;
import com.sun.jersey.api.NotFoundException;
import edu.acu.xservice.api.DirectoryEntry;
import edu.acu.xservice.api.FileManager;
import java.util.Date;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * REST service to provide entry (file/directory) information. 
 *
 * @author hgm02a
 */
@Produces("application/json")
@Path("/entry/{id}")
public class EntryResource {

	private static final Logger logger = LoggerFactory.getLogger(EntryResource.class);
	
	@Context
	private Request request;
	
	private UriInfo uriInfo;
	
    private final String path;

	private FileManager files;

	private final DirectoryEntry entry;

    /** Creates a new instance of FileResource */
	@Inject
    private EntryResource(@PathParam("id") String pathParam, FileManager files, UriInfo uriInfo) throws EntryException {
		logger.debug("path -> {}", pathParam);
		logger.debug("absolutePath -> {}", uriInfo.getAbsolutePath().toString());
		logger.debug("pathParameters -> {}", uriInfo.getPathParameters().getFirst("id"));
		
		//path = uriInfo.getPathParameters().getFirst("id");
		
		if (pathParam == null) {
			pathParam = "/";
		}

        this.path = pathParam;
		this.files = files;
		
		this.entry = files.getDirectoryEntry(path);
		if (this.entry == null)
            throw new NotFoundException("Entry not found");
    }

	/**
	 * Retrieves representation of an instance of FileResource
	 * @return an instance of java.lang.String
	 */
	@GET
	public Response getEntry() throws EntryException {
		
		logger.debug("getEntry");
		Date lastModified = this.entry.getLastUpdated();
		String et = files.getEtag(entry);
		ResponseBuilder rb;
		
		if (et != null) {
			EntityTag etag = new EntityTag(et);
			rb = request.evaluatePreconditions(lastModified, etag);
		} else {
			rb = request.evaluatePreconditions(lastModified);
		}
		if (rb != null)
			return rb.build();
		
		rb = Response.ok(entry).lastModified(entry.getLastUpdated());
		et = files.getEtag(entry);
		if (et != null)
			rb.tag(et);
		logger.debug("tag -> {}", et);
		
		return rb.build();
	}

	/**
	 * PUT method for updating or creating an instance of FileResource
	 * @param content representation for the resource
	 * @return an HTTP response with content of the updated or created resource.
	 */ 
	@PUT
	@Consumes("application/json")
	public void putJson(String content) {
	}

	/**
	 * DELETE method for resource FileResource
	 */
	@DELETE
	public void delete() {
	}
}
