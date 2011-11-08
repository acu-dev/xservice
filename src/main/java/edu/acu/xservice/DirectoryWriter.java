/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.acu.xservice;

import com.xythos.storageServer.api.FileSystemDirectory;
import edu.acu.xservice.api.Directory;
import flexjson.JSONSerializer;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.inject.Singleton;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hgm02a
 */
@Singleton
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class DirectoryWriter implements MessageBodyWriter<Directory> {
	
	private static final Logger logger = LoggerFactory.getLogger(DirectoryWriter.class);

	private final JSONSerializer serializer;

	public DirectoryWriter() {
		serializer = new JSONSerializer();
		serializer.include("contents").exclude("*.class");
	}

	@Override
	public boolean isWriteable(Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
		logger.debug("Type -> {}", type.toString());
		logger.debug("Directory.class.isAssignableFrom(type) -> {}", Directory.class.isAssignableFrom(type));
		return Directory.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(Directory t, Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
		return -1;
	}

	@Override
	public void writeTo(Directory t, Class<?> type, Type type1, Annotation[] antns, MediaType mt, MultivaluedMap<String, Object> mm, OutputStream out) throws IOException, WebApplicationException {
		logger.debug("writeTo");
		try {
			out.write(serializer.serialize(t).getBytes());
		} catch (Exception e) {
			logger.error("Error serializing", e);
		}
	}

}
