package com.tokyo.beach.application.session;

import com.tokyo.beach.application.RestControllerException;
import com.tokyo.beach.application.user.LogonCredentials;
import com.tokyo.beach.application.user.DatabaseUser;
import com.tokyo.beach.application.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class SessionController {
    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private TokenGenerator tokenGenerator;

    @Autowired
    public SessionController(
            SessionRepository sessionRepository,
            UserRepository userRepository,
            TokenGenerator tokenGenerator
    ) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public UserSession create(@RequestBody LogonCredentials credentials) {
        Optional<DatabaseUser> user = userRepository.get(credentials);

        if (user.isPresent()) {
            return sessionRepository.create(tokenGenerator, user.get());
        }

        throw new RestControllerException("Invalid email or password.");
    }

    @RequestMapping(value = "/unauthenticated")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String authError() {
        return "Authentication Error";
    }
}
