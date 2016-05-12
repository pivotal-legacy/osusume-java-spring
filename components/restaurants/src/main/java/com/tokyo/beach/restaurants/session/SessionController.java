package com.tokyo.beach.restaurants.session;

import com.tokyo.beach.restutils.RestControllerException;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserRepository;
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
        Optional<User> maybeUser = userRepository.get(credentials);

        maybeUser.orElseThrow(() -> new RestControllerException("Invalid email or password."));

        return sessionRepository.create(tokenGenerator, maybeUser.get());
    }

    @RequestMapping(value = "/session", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public void delete(@RequestBody WrappedToken wrappedToken) {
        sessionRepository.delete(wrappedToken.getToken());
    }

    @RequestMapping(value = "/unauthenticated")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String authError() {
        return "Authentication Error";
    }
}
