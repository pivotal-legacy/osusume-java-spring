package com.tokyo.beach.application.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class LikeController {
    private LikeRepository likeRepository;

    @Autowired
    public LikeController(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @RequestMapping(value = "/restaurants/{restaurantId}/likes", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@PathVariable long restaurantId) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        likeRepository.create(restaurantId, userId.longValue());
    }

}
