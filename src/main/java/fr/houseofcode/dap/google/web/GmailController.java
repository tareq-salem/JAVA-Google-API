package fr.houseofcode.dap.google.web;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.houseofcode.dap.google.GmailService;

/**
 * @author adminHOC
 *
 */
@RestController
@RequestMapping("/emails")
public class GmailController {

    /**
     * Logger.
     */
    public static final Logger LOG = LogManager.getLogger();

    /**
     * ca.
     */
    @Autowired
    private GmailService gmailService;

    /**
     * create.
     * @param userId userId
     * @return return
     * @throws IOException IOException
     * @throws GeneralSecurityException IOException
     */
    @RequestMapping("/unread")
    public Integer getNbUnreadEmail(@RequestParam("userkey") final String userId) {

        Integer mail = null;

        try {
            mail = gmailService.getNbUnreadEmail(userId);
        } catch (IOException e) {
            LOG.error("erreur lors de l'appel du nb unread rmail  pour le userId : " + userId, e);
        } catch (GeneralSecurityException e) {
            LOG.error("erreur lors de l'appel du nb unread rmail pour le userId : " + userId, e);
        }

        return mail;

    }

}
