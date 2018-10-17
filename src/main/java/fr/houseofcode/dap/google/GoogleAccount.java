/**
 *
 */
package fr.houseofcode.dap.google;

import java.io.IOException;

import javax.servlet.ServletException;

import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;

/**
 * @author adminHOC
 *
 */
@Service
public class GoogleAccount extends GoogleService {
    /** */
    private static final int SENSIBLE_DATA_FIRST_CHAR = 3;
    /**  */
    private static final int SENSIBLE_DATA_LAST_CHAR = 8;

    /**
     * Add a Google account (user will be prompt to connect and accept required
     * access).
     * @param userId  the user to store Data
     * @param request the HTTP request
     * @param session the HTTP session
     * @return the view to Display (on Error)
     */
    public Boolean canAddAccount(final String userId) {
        Boolean response = false;
        GoogleAuthorizationCodeFlow flow;
        Credential credential = null;
        try {
            flow = super.getFlow();
            credential = flow.loadCredential(userId);
            if (credential != null && credential.getAccessToken() != null) {
                response = false;
            } else {
                response = true;
            }
        } catch (IOException e) {
            LOG.error("Error while loading credential (or Google Flow)", e);
        }
        // only when error occurs, else redirected BEFORE
        return response;
    }

    /**
     * Handle the Google response.
     * @param request The HTTP Request
     * @param code    The (encoded) code use by Google (token, expirationDate,...)
     * @param session the HTTP Session
     * @return the view to display
     * @throws ServletException When Google account could not be connected to DaP.
     */
    public Boolean retrieveUserTokenAndStore(final String decodedCode, final String redirectUri, final String userId)
            throws ServletException {
        Boolean isAdded = false;
        try {
            final GoogleAuthorizationCodeFlow flow = super.getFlow();
            final TokenResponse response = flow.newTokenRequest(decodedCode).setRedirectUri(redirectUri).execute();
            final Credential credential = flow.createAndStoreCredential(response, userId);
            if (null == credential || null == credential.getAccessToken()) {
                LOG.warn("Trying to store a NULL AccessToken for user : " + userId);
            }
            if (LOG.isDebugEnabled()) {
                if (null != credential && null != credential.getAccessToken()) {
                    LOG.debug("New user credential stored with userId : " + userId + "partial AccessToken : "
                            + credential.getAccessToken().substring(SENSIBLE_DATA_FIRST_CHAR, SENSIBLE_DATA_LAST_CHAR));
                    isAdded = true;
                }
            }
            // onSuccess(request, resp, credential);
        } catch (IOException e) {
            LOG.error("Exception while trying to store user Credential", e);
            throw new ServletException("Error while trying to conenct Google Account");
        }

        //return "redirect:/";

        return isAdded;
    }

    /**
     * remove user.
     * @param userId userId
     * @return return
     * @throws IOException IOException
     */
    public final Boolean remove(final String userId) throws IOException {
        Boolean response = false;
        GoogleAuthorizationCodeFlow flow;

        flow = super.getFlow();
        Credential credential = flow.loadCredential(userId);
        if (credential != null && credential.getAccessToken() != null) {
            flow.getCredentialDataStore().delete(userId);
            response = true;
        }
        return response;
    }

    /**
     *
     */
    public GoogleAccount() {
        // TODO Auto-generated constructor stub
    }

}
