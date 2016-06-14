package com.tokyo.beach.restaurants.session;

import com.tokyo.beach.restutils.RestControllerException;
import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
public class SessionController {
    private SessionDataMapper sessionDataMapper;
    private UserDataMapper userDataMapper;
    private TokenGenerator tokenGenerator;

    @Autowired
    public SessionController(
            SessionDataMapper sessionDataMapper,
            UserDataMapper userDataMapper,
            TokenGenerator tokenGenerator
    ) {
        this.sessionDataMapper = sessionDataMapper;
        this.userDataMapper = userDataMapper;
        this.tokenGenerator = tokenGenerator;
    }

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public UserSession create(@RequestBody LogonCredentials credentials) {
        Optional<User> maybeUser = userDataMapper.get(credentials);

        maybeUser.orElseThrow(() -> new RestControllerException("Invalid email or password."));

        return sessionDataMapper.create(tokenGenerator, maybeUser.get());
    }

    @RequestMapping(value = "/session", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public void delete(@RequestBody WrappedToken wrappedToken) {
        sessionDataMapper.delete(wrappedToken.getToken());
    }

    @RequestMapping(value = "/unauthenticated")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String authError() {
        return "Authentication Error";
    }
}
