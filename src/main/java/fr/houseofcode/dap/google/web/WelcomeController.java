/**
 *
 */
package fr.houseofcode.dap.google.web;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.houseofcode.dap.google.GmailService;

/**
 * @author adminHOC
 *
 */
@Controller
public class WelcomeController {
    /**
     *
     */
    @Autowired
    private GmailService gmailService;

    /**
    * Request view welcome.html.
    * @param model mvc == view injection in
    * @return welcome tpl
    */
    @RequestMapping("/")
    public String welcome(final Model model) {
        Integer nbEmail = null;
        try {
            nbEmail = gmailService.getNbUnreadEmail("gertrude");
        } catch (IOException e) {

            e.printStackTrace();
        } catch (GeneralSecurityException e) {

            e.printStackTrace();
        }
        model.addAttribute("userName", nbEmail);
        return "welcome";
    }
}
