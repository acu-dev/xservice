/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.acu.xservice;

import edu.acu.xservice.api.File;
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

/**
 *
 * @author hgm02a
 */
@Singleton
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class FileWriter implements MessageBodyWriter<File> {

	private final JSONSerializer serializer;

	public FileWriter() {
		serializer = new JSONSerializer();
	}

	@Override
	public boolean isWriteable(Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
		return File.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(File t, Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
		return -1;
	}

	@Override
	public void writeTo(File t, Class<?> type, Type type1, Annotation[] antns, MediaType mt, MultivaluedMap<String, Object> mm, OutputStream out) throws IOException, WebApplicationException {
		out.write(serializer.serialize(t).getBytes());
	}

}
