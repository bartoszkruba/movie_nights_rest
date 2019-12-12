package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.movieWatching.MovieWatchingCommand;
import com.example.movie_nights_rest.exception.BadRequestException;
import com.example.movie_nights_rest.exception.InternalServerErrorException;
import com.example.movie_nights_rest.exception.ResourceNotFoundException;
import com.example.movie_nights_rest.model.User;
import com.example.movie_nights_rest.repository.MovieRepository;
import com.example.movie_nights_rest.repository.MovieWatchingRepository;
import com.example.movie_nights_rest.repository.UserRepository;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    @Setter
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @Setter
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;

    private final UserRepository userRepository;
    private final MovieWatchingRepository movieWatchingRepository;
    private final MovieRepository movieRepository;

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

    public MovieWatchingCommand createMovieWatching(String creatorId, String[] attendees, Long startTime, String movieId) {

        var movie = movieRepository.findById(movieId).orElseThrow(ResourceNotFoundException::new);
        var creator = userRepository.findById(creatorId).orElseThrow(ResourceNotFoundException::new);
        var fetchedAttendees = new ArrayList<User>();

        for (String attendeeId : attendees) {
            var attendee = userRepository.findById(attendeeId).orElseThrow(ResourceNotFoundException::new);
            if (fetchedAttendees.contains(attendee)) throw new BadRequestException();
            else fetchedAttendees.add(attendee);
        }

        long playTime;

        try {
            playTime = Long.parseLong(movie.getRuntime().toLowerCase().replace("min", "").trim());
        } catch (Exception e) {
            // If movie do not include information about runtime assume that movie is 2 hours long
            playTime = 2 * 60L;
        }

        DateTime start = new DateTime(startTime);
        DateTime end = new DateTime(startTime + playTime * 60 * 1000);

        var credentials = new ArrayList<Credential>();

        var creatorCredential = getCredentials(creator.getRefreshToken());
        if (!getAllEventsBetween(start, end, creatorCredential).isEmpty()) throw new BadRequestException();
        else credentials.add(creatorCredential);

        for (User attendee : fetchedAttendees) {
            var attendeeCredential = getCredentials(attendee.getRefreshToken());

            if (!getAllEventsBetween(start, end, attendeeCredential).isEmpty()) throw new BadRequestException();
            else credentials.add(attendeeCredential);
        }

        var event = new Event()
                .setSummary(movie.getTitle())
                .setDescription("Movie watching of " + movie.getTitle())
                .setStart(new EventDateTime().setDateTime(start))
                .setEnd(new EventDateTime().setDateTime(end))
                .setEtag("movie_nights");

        var eventAttendees = new ArrayList<EventAttendee>();
        for (User attendee : fetchedAttendees) {
            eventAttendees.add(new EventAttendee().setEmail(attendee.getEmail()));
        }

        event.setAttendees(eventAttendees);

        for (Credential credential : credentials) {
            var calendar = new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                    .setApplicationName("Test")
                    .build();
            try {
                calendar.events().insert("primary", event);
            } catch (IOException e) {
                e.printStackTrace();
                throw new InternalServerErrorException();
            }
        }

        return MovieWatchingCommand.builder()
                .startTime(startTime)
                .endTime(startTime + playTime * 60 * 1000)
                .attendees(fetchedAttendees.stream().map(User::getId).collect(Collectors.toList()))
                .movieId(movieId)
                .build();
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
