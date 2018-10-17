/**
 * Main
 */
package fr.houseofcode.dap.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

/**
 * @author adminHOC
 *
 */
@Service
public class GmailService extends GoogleService {

    /**
     * Logger.
     */
    private static final Logger LOG = LogManager.getLogger();

    /** Small enough. */
    //private static final int SMALL_MAX_ITEM_PER_PAGE = 10;

    /**
     * Build a GmailService using specific Configuration.
     */
    public GmailService() {
        super();
    }

    /**
     * create.
     * @param userId the userId
     * @return the service
     * @throws GeneralSecurityException the GeneralSecurityException
     * @throws IOException the IOException
     */
    private Gmail getService(final String userId) throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(httpTransport, getJsonFactory(), getCredentials(userId))
                .setApplicationName(getConfiguration().getApplicationName()).build();
        return service;
    }

    /**
     * Retrieve labels from Google account.
     * @param userId The internal userId
     * @param labelId messageId The userId
     * @return the Google Label
     * @throws IOException when Google error occurs
     * @throws GeneralSecurityException the GeneralSecurityException
     */
    @RequestMapping("/label/{labelid}")
    public Label displayLabel(@RequestParam("userkey") final String userId,
            @PathVariable("labelid") final String labelId) throws IOException, GeneralSecurityException {
        Gmail service = getService(userId);
        Label label = service.users().labels().get("me", labelId).execute();

        System.out.println("Label " + label.getName() + " retrieved.");
        System.out.println(label.toPrettyString());

        return label;
    }

    /**
     * create.
     * @param userId userId
     * @return return
     * @throws IOException IOException
     * @throws GeneralSecurityException IOException
     */
    public Integer getNbUnreadEmail(final String userId) throws IOException, GeneralSecurityException {
        List<Message> allMessages = getMessages(userId, "me", "is:UNREAD in:inbox");
        System.out.println(allMessages);
        return allMessages.size();
    }

    /**
     * create.
     * @param userId userId
     * @param gUserId gUserId
     * @param query query
     * @return return
     * @throws IOException IOException
     * @throws GeneralSecurityException GeneralSecurityException
     */
    @RequestMapping("/message/{guserid}")
    private List<Message> getMessages(@RequestParam("userkey") final String userId,
            @PathVariable("guserid") final String gUserId, final String query)
            throws IOException, GeneralSecurityException {
        Gmail service = getService(userId);
        ListMessagesResponse response = service.users().messages().list(gUserId).setQ(query).execute();

        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(gUserId).setQ(query).setPageToken(pageToken).execute();
                messages.addAll(response.getMessages());
            } else {
                break;
            }
        }

        return messages;
    }

    /**
     * list message from Google account.
     *
     * @param userId the userId ("me")
     * @param query the query
     * @return messages
     * @throws IOException the OException
     * @throws GeneralSecurityException the GeneralSecurityException
     * @throws
     */
    public String listMessages(final String userId, final String query) throws IOException, GeneralSecurityException {
        List<Message> messages = getMessages(userId, "me", query);
        /** structurer la String de resultat*/
        String builtMessage = null;
        for (Message message : messages) {
            builtMessage += message.getId() + System.getProperty("line.separator");
        }

        //LOG.info("Nb unread Emails :" + messages.size());

        return builtMessage;
    }
}
