package com.tokyo.beach.application.session;

import com.tokyo.beach.application.RestControllerException;
import com.tokyo.beach.application.logon.LogonCredentials;
import com.tokyo.beach.application.token.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class SessionController {

    private SessionRepository sessionRepository;
    private TokenGenerator tokenGenerator;

    @Autowired
    public SessionController(
            SessionRepository sessionRepository,
            TokenGenerator tokenGenerator
    ) {
        this.sessionRepository = sessionRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @RequestMapping(value = "/session", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public UserSession login(@RequestBody LogonCredentials credentials) {
        Optional<UserSession> userSessionOptional = sessionRepository.logon(tokenGenerator, credentials.getEmail(), credentials.getPassword());

        return userSessionOptional.orElseThrow(
                () -> new RestControllerException("Invalid email or password.")
        );
    }

}
