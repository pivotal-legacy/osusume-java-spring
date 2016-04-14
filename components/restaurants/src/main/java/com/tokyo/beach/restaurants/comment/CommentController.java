package com.tokyo.beach.restaurants.comment;

import com.tokyo.beach.restaurants.user.DatabaseUser;
import com.tokyo.beach.restaurants.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class CommentController {
    private CommentRepository commentRepository;
    private UserRepository userRepository;

    @Autowired
    public CommentController(CommentRepository commentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "restaurants/{restaurantId}/comments", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public SerializedComment create(@RequestBody NewCommentWrapper newCommentWrapper, @PathVariable String restaurantId) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");
        Comment persistedComment = commentRepository.create(
                newCommentWrapper.getNewComment(),
                userId.longValue(),
                restaurantId
        );
        DatabaseUser currentUser = userRepository.get(userId.longValue()).get();
        return new SerializedComment(persistedComment, currentUser);
    }

}
