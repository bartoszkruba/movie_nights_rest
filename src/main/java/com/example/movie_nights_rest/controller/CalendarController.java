package com.example.movie_nights_rest.controller;

import com.example.movie_nights_rest.annotation.CurrentUser;
import com.example.movie_nights_rest.config.security.UserPrincipal;
import com.example.movie_nights_rest.model.Role;
import com.example.movie_nights_rest.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@CrossOrigin
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/test")
    @Secured({Role.ADMIN, Role.BASIC})
    public void test(@CurrentUser UserPrincipal userPrincipal) {
        calendarService.test(userPrincipal.getRefreshToken());
    }

}
