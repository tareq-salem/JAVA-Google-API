/**
 *
 */
package fr.houseofcode.dap.google.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;

import fr.houseofcode.dap.google.GoogleUserService;

/**
 * @author adminHOC
 *
 */
@Controller
public class AdminController {

    /** googleUserService. */
    @Autowired
    private GoogleUserService googleUserService;

    /**
     * Request view welcome.html.
     * @param model mvc == view injection in
     * @exception  IOException Google error
     * @exception GeneralSecurityException Google security error
     * @return welcome tpl
     */
    @RequestMapping("/admin")
    public String admin(final Model model) throws IOException, GeneralSecurityException {
        DataStore<StoredCredential> response = null;
        response = googleUserService.getUserCredential();

        Set<String> googleUserKeys = response.keySet();

        Map<String, StoredCredential> credentialsList = new HashMap<>();

        //alimenter Map à partir du DataStore
        for (String googleUserKey : googleUserKeys) {
            credentialsList.put(googleUserKey, response.get(googleUserKey));
        }

        //ajouter la MAP au model
        model.addAttribute("credentialList", credentialsList);

        return "admin.html";
    }

}
