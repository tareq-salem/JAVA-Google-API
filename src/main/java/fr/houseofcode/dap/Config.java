/**
 *
 */
package fr.houseofcode.dap;

/**
 * @author adminHOC
 *
 */
public class Config {

    /** Folder to store autorisations grant by user. */
    private static final String TOKEN_FOLDER = "tokens";
    /** Location of Json "Google ID" file. */
    private static final String CLIENT_SECRET_FILE = "/credentials.json";
    /** display name of the application. */
    private static final String APPLICATION_NAME = "gHoC DaP";
    /** display name of the application. */
    private static final String OAUTH_CALLBACK_URL = "/oAuth2Callback";

    /**  */
    private String applicationName;
    /** */
    private String tokenFolder;
    /** */
    private String clientSecretFile;
    /** */
    private String oAuth2CallbackUrl;

    /** */
    public Config() {
        super();
        this.applicationName = APPLICATION_NAME;
        this.tokenFolder = TOKEN_FOLDER;
        this.clientSecretFile = CLIENT_SECRET_FILE;
        this.oAuth2CallbackUrl = OAUTH_CALLBACK_URL;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param appName the applicationName to set
     */
    public void setApplicationName(final String appName) {
        this.applicationName = appName;
    }

    /**
     * @return the credentialFolder
     */
    public String getTokenFolder() {
        return tokenFolder;
    }

    /**
     * @param credentFolder the credentialFolder to set
     */
    public void setTokenFolder(final String credentFolder) {
        this.tokenFolder = credentFolder;
    }

    /**
     * @return the clientSecretFile
     */
    public String getClientSecretFile() {
        return clientSecretFile;
    }

    /**
     * @param secretFile the clientSecretFile to set
     */
    public void setClientSecretFile(final String secretFile) {
        this.clientSecretFile = secretFile;
    }

    /**
     *
     * @return return
     */
    public String getoAuth2CallbackUrl() {
        return oAuth2CallbackUrl;
    }

    /**
     *
     * @param oAuth2CallbackUrl oAuth2CallbackUrl
     */
    public void setoAuth2CallbackUrl(final String oAuth2CallbackUrl) {
        this.oAuth2CallbackUrl = oAuth2CallbackUrl;
    }

}
