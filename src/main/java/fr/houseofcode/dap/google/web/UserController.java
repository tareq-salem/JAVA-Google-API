/**
 *
 */
package fr.houseofcode.dap.google.web;

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

    /**
     *
     */
    @Autowired
    private AppUserRepository repository;

    /**
     * Add a Google account (user will be prompt to connect and accept required
     * access).
     * @param userKey  the user to store Data
     */
    @RequestMapping("/user/add/{userKey}")
    public void addUser(@PathVariable("userKey") final String userKey) {
        AppUser savedEntity = repository.save(new AppUser(userKey));

        LOG.info(savedEntity);
    }
}
