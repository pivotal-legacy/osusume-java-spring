package com.tokyo.beach.restaurants.user;

import com.tokyo.beach.restutils.RestControllerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
public class UserController {
    private UserDataMapper userDataMapper;

    @Autowired
    public UserController(
            UserDataMapper userDataMapper
    ) {
        this.userDataMapper = userDataMapper;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public User profile() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Long userId = (Long) request.getAttribute("userId");

        Optional<User> maybeUser = userDataMapper.get(userId);

        maybeUser.orElseThrow(() -> new RestControllerException("Invalid user id."));
        return maybeUser.get();
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public User registerUser(@RequestBody NewUser newUser) {
        return userDataMapper.create(
                newUser.getEmail(),
                newUser.getPassword(),
                newUser.getName()
        );
    }
}
