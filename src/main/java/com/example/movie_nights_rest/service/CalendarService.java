package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.exception.InternalServerErrorException;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    @Setter
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @Setter
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;

    private GoogleCredential getCredentials(String refreshToken) {

        try {
            GoogleTokenResponse response = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance(),
                    refreshToken, CLIENT_ID, CLIENT_SECRET).execute();
            return new GoogleCredential().setAccessToken(response.getAccessToken());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void test(String refreshToken) {
        var credentials = getCredentials(refreshToken);

        var calendar = new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credentials)
                .setApplicationName("Test")
                .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events;

        try {
            events = calendar.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
//                    .setQ("etag=Movie_Nights")
                    .execute();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new InternalServerErrorException();
        }

        var items = events.getItems();

        if (items.isEmpty()) System.out.println("No upcoming events found.");
        else {
            System.out.println("Upcoming events: ");
            for (Event event : items) {
                var start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }

                var end = event.getEnd().getDateTime();
                if (end == null) {
                    end = event.getStart().getDate();
                }

                System.out.printf("%s (%s) -> (%s)\n", event.getSummary(), start, end);
            }
        }
    }



    private List<Event> getAllEventsBetween(DateTime from, DateTime to, Credential credentials) {
        var calendar = new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credentials)
                .setApplicationName("Test")
                .build();

        try {
           return calendar.events().list("primary")
                    .setTimeMin(from)
                    .setTimeMax(to)
                    .setSingleEvents(true)
                    .execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalServerErrorException();
        }

    }
}
