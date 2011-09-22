package edu.acu.xservice;

import com.xythos.common.api.VirtualServer;
import com.xythos.common.api.XythosException;
import com.xythos.security.api.Context;
import com.xythos.storageServer.api.FileSystem;
import com.xythos.storageServer.api.FileSystemDirectory;
import com.xythos.storageServer.api.FileSystemEntry;
import com.xythos.storageServer.api.FileSystemFile;
import com.xythos.webui.WebuiUtil;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hgm02a
 */
public class XythosEntryServlet extends HttpServlet {

	private final Logger log = LoggerFactory.getLogger(XythosEntryServlet.class);

	protected FileSystemEntry getFileSystemEntry(String path, Context context) throws XythosException {
		log.debug("getFileSystemEntry(" + path + ")");
		FileSystemEntry entry = null;
		VirtualServer virtualServer = context.getContextUser().getHomeDirectoryVirtualServer();
		if (virtualServer == null) {
			log.error("VirtualServer is null");
			throw new XythosException("Could not get VirtualServer") {
			};
		}
		log.debug("VirtualServer - " + virtualServer.getName());
		if (path != null && !path.equals("")) {
			//try and find the entry set in the request (set by links in dir list UI)
			entry = FileSystem.findEntry(virtualServer, path, false, context);
		}
		log.debug("Entry -> " + entry);
		return entry;
	}

	protected JSONObject getJSONObject(FileSystemEntry entry, HttpServletRequest request) throws JSONException {
		
		HttpSession session = request.getSession();
		JSONObject object = new JSONObject();
		String path = entry.getName();
		
		// Full path
		if (path.equals("")) {
			object.put("path", "/");
		} else {
			object.put("path", path);
		}
		
		// Last modified
		object.put("lastModified", entry.getLastUpdateTimestamp());
		
		// File size & type
		if (entry instanceof FileSystemFile) {
			
			object.put("size", Util.getPrintableNumberOfBytes(entry.getEntrySize()));

			//object.put("url", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "?" + WebuiUtil.SESSIONID_PARAM + "=" + session.getAttribute(ContextPerRequestFilter.XYTHOS_SESSION_ID) + "&" + WebuiUtil.SECURITY_TOKEN_NAME + "=" + session.getAttribute(ContextPerRequestFilter.XYTHOS_SECURITY_TOKEN));

			try {
				object.put("type", entry.getFileContentType());
			} catch (XythosException e) {
				log.warn("Error getting mimetype", e);
			}
			
		} else if (entry instanceof FileSystemDirectory) {
			object.put("type", "folder");
		}
		return object;
	}
}
