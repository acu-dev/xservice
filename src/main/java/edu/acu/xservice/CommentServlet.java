
package edu.acu.xservice;

import com.google.inject.Singleton;
import com.xythos.common.api.XythosException;
import com.xythos.security.api.Context;
import com.xythos.security.api.UserBase;
import com.xythos.storageServer.api.Comment;
import com.xythos.storageServer.api.FileSystemEntry;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hgm02a
 */
@Singleton
public class CommentServlet extends XythosEntryServlet {

    private final Logger log = LoggerFactory.getLogger(CommentServlet.class);

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String path = request.getParameter("path");
        String comment = request.getParameter("comment");

        Context context = (Context) request.getAttribute(ContextPerRequestFilter.XYTHOS_CONTEXT);

        UserBase user = context.getContextUser();

        FileSystemEntry entry = null;
        try {
            entry = getFileSystemEntry(path, context);
        } catch (XythosException e) {
            log.error("Xythos Exception caught: ", e);
        }

        if (entry == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (comment == null || comment.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Comment c = null;
        try {
            c = entry.createComment(user.getPrincipalID(), comment);
        } catch (XythosException e) {
            log.error("Error creating comment", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an error while trying to create the comment");
            return;
        }

        if (c == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an error while trying to create the comment");
            return;
        }

        JSONObject object = new JSONObject();

        try {
            object.put("id", c.getID());
            object.put("text", c.getCommentText());
            object.put("date", c.getCreationDate().getTime());
            object.put("author", "Me");
            object.put("deletable", true);
        } catch (JSONException e) {
            log.error("Error creating JSONObject", e);
        } catch (XythosException e) {
            log.error("Error getting new comment info from xythos", e);
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
        return "Returns, creates, or edits comments for a specific file entry";
    }
}
