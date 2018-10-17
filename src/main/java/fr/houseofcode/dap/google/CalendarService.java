/**
 *
 */
package fr.houseofcode.dap.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

/**
 * @author adminHOC
 *
 */
@Service
public class CalendarService extends GoogleService {

    /** Small enough. */
    private static final int SMALL_MAX_ITEM_PER_PAGE = 3;

    /**
     *
     */
    public CalendarService() {
        super();
    }

    /**
     * create.
     * @param userId the userId
     * @return the service
     * @throws GeneralSecurityException the GeneralSecurityException
     * @throws IOException the IOException
     */
    private Calendar getService(final String userId) throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(httpTransport, getJsonFactory(), getCredentials(userId))
                .setApplicationName(getConfiguration().getApplicationName()).build();

        return service;
    }

    //    /**
    //     *
    //     * @param userId userId
    //     * @param calendarId calendarId
    //     * @throws GeneralSecurityException  GeneralSecurityException
    //     * @throws IOException  IOException
    //     * @return Event sUmary
    //     */
    //    public String displayNextEvent(final String userId, final String calendarId)
    //            throws GeneralSecurityException, IOException {
    //        String reponse = "No upcoming events found.";
    //        Calendar service = getService(userId);
    //
    //        //rechercher le 3 prochain Event
    //        DateTime now = new DateTime(System.currentTimeMillis());
    //        Events events = service.events().list(calendarId).setMaxResults(SMALL_MAX_ITEM_PER_PAGE).setTimeMin(now)
    //                .setOrderBy("startTime").setSingleEvents(true).execute();
    //        List<Event> items = events.getItems();
    //
    //        Event nextEvent = items.get(0);
    //
    //        //        if (items.isEmpty()) {
    //        //            System.out.println("No upcoming events found.");
    //        //        } else {
    //        //            System.out.println("Upcoming events");
    //        //            for (Event event : items) {
    //        //                DateTime start = event.getStart().getDateTime();
    //        //                if (start == null) {
    //        //                    start = event.getStart().getDate();
    //        //                }
    //        //                System.out.printf("%s (%s)\n", event.getSummary(), start);
    //        //                reponse = event.getSummary();
    //        //            }
    //        //        }
    //        if (null != nextEvent) {
    //            reponse = nextEvent.getSummary();
    //            //System.out.println(reponse);
    //        }
    //
    //        return reponse;
    //    }

    /**
    *
    * @param userId userId
    * @param calendarId calendarId
    * @throws GeneralSecurityException  GeneralSecurityException
    * @throws IOException  IOException
    * @return Event sUmary
    */
    public String displayNext3Events(final String userId, final String calendarId)
            throws GeneralSecurityException, IOException {
        String reponse = "";
        Calendar service = getService(userId);

        //rechercher le prochain Event
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list(calendarId).setMaxResults(SMALL_MAX_ITEM_PER_PAGE).setTimeMin(now)
                .setOrderBy("startTime").setSingleEvents(true).execute();
        List<Event> items = events.getItems();

        if (items.isEmpty()) {
            //System.out.println("No upcoming events found.");
            reponse = "No upcoming events found.";
        } else {
            //System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                //System.out.printf("%s (%s)\n", event.getSummary(), start);
                //System.out.println(event.getSummary(), start);
                reponse += event.getSummary() + "--";
            }
        }

        return reponse;
    }
}
