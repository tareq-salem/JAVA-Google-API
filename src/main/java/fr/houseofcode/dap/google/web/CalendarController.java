package fr.houseofcode.dap.google.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.houseofcode.dap.data.AppUser;
import fr.houseofcode.dap.data.GoogleUser;
import fr.houseofcode.dap.google.CalendarService;

/**
 * @author adminHOC
 *
 */
@RestController
@RequestMapping("/events")
public class CalendarController {
    /** LOG. */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * ca.
     */
    @Autowired
    private CalendarService calendarService;

    /**
    *
    * @param userId userId
    * @param calendarId calendarId
    * @throws GeneralSecurityException  GeneralSecurityException
    * @throws IOException  IOException
    * @return Event sUmary
    */
    @RequestMapping("/next/{calendarkey}")
    public String getNextEvents(@RequestParam("userkey") final String userId,
            @PathVariable("calendarkey") final String calendarId) {

        AppUser currentUser = repository.getByUserKey(userKey);
        List<GoogleUser> gUsers = currentUser.getGuser();
        String event = null;

        try {
            event = calendarService.displayNext3Events(userId, calendarId);
        } catch (IOException e) {
            LOG.error("erreur lors de l'appel du NEXT event pour le userId : " + userId, e);
        } catch (GeneralSecurityException e) {
            LOG.error("erreur lors de l'appel du NEXT event pour le userId : " + userId, e);
        }

        return event;

    }

}
