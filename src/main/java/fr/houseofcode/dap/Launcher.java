package fr.houseofcode.dap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

/**
 *
 */
public final class Launcher {

    /** Application name. */
    private static final String APPLICATION_NAME = "Hoc Dap Admin";
    /** Json factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    /** Folder to store user Credential. */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /** Small enough. */
    private static final int SMALL_MAX_ITEM_PER_PAGE = 10;

    //public static final String defaultUser = "salemtareq5@gmail.com";
    // private static final List<String> SCOPES =
    // Collections.singletonList(GmailScopes.GMAIL_LABELS);

    /**
     *
     */

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     *
     */
    private Launcher() {
        throw new UnsupportedOperationException("utility class");
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        /**
         * Global instance of the scopes required by this quickstart. If modifying these
         * scopes, delete your previously saved credentials/ folder.
         */
        List<String> scopes = new ArrayList<String>();
        scopes.add(GmailScopes.GMAIL_LABELS);
        scopes.add(GmailScopes.GMAIL_READONLY);
        scopes.add(CalendarScopes.CALENDAR_READONLY);

        // Load client secrets.
        InputStream in = Launcher.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
                clientSecrets, scopes)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                        .setAccessType("offline").build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    /**
     * create jdjdj jdjd jdjdd.
     * @param args the args
     * @throws IOException the IOException
     * @throws GeneralSecurityException the GeneralSecurityException
     */

    public static void main(final String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME).build();

        Calendar calendarService = new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME).build();

        getLabel(service, "me", "UNREAD");

        // --- recuoérer la liste des ID de mesages
        List<Message> listMessage = listMessages(service, "me", "unique32154");

        // -- -- pour chaque messsage appelre la methode "getMEssage)

        System.out.println("Nb Messages : " + listMessage.size());

        for (Message message : listMessage) {
            // -- -- Afficher le "body" du message
            getMessage(service, "me", message.getId());

        }

        getEvents(calendarService, "salemtareq5@gmail.com");

    }

    /**
     * list message from Google account.
     * @param service the service
     * @param userId the userId ("me")
     * @param query the querry
     * @return messages
     * @throws IOException the OException
     */

    public static List<Message> listMessages(final Gmail service, final String userId, final String query)
            throws IOException {
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

        @SuppressWarnings("unused")
        String bob = "";
        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query).setPageToken(pageToken).execute();
                messages.addAll(response.getMessages());
            } else {
                break;
            }
        }

        return messages;
    }

    /**
     * Retrieve labels from Google account.
     * @param service the service
     * @param userId The userId ("me", or Google ID)
     * @param labelId messageId The userId
     * @throws IOException when Google error occurs
     */

    public static void getLabel(final Gmail service, final String userId, final String labelId) throws IOException {
        Label label = service.users().labels().get(userId, labelId).execute();

        System.out.println("Label " + label.getName() + " retrieved.");
        System.out.println(label.toPrettyString());
    }

    /**
     * Retrieve an email from Google account.
     * @param service The service
     * @param userId The userId ("me", or Google ID)
     * @param messageId The userId
     * @return the Message
     * @throws IOException when Google error occurs
     */
    public static Message getMessage(final Gmail service, final String userId, final String messageId)
            throws IOException {

        Message message = service.users().messages().get(userId, messageId).execute();
        System.out.println("Message snippet: " + message.getSnippet());

        return message;
    }

    /**
     * Retrieve an event from Google account agenda.
     * @param calendarService The service
     * @param calendarId The calendarId (your adresse email)
     * @return the event
     * @throws IOException when Google error occurs
     */
    public static Events getEvents(final Calendar calendarService, final String calendarId) throws IOException {

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = calendarService.events().list(calendarId).setMaxResults(SMALL_MAX_ITEM_PER_PAGE).setTimeMin(now)
                .setOrderBy("startTime").setSingleEvents(true).execute();
        // CalendarListEntry calendarListEntry =
        // calendarService.calendarList().get("calendarId").execute();
        // System.out.println(calendarListEntry.getSummary());
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
        return events;
    }
}
