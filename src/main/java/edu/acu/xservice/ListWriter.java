
package edu.acu.xservice;

import com.google.inject.Singleton;
import flexjson.JSONSerializer;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serializes List objects into JSON and writes to provided OutputStream.
 *
 * @author cjs00c
 */
@Singleton
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ListWriter implements MessageBodyWriter<List> {
	
	private static final Logger logger = LoggerFactory.getLogger(ListWriter.class);

	private final JSONSerializer serializer;

	public ListWriter() {
		serializer = new JSONSerializer();
	}

	@Override
	public boolean isWriteable(Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
		return List.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(List t, Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
		return -1;
	}

	@Override
	public void writeTo(List t, Class<?> type, Type type1, Annotation[] antns, MediaType mt, MultivaluedMap<String, Object> mm, OutputStream out) 
			throws IOException, WebApplicationException {
		logger.debug("ListWriter writing...");
		try {
			out.write(serializer.serialize(t).getBytes());
		} catch (Exception e) {
			logger.error("Error serializing", e);
		}
	}
	
}
