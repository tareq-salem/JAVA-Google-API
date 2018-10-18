package fr.houseofcode.dap.google.web;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.persistence.ManyToOne;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;

import fr.houseofcode.dap.Config;
import fr.houseofcode.dap.data.AppUser;
import fr.houseofcode.dap.data.AppUserRepository;
import fr.houseofcode.dap.data.GoogleUser;
import fr.houseofcode.dap.google.GoogleAccount;

/**
 * @author adminHOC
 *
 */
@Controller
public class GoogleController {

    /** LOG. */
    private static final Logger LOG = LogManager.getLogger();
    /**
     *
     */
    @Autowired
    private GoogleAccount googleAccount;

    /** GoogleAuthorizationCodeFlow. */
    @Autowired
    private GoogleAuthorizationCodeFlow flow;

    /** */
    @Autowired
    private Config configuration;

    /**
    *
    */
    @Autowired
    private AppUserRepository repository;

    /**
    *
    */
    @ManyToOne
    private AppUser appUser;

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
    @RequestMapping("/account/add/{googleAccount}") // /account/add/bob?userKey=gertrude
    public String addAccount(@PathVariable("googleAccount") final String googleAccountName,
            @RequestParam("userKey") final String userKey, final HttpServletRequest request,
            final HttpSession session) {
        Boolean canAdd = true;
        String response;

        //canAdd = googleAccount.canAddAccount(googleAccountName);

        // Vérifier que l'utilisateur (userKey) existe
        AppUser currentUser = repository.getByUserKey(userKey);
        //Erreur user inexistant
        if (null == currentUser) {
            LOG.info("erreur l'utilisateur n'existe pas");
            canAdd = false;
        } else {
            // Vérifier que account n'existe pas (googleAccount). rechercher dans le compte lié à currentUser
            List<GoogleUser> gUsers = currentUser.getGuser();
            // bouclé !

            for (GoogleUser guser : gUsers) {
                if (googleAccountName.equals(guser.getName())) {
                    //Erreur compte existant
                    LOG.info("erreur l'utilisateur existe deja");
                    canAdd = false;
                    break;
                }
            }
        }

        if (canAdd) {
            // redirect to the authorization flow
            final AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl();
            authorizationUrl.setRedirectUri(buildRedirectUri(request, configuration.getoAuth2CallbackUrl()));
            // store userId in session for CallBack Access
            session.setAttribute("userKey", userKey);
            session.setAttribute("googleAccountName", googleAccountName);
            response = "redirect:" + authorizationUrl.build();
        } else {
            response = "errorOccurs";
        }

        return response;
    }

    /**
     * Handle the Google response.
     * @param request The HTTP Request
     * @param code    The (encoded) code use by Google (token, expirationDate,...)
     * @param session the HTTP Session
     * @return the view to display
     * @throws ServletException When Google account could not be connected to DaP.
     * @throws IOException the IOException
     * @throws GeneralSecurityException the GeneralSecurityException
     */
    @RequestMapping("/oAuth2Callback")
    public String oAuthCallback(@RequestParam final String code, final HttpServletRequest request,
            final HttpSession session) {
        String redirect = "callBackError";

        try {
            final String decodedCode = extracCode(request);
            final String redirectUri = buildRedirectUri(request, configuration.getoAuth2CallbackUrl());
            final String userKey = getUserKey(session);
            final String googleAccountName = getGoogleAccountName(session);
            //s'inspirer de getUserKey(session);

            Boolean isAdded = googleAccount.retrieveUserTokenAndStore(decodedCode, redirectUri, googleAccountName);
            if (isAdded) {
                // sauvegarder lien entre User -> Account
                AppUser currentUser = repository.getByUserKey(userKey);
                currentUser.getGuser().add(new GoogleUser(googleAccountName));
                repository.save(currentUser);
                LOG.info(currentUser);

                redirect = "redirect:/emails/unread?userkey=" + userKey;
            }
        } catch (ServletException e) {
            LOG.error("Error while retrievieng Tokens form Callback", e);
        }

        return redirect;
    }

    /**
     * retrieve the User ID in Session.
     * @param session the HTTP Session
     * @return the current User Id in Session
     * @throws ServletException if no User Id in session
     */
    private String getUserKey(final HttpSession session) throws ServletException {
        String userKey = null;
        if (null != session && null != session.getAttribute("userKey")) {
            userKey = (String) session.getAttribute("userKey");
        }
        if (null == userKey) {
            LOG.error("userKey in Session is NULL in Callback");
            throw new ServletException("Error when trying to add Google acocunt : userKey is NULL is User Session");
        }
        return userKey;
    }

    /**
     * retrieve the googleAccountName in Session.
     * @param session the HTTP Session
     * @return the current googleAccountName in Session
     * @throws ServletException if no googleAccountName in session
     */
    private String getGoogleAccountName(final HttpSession session) throws ServletException {
        String googleAccountName = null;
        if (null != session && null != session.getAttribute("googleAccountName")) {
            googleAccountName = (String) session.getAttribute("googleAccountName");
        }
        if (null == googleAccountName) {
            LOG.error("googleAccountName in Session is NULL in Callback");
            throw new ServletException(
                    "Error when trying to add Google acocunt : googleAccountName is NULL in googleAccountName Session");
        }
        return googleAccountName;
    }

    /**
     * Extract OAuth2 Google code (from URL) and decode it.
     * @param request the HTTP request to extract OAuth2 code
     * @return the decoded code
     * @throws ServletException if the code cannot be decoded
     */
    private String extracCode(final HttpServletRequest request) throws ServletException {
        final StringBuffer buf = request.getRequestURL();
        if (null != request.getQueryString()) {
            buf.append('?').append(request.getQueryString());
        }
        final AuthorizationCodeResponseUrl responseUrl = new AuthorizationCodeResponseUrl(buf.toString());
        final String decodeCode = responseUrl.getCode();
        if (decodeCode == null) {
            throw new MissingServletRequestParameterException("code", "String");
        }
        if (null != responseUrl.getError()) {
            LOG.error("Error when trying to add Google acocunt : " + responseUrl.getError());
            throw new ServletException("Error when trying to add Google acocunt");
            // onError(request, resp, responseUrl);
        }
        return decodeCode;
    }

    /**
     * Build a current host (and port) absolute URL.
     * @param req         The current HTTP request to extract schema, host, port
     *                    informations
     * @param destination the "path" to the resource
     * @return an absolute URI
     */
    protected String buildRedirectUri(final HttpServletRequest req, final String destination) {
        final GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath(destination);
        return url.build();
    }

    /**
     * remove user.
     * @param userId userId
     * @return return
     * @throws IOException IOException
     */
    @RequestMapping("/account/remove/{userId}")
    public final String remove(@PathVariable final String userId) {
        String nextView;
        Boolean isDeleted = null;

        try {
            isDeleted = googleAccount.remove(userId);
        } catch (IOException e) {
            LOG.error("Error while removing account, userId : " + userId, e);
        }

        if (isDeleted) {
            nextView = "redirect:/admin";
        } else {
            nextView = "ErrorOccurs";
        }

        return nextView;

    }

    /**
     * Delete an internal user and his Google Tokens.
     * @return a goodBye message
     * @throws IOException if Google Errors
     */
    @RequestMapping("/account/deleted")
    public final String deleted() throws IOException {
        String response = "Account deleted";
        return response;
    }
}
