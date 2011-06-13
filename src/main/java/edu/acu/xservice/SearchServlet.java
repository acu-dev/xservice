
package edu.acu.xservice;

import com.google.inject.Singleton;
import com.xythos.common.api.NetworkAddress;
import com.xythos.common.api.VirtualServer;
import com.xythos.common.api.XythosException;
import com.xythos.security.api.Context;
import com.xythos.security.api.UserBase;
import com.xythos.storageServer.api.FileSystemDirectory;
import com.xythos.storageServer.api.FileSystemEntry;
import com.xythos.storageServer.api.classification.ClassificationManager;
import com.xythos.util.api.ServletUtil;
import com.xythos.webdav.api.WebdavLogic;
import com.xythos.webdav.dasl.api.DaslResultSet;
import com.xythos.webdav.dasl.api.DaslStatement;
import com.xythos.webui.Details;
import com.xythos.webui.WebuiUtil;
import com.xythos.webview.dropbox.DropBoxPropertyStore;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.StringTokenizer;
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
public class SearchServlet extends XythosEntryServlet {

    private final Logger log = LoggerFactory.getLogger(SearchServlet.class);

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
        String searchString = URLDecoder.decode(request.getParameter("search"), "UTF-8");

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
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (!(entry instanceof FileSystemDirectory)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Requested path was not a directory.");
                return;
            }

            object = getJSONObject(entry, request);

            log.debug("entry is directory");
            FileSystemDirectory directory = (FileSystemDirectory) entry;

            DaslStatement l_daslStatement = new DaslStatement(getDASL(searchString, entry.getName(), request), context);
            DaslResultSet l_daslResult = l_daslStatement.executeDaslQuery(true);

            JSONArray entriesArray = new JSONArray();

            if (l_daslResult != null) {
                while (l_daslResult.nextEntry()) {
                    FileSystemEntry e = l_daslResult.getCurrentEntry();

                    entriesArray.put(getJSONObject(e, request));
                }
            }

            object.put("contents", entriesArray);
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

    protected String getDASL(String searchString, String directory, HttpServletRequest request) throws XythosException {
        HttpSession session = request.getSession();
        String l_searchFrom = directory;
        if (!l_searchFrom.equals("")) {
            if (l_searchFrom.charAt(0) != '/') {
                l_searchFrom = "/" + l_searchFrom;
            }
        }
        String l_path = WebdavLogic.getEntryURL(directory, request);

        VirtualServer l_vServer = NetworkAddress.findVirtualServer(request.getServerName(), request.getServerPort());

        String l_dasl = "<?xml version=\"1.0\"?>\n" +
                "<D:searchrequest xmlns:D=\"DAV:\" " +
                " xmlns:S=\"" + Details.WEBUINS +
                "\" xmlns:X=\"" + WebdavLogic.WFSNS + "\"" +
                " xmlns:C=\"" + ClassificationManager.getDocumentClassPropertyNamespace(l_vServer) + "\"" +
                " xmlns:" + DropBoxPropertyStore.DROPBOX_XML_NAMESPACE_ALIAS + "=\"" + DropBoxPropertyStore.DROPBOX_NAMESPACE + "\"" +
                ">\n" +
                "<D:basicsearch>\n" +
                "  <D:select>\n" +
                "    <D:prop><D:getcontentlength/></D:prop>\n" +
                "  </D:select>\n" +
                "  <D:from>\n" +
                "    <D:scope>\n" +
                "      <D:href>" +
                WebdavLogic.getEntryURL(directory, request) +
                "</D:href>\n" +
                "      <D:depth>infinity</D:depth>\n" +
                "    </D:scope>\n" +
                "  </D:from>\n" +
                "  <D:where>\n";
        l_dasl +=
                "    <D:or>" +
                "       <X:contains><![CDATA[" + searchString + "]]></X:contains>\n" +
                "       <X:comment-contains><![CDATA[" + WebuiUtil.escapeDaslLikeString(searchString) + "]]></X:comment-contains>\n";
        StringTokenizer l_tokenizer = new StringTokenizer(searchString);
        int l_numToks = l_tokenizer.countTokens();
        if (l_numToks > 0) {
            l_dasl += "<D:and> \n ";
        }
        while (l_tokenizer.hasMoreElements()) {
            String l_tag = (String) l_tokenizer.nextElement();
            l_dasl += " <X:some-eq  casesensitive=\"0\"> <D:prop><X:Tags/></D:prop><D:literal><![CDATA[" + l_tag + "]]></D:literal></X:some-eq>\n";

        }
        if (l_numToks > 0) {
            l_dasl += "</D:and> \n";
        }
        l_dasl +=
                "    </D:or>\n" +
                "  </D:where>\n" +
                "</D:basicsearch>\n" +
                "<X:vserver>" +
                ServletUtil.makeHtmlSafe(l_vServer.getName()) +
                "</X:vserver>\n" +
                "<X:localpath>" +
                ServletUtil.string2HttpSafe(directory) +
                "</X:localpath>\n" +
                "<X:searchtype>advancedsearch</X:searchtype>\n" +
                "</D:searchrequest>\n";
        return l_dasl;
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Returns a search of a directory";
    }
}
