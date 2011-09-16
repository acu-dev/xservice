
package edu.acu.xservice;

import com.google.inject.Singleton;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides current and compatible versions of the xService and xDrive apps. This
 * is typically the first service the app requests when validating an account and
 * is requested on app launch, to check for updates.
 *
 * @author cjs00c
 */
@Singleton
public class VersionServlet extends HttpServlet {
	
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

			JSONObject versions = new JSONObject();
			versions.put("xservice", "0.1-SNAPSHOT");
			versions.put("xdrive", "0.1-SNAPSHOT");
			
			JSONObject out = new JSONObject();
			out.put("versions", versions);
			
			PrintWriter writer = response.getWriter();
			writer.write(out.toString());
			writer.flush();
			writer.close();
		
		} catch (JSONException e) {
			log.error("Unable to create JSONObject for /version", e);
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
