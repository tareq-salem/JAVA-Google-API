/**
 *
 */
package fr.houseofcode.dap.google;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.FileSystemNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.gmail.GmailScopes;

import fr.houseofcode.dap.Config;
import fr.houseofcode.dap.GoogleFacade;

/**
 * @author adminHOC
 *
 */
public class GoogleService {
    /**
     * Logger.
     */
    public static final Logger LOG = LogManager.getLogger();
    /** Json factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    /** */
    @Autowired
    private Config configuration;

    /** Google Authorization Flow. */
    private GoogleAuthorizationCodeFlow flow;

    /**
     * Prepare a Google Service.
     * @param config configuration to pickup App specific configuration (credential
     *               folder, application name, JSON App credential file, ...)
     *
     */
    public GoogleService(final Config config) {
        this.configuration = config;
    }

    /** */
    private List<String> scopes;

    /**
     *
     */
    public GoogleService() {
        init();
    }

    /**
    *
    */
    private void init() {
        scopes = new ArrayList<String>();
        scopes.add(GmailScopes.GMAIL_LABELS);
        scopes.add(GmailScopes.GMAIL_READONLY);
        scopes.add(CalendarScopes.CALENDAR_READONLY);
    }

    /**
     * Creates an authorized Credential object.
     * @param httpTransport the httpTransport
     * @return An authorized Credential object.
     * @throws IOException the IOException
     */
    public Credential getCredentialsOld(final NetHttpTransport httpTransport) throws IOException {
        /**
         * Global instance of the scopes required by this quickstart. If modifying these
         * scopes, delete your previously saved credentials/ folder.
         */
        //init scope = new init();

        // Load client secrets.
        InputStream in = GmailService.class.getResourceAsStream(configuration.getClientSecretFile());
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow newFlow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
                clientSecrets, scopes)
                        .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(configuration.getTokenFolder())))
                        .setAccessType("offline").build();
        return new AuthorizationCodeInstalledApp(newFlow, new LocalServerReceiver()).authorize("user");
    }

    /**
     * Creates an authorized Credential object.
     * @param userId the App User ID
     * @return An authorized Credential object.
     * @throws IOException If there is no client_secret.
     */
    public Credential getCredentials(final String userId) throws IOException {
        return getFlow().loadCredential(userId);
        // installed App code
        // return new AuthorizationCodeInstalledApp(flow, new
        // LocalServerReceiver()).authorize("user");
    }

    /**
     * Initialize Google flow (if not already initialized) and return it.
     * @return a Google Authorization Flow
     * @throws IOException if Google Error occurs
     */
    @Bean
    public GoogleAuthorizationCodeFlow getFlow() throws IOException {
        if (null == flow) {
            flow = initializeFlow();
        }
        return flow;
    }

    /**
     * Initialize a Google Flow.
     * @return the Google Flow
     * @throws IOException if Google Error occurs
     */
    public GoogleAuthorizationCodeFlow initializeFlow() throws IOException {
        // Load client secrets.
        Reader appClientSecret = null;
        final File appClientSecretFile = new File(configuration.getClientSecretFile());
        if (appClientSecretFile.exists()) {
            appClientSecret = new InputStreamReader(new FileInputStream(appClientSecretFile), Charset.forName("UTF-8"));
        } else {
            // try with app local data (not recommended to store this file in public
            // repository)
            final InputStream appClientSecretStream = GoogleFacade.class
                    .getResourceAsStream(configuration.getClientSecretFile());
            if (null != appClientSecretStream) {
                appClientSecret = new InputStreamReader(appClientSecretStream, Charset.forName("UTF-8"));
            }
        }
        if (null == appClientSecret) {
            final String message = "No AppCredentialFile to connect to Google App. This file should be in : "
                    + configuration.getClientSecretFile();
            LOG.error(message);
            throw new FileSystemNotFoundException(message);
        }
        final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(getJsonFactory(), appClientSecret);
        // Build flow and trigger user authorization request.
        final GoogleAuthorizationCodeFlow newFlow = new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(),
                getJsonFactory(), clientSecrets, scopes)
                        .setDataStoreFactory(new FileDataStoreFactory(new File(configuration.getTokenFolder())))
                        .setAccessType("offline").build();
        return newFlow;
    }

    /**
     * @return the jsonFactory
     */
    protected static JsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }

    /**
     * @return the configuration
     */
    protected Config getConfiguration() {
        return configuration;
    }

    /**
     *
     * @param conf the conf
     */
    public void setConfiguration(final Config conf) {
        this.configuration = conf;
    }

}
