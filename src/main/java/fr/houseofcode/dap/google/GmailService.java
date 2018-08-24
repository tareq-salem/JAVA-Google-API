/**
 * Main
 */
package fr.houseofcode.dap.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import fr.houseofcode.dap.Launcher;

/**
 * @author adminHOC
 *
 */
public class GmailService {

    /** Application name. */
    private static final String APPLICATION_NAME = "Hoc Dap Admin";
    /** Json factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    /** Folder to store user Credential. */
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /** Small enough. */
    //private static final int SMALL_MAX_ITEM_PER_PAGE = 10;

    //public static final String defaultUser = "salemtareq5@gmail.com";
    // private static final List<String> SCOPES =
    // Collections.singletonList(GmailScopes.GMAIL_LABELS);

    /**
     *
     */

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    /** Build a new authorized API client service. */
    private static final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    /**
     * Create a new gmailService.
     * @return a GmailService
     * @throws IOException the IO
     */
    public Gmail getService() throws IOException {

        /** Gmail service */
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();

        return service;
    }

    /**
     * Creates an authorized Credential object.
     * @param httpTransport the httpTransport
     * @return the credential
     * @throws IOException the IOException
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        /**
         * Global instance of the scopes required by this quickstart. If modifying these
         * scopes, delete your previously saved credentials/ folder.
         */
        List<String> scopes = new ArrayList<String>();
        scopes.add(GmailScopes.GMAIL_LABELS);
        scopes.add(GmailScopes.GMAIL_READONLY);

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

}