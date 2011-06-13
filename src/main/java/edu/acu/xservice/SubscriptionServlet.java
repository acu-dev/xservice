
package edu.acu.xservice;

import com.google.inject.Singleton;
import com.xythos.common.api.XythosException;
import com.xythos.security.api.Context;
import com.xythos.security.api.UserBase;
import com.xythos.storageServer.api.FileSystemEntry;
import com.xythos.storageServer.api.Subscription;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
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
public class SubscriptionServlet extends XythosEntryServlet {

    private final Logger log = LoggerFactory.getLogger(SubscriptionServlet.class);

    /**
     * Handles the HTTP <code>DELETE</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("doDelete -> " + request.getRequestURL() + request.getQueryString());
//        log.debug("body -> " + request.get)
        HttpSession session = request.getSession();
        String path = request.getParameter("path");
        log.debug("path -> " + path);
        String idString = request.getParameter("id");
        log.debug("id -> " + idString);
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "A valid subscription id must be provided.");
            return;
        }

        Context context = (Context) request.getAttribute(ContextPerRequestFilter.XYTHOS_CONTEXT);

        UserBase user = context.getContextUser();

        FileSystemEntry entry = null;
        try {
            entry = getFileSystemEntry(path, context);
        } catch (XythosException e) {
            log.error("Xythos Exception caught", e);
        }

        if (entry == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            Subscription[] subs = entry.getSubscriptions(user.getPrincipalID());
            Subscription subscription = null;
            for (Subscription s : subs) {
                if (s.getID() == id) {
                    subscription = s;
                }
            }
            if (subscription == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not find that subscription on the specified entry.");
                return;
            }

            try{
                subscription.delete();
            }catch(XythosException e){
                log.error("Error deleting subscription", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            return;

        } catch (XythosException e) {
            log.error("Xythos Exception caught", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

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
        HttpSession session = request.getSession();
        String path = request.getParameter("path");
        String idString = request.getParameter("id");
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "A valid subscription id must be provided.");
            return;
        }

        Context context = (Context) request.getAttribute(ContextPerRequestFilter.XYTHOS_CONTEXT);

        UserBase user = context.getContextUser();

        FileSystemEntry entry = null;
        try {
            entry = getFileSystemEntry(path, context);
        } catch (XythosException e) {
            log.error("Xythos Exception caught", e);
        }

        if (entry == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            Subscription[] subs = entry.getSubscriptions(user.getPrincipalID());
            Subscription subscription = null;
            for (Subscription s : subs) {
                if (s.getID() == id) {
                    subscription = s;
                }
            }
            if (subscription == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not find that subscription on the specified entry.");
                return;
            }

            JSONObject object = null;
            try {
                object = getSubscriptionJSON(subscription);
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

        } catch (XythosException e) {
            log.error("Xythos Exception caught", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }


        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unknown error occurred.");
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
        String typeString = request.getParameter("type");
        int type;
        if (typeString.equals("CHANGE")) {
            type = Subscription.RESOURCE_CHANGED_SUBSCRIPTION_TYPE;
        } else if (typeString.equals("COMMENT")) {
            type = Subscription.RESOURCE_COMMENT_CHANGED_SUBSCRIPTION_TYPE;
        } else if (typeString.equals("READ")) {
            type = Subscription.RESOURCE_READ_SUBSCRIPTION_TYPE;
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid type provided. Must be one of (CHANGE, COMMENT, READ)");
            return;
        }
        String notificationString = request.getParameter("notification");
        int notification;
        if (notificationString.equals("REPORT")) {
            notification = Subscription.REPORT_NOTIFICATION_TYPE;
        } else if (notificationString.equals("INSTANT")) {
            notification = Subscription.RESOURCE_NOTIFICATION_TYPE;
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid notification type provided. Must be one of (REPORT, INSTANT)");
            return;
        }

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

        Subscription s = null;
        try {
            s = entry.createSubscription(user.getPrincipalID(), type, notification, Subscription.DOES_NOT_EXPIRE);
        } catch (XythosException e) {
            log.error("Error creating subscription", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an error while trying to create the subscription");
            return;
        }

        if (s == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an error while trying to create the subscription");
            return;
        }

        JSONObject object = null;
        try {
            object = getSubscriptionJSON(s);
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

    public JSONObject getSubscriptionJSON(Subscription s) throws JSONException {
        JSONObject object = new JSONObject();

        object.put("id", s.getID());
        Timestamp expires = s.getExpirationDate();
        if (expires == null || expires.equals(Subscription.DOES_NOT_EXPIRE)) {
            object.put("expires", "Does Not Expire");
        } else {
            object.put("expires", expires.getTime());
        }
        int stype = s.getSubscriptionType();
        switch (stype) {
            case Subscription.RESOURCE_READ_SUBSCRIPTION_TYPE:
                object.put("type", "READ");
                break;
            case Subscription.RESOURCE_CHANGED_SUBSCRIPTION_TYPE:
                object.put("type", "CHANGE");
                break;
            case Subscription.RESOURCE_COMMENT_CHANGED_SUBSCRIPTION_TYPE:
                object.put("type", "COMMENT");
                break;
            default:
                object.put("type", "UNKNOWN");
                break;
        }
        int ntype = s.getNotificationType();
        switch (ntype) {
            case Subscription.REPORT_NOTIFICATION_TYPE:
                object.put("notification", "Daily Report");
                break;
            case Subscription.RESOURCE_NOTIFICATION_TYPE:
                object.put("notification", "When events occur");
                break;
            default:
                object.put("notification", "UNKNOWN");
                break;
        }

        return object;
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
