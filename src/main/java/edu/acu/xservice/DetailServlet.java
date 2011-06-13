
package edu.acu.xservice;

import com.google.inject.Singleton;
import com.xythos.common.api.XythosException;
import com.xythos.security.api.Context;
import com.xythos.security.api.UserBase;
import com.xythos.storageServer.api.Comment;
import com.xythos.storageServer.api.FileSystemDirectory;
import com.xythos.storageServer.api.FileSystemEntry;
import com.xythos.storageServer.api.FileSystemFile;
import com.xythos.storageServer.api.Subscription;
import com.xythos.storageServer.permissions.api.AccessControlEntry;
import com.xythos.storageServer.permissions.api.DirectoryAccessControlEntry;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Timestamp;
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
public class DetailServlet extends XythosEntryServlet {

    private final Logger log = LoggerFactory.getLogger(DetailServlet.class);

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
        String path = URLDecoder.decode(request.getParameter("path"), "UTF-8");

        Context context = (Context) request.getAttribute(ContextPerRequestFilter.XYTHOS_CONTEXT);

        UserBase user = context.getContextUser();

        JSONObject object = null;

        try {
            FileSystemEntry entry = getFileSystemEntry(path, context);

            if (entry == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            object = getJSONObject(entry, request);

            addDetails(object, entry, user);


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

    public void addDetails(JSONObject object, FileSystemEntry entry, UserBase user) throws JSONException {
        try {
            object.put("owner", Util.getPrincipalDisplayName(entry.getEntryOwnerPrincipalID(), user));
        } catch (XythosException e) {
            log.error("Failed to retrieve owner", e);
        }

        try {
            object.put("creator", Util.getPrincipalDisplayName(entry.getCreatedByPrincipalID(), user));
        } catch (XythosException e) {
            log.error("Failed to retrieve creator", e);
        }
        object.put("created", entry.getCreationTimestamp().getTime());

        try {
            object.put("description", entry.getDescription());
        } catch (XythosException e) {
            log.error("Failed to retrieve description", e);
        }

        try {
            object.put("updator", Util.getPrincipalDisplayName(entry.getLastUpdatedByPrincipalID(), user));
        } catch (XythosException e) {
            log.error("Failed to retrieve updator", e);
        }
        object.put("updated", entry.getLastUpdateTimestamp().getTime());

        String userId = user.getPrincipalID();
        boolean isOwner = false;
        try{
            isOwner = entry.getEntryOwnerPrincipalID().equals(userId);
        } catch (XythosException e) {
            log.error("Failed to figure out whether user is owner of the file", e);
        }
        AccessControlEntry userAcl = null;
        try {
            userAcl = isOwner ? entry.getAccessControlEntry("OWNER@PUBLIC") : entry.getAccessControlEntry(userId);
        } catch (XythosException e) {
            log.error("Couldn't get user's AccessControlEntry for the requested file", e);
        }

        try {
            JSONArray commentsArray = new JSONArray();
            Comment[] comments = entry.getComments();
            for (Comment c : comments) {
                JSONObject comment = new JSONObject();
                comment.put("id", c.getID());
                comment.put("text", c.getCommentText());
                comment.put("date", c.getCreationDate().getTime());
                String authorId = c.getAuthorPrincipalID();
                if(authorId.equals(user.getPrincipalID())){
                    comment.put("author", "Me");
                }else{
                    comment.put("author", Util.getPrincipalDisplayName(authorId, user));
                }
                comment.put("deletable", isOwner || userAcl.isDeleteable() || authorId.equals(userId) );
                commentsArray.put(comment);
            }
            object.put("comments", commentsArray);
        } catch (XythosException e) {
            log.error("Failed to retrieve comments", e);
        }
        
        try {
            JSONArray subscriptions = new JSONArray();
            Subscription[] subs = entry.getSubscriptions(user.getPrincipalID());
            for (Subscription s : subs) {
                JSONObject subscription = new JSONObject();
                subscription.put("id", s.getID());
                Timestamp expires = s.getExpirationDate();
                if (expires == null || expires.equals(Subscription.DOES_NOT_EXPIRE)) {
                    subscription.put("expires", "Does Not Expire");
                } else {
                    subscription.put("expires", expires.getTime());
                }
                int stype = s.getSubscriptionType();
                switch (stype) {
                    case Subscription.RESOURCE_READ_SUBSCRIPTION_TYPE:
                        subscription.put("type", "READ");
                        break;
                    case Subscription.RESOURCE_CHANGED_SUBSCRIPTION_TYPE:
                        subscription.put("type", "CHANGE");
                        break;
                    case Subscription.RESOURCE_COMMENT_CHANGED_SUBSCRIPTION_TYPE:
                        subscription.put("type", "COMMENT");
                        break;
                    default:
                        subscription.put("type", "UNKNOWN");
                        break;
                }
                int ntype = s.getNotificationType();
                switch (ntype) {
                    case Subscription.REPORT_NOTIFICATION_TYPE:
                        subscription.put("notification", "Daily Report");
                        break;
                    case Subscription.RESOURCE_NOTIFICATION_TYPE:
                        subscription.put("notification", "When events occur");
                        break;
                    default:
                        subscription.put("notification", "UNKNOWN");
                        break;
                }
                subscriptions.put(subscription);
            }
            object.put("subscriptions", subscriptions);
        } catch (XythosException e) {
            log.error("Failed to retrieve subscriptions", e);
        }

        try {
            if (userAcl != null && userAcl.isPermissionable()) {
                JSONArray shares = new JSONArray();
                AccessControlEntry[] acls = entry.getPrincipalAccessControlEntries();
                for (AccessControlEntry acl : acls) {
                    JSONObject aclObject = new JSONObject();
                    aclObject.put("id", acl.getPrincipalID());
                    aclObject.put("name", Util.getPrincipalDisplayName(acl.getPrincipalID(), user));
                    aclObject.put("read", acl.isReadable());
                    aclObject.put("write", acl.isWriteable());
                    aclObject.put("delete", acl.isDeleteable());
                    aclObject.put("grant", acl.isPermissionable());
                    if(acl instanceof DirectoryAccessControlEntry){
                        DirectoryAccessControlEntry dacl = (DirectoryAccessControlEntry)acl;
                        aclObject.put("inheritRead", dacl.isChildInheritReadable());
                        aclObject.put("inheritWrite", dacl.isChildInheritWriteable());
                        aclObject.put("inheritDelete", dacl.isChildInheritDeleteable());
                        aclObject.put("inheritGrant", dacl.isChildInheritPermissionable());
                    }
                    shares.put(aclObject);
                }
                object.put("permissions", shares);
            }
        } catch (XythosException e) {
            log.error("Failed to retrieve shares", e);
        }

        if (entry instanceof FileSystemDirectory) {
            FileSystemDirectory dir = (FileSystemDirectory) entry;
        } else if (entry instanceof FileSystemFile) {
            FileSystemFile file = (FileSystemFile) entry;
            try {
                String[] tags = file.getTags();
                JSONArray tagsArray = new JSONArray();
                for (String tag : tags) {
                    tagsArray.put(tag);
                }
                object.put("tags", tagsArray);
            } catch (XythosException e) {
                log.error("Failed to retrieve tags", e);
            }
        }
    }



    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Returns Entry Details";
    }
}
