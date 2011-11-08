/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.acu.xservice.xythos;

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
public class XythosUtil {
	
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
