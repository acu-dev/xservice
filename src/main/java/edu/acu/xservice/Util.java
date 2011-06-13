
package edu.acu.xservice;

import com.xythos.common.api.XythosException;
import com.xythos.security.api.Principal;
import com.xythos.security.api.PrincipalManager;
import com.xythos.security.api.UserBase;
import com.xythos.storageServer.api.Parameters;
import com.xythos.webview.WebviewUtil;

/**
 *
 * @author hgm02a
 */
public class Util {

    // I'm going to use rounded off values so that we never have a value beyound 4 digits
    private static final long 
            KB = 1000L,
            MB = 1000000L,
            GB = 1000000000L,
            TB = 100000000000L;

    public static String getPrintableNumberOfBytes(long bytes) {
        if (bytes < KB) {
            return bytes + "B";
        }
        if (bytes < MB) {
            return bytes / KB + "KB";
        }
        if (bytes < GB) {
            return bytes / MB + "MB";
        }
        if (bytes < TB) {
            return bytes / GB + "GB";
        }
        return bytes / TB + "TB";
    }

    public static String getPrincipalDisplayName(String p_principalID,
            UserBase p_loggedInUser)
            throws XythosException {
        Principal l_principal = WebviewUtil.findPrincipal(p_principalID, p_loggedInUser);
        if (l_principal == null) {
            if (p_principalID.equals(PrincipalManager.NO_USER_PRINCIPAL_ID)) {
                return "No User";
            }
            return "User No Longer Exists ("+p_principalID+")";
        }
        String l_languageCode = Parameters.getDefaultLanguage();
        if (p_loggedInUser != null) {
            l_languageCode = p_loggedInUser.getLanguageCode();
        }
        return l_principal.getTranslatedDisplayName(l_languageCode);
    }
}
