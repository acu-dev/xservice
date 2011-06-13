package edu.acu.xservice;

import com.google.inject.Singleton;
import com.xythos.common.api.XythosException;
import com.xythos.security.api.Context;
import com.xythos.security.api.UserBase;
import com.xythos.storageServer.api.FileSystemDirectory;
import com.xythos.storageServer.api.FileSystemEntry;
import com.xythos.storageServer.api.FileSystemFile;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hgm02a
 */
@Singleton
public class DirectoryServlet extends XythosEntryServlet {

	private final Logger log = LoggerFactory.getLogger(DirectoryServlet.class);

	/** 
	 * Handles the HTTP <code>GET</code> method.
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String requestPath = request.getParameter("path");
		if (requestPath == null) {
			// Bad request (path was not given)
			response.setStatus(400);
			response.setContentType("text/html");
			PrintWriter writer = response.getWriter();
			writer.write("<h2>missing 'path' parameter</h2>");
			writer.flush();
			writer.close();
			return;
		}
		
		HttpSession session = request.getSession();
		String path = URLDecoder.decode(requestPath, "UTF-8");

		Context context = (Context) request.getAttribute(ContextPerRequestFilter.XYTHOS_CONTEXT);

		UserBase user = context.getContextUser();

		JSONObject object = null;

		try {
			FileSystemEntry entry;
			if (path.equals("Home")) {
				entry = user.getHomeDirectory(context);
			} else if (path.equals("Courses")) {
				entry = getFileSystemEntry("/courses", context);
			} else if (path.equals("Departments")) {
				entry = getFileSystemEntry("/departments", context);
			} else {
				entry = getFileSystemEntry(path, context);
			}

			if (entry == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			object = getJSONObject(entry, request);

			if (entry instanceof FileSystemDirectory) {
				log.debug("entry is directory");
				FileSystemDirectory directory = (FileSystemDirectory) entry;
				FileSystemEntry[] entries = directory.getReadableDirectoryContents(false);
				JSONArray entriesArray = new JSONArray();
				for (FileSystemEntry e : entries) {
					entriesArray.put(getJSONObject(e, request));
				}
				object.put("contents", entriesArray);
			} else if (entry instanceof FileSystemFile) {
				log.debug("entry is file");
			}
		} catch (XythosException e) {
			log.error("Xythos Exception caught: ", e);
		} catch (JSONException e) {
			log.error("Error creating JSONObject", e);
		}

		if (object != null) {
//            response.setContentType("application/json");

			PrintWriter writer = response.getWriter();
			writer.write(object.toString());
			writer.flush();
			writer.close();
			return;
		}

		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unknown error occurred.");
	}

	/** 
	 * Returns a short description of the servlet.
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Returns a directory listing";
	}
}
