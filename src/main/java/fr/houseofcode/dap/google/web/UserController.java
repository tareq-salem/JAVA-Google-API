/**
 *
 */
package fr.houseofcode.dap.google.web;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.houseofcode.dap.data.AppUser;
import fr.houseofcode.dap.data.AppUserRepository;

/**
 * @author adminHOC
 *
 */
@RestController
public class UserController {

    /** LOG. */
    private static final Logger LOG = LogManager.getLogger();

    @Autowired
    private AppUserRepository repository;

    /**
     * Add a Google account (user will be prompt to connect and accept required
     * access).
     * @param userId  the user to store Data
     * @param request the HTTP request
     * @param session the HTTP session
     * @return the view to Display (on Error)
     * @throws IOException the IOException
     * @throws GeneralSecurityException the GeneralSecurityException
     */
    @RequestMapping("/user/add/{userId}")
    public void addUser(@PathVariable final String userKey, final HttpServletRequest request,
            final HttpSession session) {
        AppUser savedEntity = repository.save(new AppUser(userKey));

        LOG.debug(savedEntity);
    }
}