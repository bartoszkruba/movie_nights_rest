package com.example.movie_nights_rest.service;

import com.example.movie_nights_rest.command.movieWatching.MovieWatchingCommand;
import com.example.movie_nights_rest.exception.BadRequestException;
import com.example.movie_nights_rest.exception.InternalServerErrorException;
import com.example.movie_nights_rest.exception.ResourceNotFoundException;
import com.example.movie_nights_rest.model.User;
import com.example.movie_nights_rest.repository.MovieRepository;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;

    private final UserRepository userRepository;
    private final EmailAsyncService emailAsyncService;
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

    public MovieWatchingCommand createMovieWatching(String creatorId, String[] attendees, Long startTime, String movieId) {

        var movie = movieRepository.findById(movieId).orElseThrow(ResourceNotFoundException::new);
        var creator = userRepository.findById(creatorId).orElseThrow(ResourceNotFoundException::new);
        var fetchedAttendees = new ArrayList<User>();

        for (String attendeeId : attendees) {
            var attendee = userRepository.findById(attendeeId).orElseThrow(ResourceNotFoundException::new);

            if (!creator.getFriends().contains(attendee)) throw new BadRequestException();

            if (fetchedAttendees.contains(attendee)) throw new BadRequestException();
            else if (!attendeeId.equals(creatorId)) fetchedAttendees.add(attendee);
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
                calendar.events().insert("primary", event).execute();
            } catch (IOException e) {
                e.printStackTrace();
                throw new InternalServerErrorException();
            }
        }

        for (User attendee : fetchedAttendees) {
            emailAsyncService.sendMovieWatchingInfo(attendee.getEmail(), movieId);
        }

        return MovieWatchingCommand.builder()
                .startTime(startTime)
                .endTime(startTime + playTime * 60 * 1000)
                .attendees(fetchedAttendees.stream().map(User::getId).collect(Collectors.toList()))
                .movieId(movieId)
                .build();
    }

    public List<MovieWatchingCommand> getMovieWatchings(String refreshToken) {
        var credentials = getCredentials(refreshToken);

        var calendar = new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credentials)
                .setApplicationName("Test")
                .build();

        try {
            return calendar.events().list("primary")
                    .setTimeMin(new DateTime(System.currentTimeMillis()))
                    .setSingleEvents(true)
                    .setQ("etag=movie_nights")
                    .execute().getItems().stream()
                    .map(event -> {
                        var movie = movieRepository.findByTitle(event.getSummary())
                                .orElseThrow(InternalServerErrorException::new);
                        return MovieWatchingCommand.builder()
                                .movieId(movie.getImdbID())
                                .attendees(event.getAttendees().stream().map(eventAttendee -> {
                                    var user = userRepository.findByEmail(eventAttendee.getEmail())
                                            .orElseThrow(InternalServerErrorException::new);
                                    return user.getId();
                                }).collect(Collectors.toList()))
                                .startTime(event.getStart().getDateTime().getValue())
                                .endTime(event.getEnd().getDateTime().getValue()).build();
                    }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new InternalServerErrorException();
        }
    }

    public List<Long> getPossibleWatchingTimes(String[] attendees, Integer starTime, String movieId, int numberOfTimes) {
        var movie = movieRepository.findById(movieId).orElseThrow(ResourceNotFoundException::new);

        var fetchedAttendees = new ArrayList<User>();

        for (String userId : attendees) {
            fetchedAttendees.add(userRepository.findById(userId).orElseThrow(ResourceNotFoundException::new));
        }

        int moviePlayTime;

        try {
            moviePlayTime = Integer.parseInt(movie.getRuntime().toLowerCase().replace("min", "").trim());
        } catch (Exception e) {
            // if move do not include runtime info set movie play time to 2 hours
            moviePlayTime = 60 * 2;
        }

        int startHour = starTime / 60;
        int startMinute = starTime % 60;

        int endTime = starTime + moviePlayTime;
        if (endTime > 24 * 60) endTime -= 24 * 60;

        int endHour = endTime / 60;
        int endMinute = endTime % 60;

        var possibleStartTime = LocalDateTime.now().withHour(startHour).withMinute(startMinute);
        var possibleEndTime = LocalDateTime.now().withHour(endHour).withMinute(endMinute);

        if (possibleEndTime.isBefore(possibleStartTime)) possibleEndTime = possibleEndTime.plus(1, ChronoUnit.DAYS);

        if (possibleStartTime.isBefore(LocalDateTime.now())) {
            possibleStartTime = possibleStartTime.plus(1, ChronoUnit.DAYS);
            possibleEndTime = possibleEndTime.plus(1, ChronoUnit.DAYS);
        }

        var possiblePlayTimes = new ArrayList<Long>();

        do {
            int freeAttendees = 0;

            for (User user : fetchedAttendees) {
                try {
                    var credentials = getCredentials(user.getRefreshToken());
                    var calendar = new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credentials)
                            .setApplicationName("Test")
                            .build();

                    if (!calendar.events().list("primary")
                            .setTimeMin(new DateTime(possibleStartTime.toEpochSecond(OffsetDateTime.now().getOffset())))
                            .setTimeMax(new DateTime(possibleEndTime.toEpochSecond(OffsetDateTime.now().getOffset())))
                            .setSingleEvents(true).execute().getItems().isEmpty()) {
                        break;
                    } else freeAttendees++;
                } catch (Exception e) {
                    throw new InternalServerErrorException();
                }
            }
            if (freeAttendees == attendees.length)
                possiblePlayTimes.add(possibleStartTime.toEpochSecond(OffsetDateTime.now().getOffset()));

        } while (possiblePlayTimes.size() < numberOfTimes);

        return possiblePlayTimes;
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
