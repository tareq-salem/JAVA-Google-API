package fr.houseofcode.dap;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import fr.houseofcode.dap.data.AppUser;
import fr.houseofcode.dap.data.AppUserRepository;

/**
 * Spring Boot launcher.
 */
@SpringBootApplication
public class Launcher {

    /**
     * Logger.
     */
    private static final Logger LOG = LogManager.getLogger();

    /** Application name. */
    //private static final String APPLICATION_NAME = "Hoc Dap Admin";
    /** Json factory. */
    //private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    /** Folder to store user Credential. */
    //private static final String TOKENS_DIRECTORY_PATH = "tokens";
    /** Small enough. */
    //private static final int SMALL_MAX_ITEM_PER_PAGE = 10;

    public static final String DEFAULT_USER = "user";

    /**
     *
     */
    //private Launcher() {
    //throw new UnsupportedOperationException("utility class");
    // }

    /**
     * create jdjdj jdjd jdjdd.
     * @param args the args
     * @throws IOException the IOException
     * @throws GeneralSecurityException the GeneralSecurityException
     */
    public static void main(final String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        SpringApplication.run(Launcher.class, args);
        //        Config appConf = new Config();
        //        appConf.setApplicationName("ma super app!");
        //        appConf.setTokenFolder("tokens");
        //
        //        System.out.println("Bienvenue dans :" + appConf.getApplicationName());
        //
        //        GmailService gmailService = new GmailService();
        //        gmailService.setConfiguration(appConf);
        //
        //        //String messages = gmailService.listMessages("me", "");
        //        //System.out.println(messages);
        //
        //        gmailService.displayLabel(DEFAULT_USER, "UNREAD");
        //        gmailService.getNbUnreadEmail(DEFAULT_USER);
        //
        //        CalendarService calendarService = new CalendarService();
        //        calendarService.setConfiguration(appConf);
        //        calendarService.displayNextEvent(DEFAULT_USER, "primary");

    }

    /**
     *
     * @return the return
     */
    @Bean
    public Config createConfig() {
        Config appConf = new Config();
        appConf.setApplicationName("ma super app!");
        appConf.setTokenFolder(System.getProperty("user.home") + System.getProperty("file.separator") + "dap"
                + System.getProperty("file.separator") + "tokens");
        appConf.setClientSecretFile(System.getProperty("user.home") + System.getProperty("file.separator") + "dap"
                + System.getProperty("file.separator") + "credentials.json");

        return appConf;
    }
    //Controller Con = new Controller();

    @Bean
    public CommandLineRunner demo(AppUserRepository repository) {
        return (args) -> {
            repository.save(new AppUser("Bob"));
            repository.save(new AppUser("Gertrude"));
            repository.save(new AppUser("Twiggy"));

            Iterable<AppUser> allAppUser = repository.findAll();

            for (AppUser user : allAppUser) {
                LOG.info(user);
            }
        };
    }
}
