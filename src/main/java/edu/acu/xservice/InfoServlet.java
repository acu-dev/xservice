
package edu.acu.xservice;

import com.google.inject.Singleton;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cjs00c
 */
@Singleton
public class InfoServlet extends HttpServlet {
	
	private final Logger log = LoggerFactory.getLogger(CommentServlet.class);

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// Server
			JSONObject server = new JSONObject();
			server.put("protocol", request.getScheme());
			server.put("host", request.getServerName());
			server.put("port", request.getServerPort());
			server.put("servicePath", "/xservice");
			
			// Default paths
			JSONArray paths = new JSONArray();
			
			JSONObject home = new JSONObject();
			home.put("name", "Home");
			home.put("path", "Home");
			paths.put(home);
			
			JSONObject courses = new JSONObject();
			courses.put("name", "Courses");
			courses.put("path", "/courses");
			paths.put(courses);

			JSONObject departments = new JSONObject();
			departments.put("name", "Departments");
			departments.put("path", "/departments");
			paths.put(departments);

			// Output info
			JSONObject info = new JSONObject();
			info.put("server", server);
			info.put("defaultPaths", paths);
			
			PrintWriter writer = response.getWriter();
			writer.write(info.toString());
			writer.flush();
			writer.close();
			
		} catch (JSONException e) {
			log.error("Unable to create JSONObject for /info", e);
		}
    }
	
	/** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Returns server info and default app configuration";
    }
}
