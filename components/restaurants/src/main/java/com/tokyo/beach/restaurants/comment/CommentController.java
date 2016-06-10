package com.tokyo.beach.restaurants.comment;

import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@CrossOrigin
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
        User currentUser = userRepository.get(userId.longValue()).get();
        return new SerializedComment(persistedComment, currentUser);
    }

    @RequestMapping(value = "comments/{commentId}", method = DELETE)
    @ResponseStatus(OK)
    public void delete(@PathVariable String commentId) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        Optional<Comment> maybeCommentToDelete = commentRepository.get(Long.parseLong(commentId));

        if (maybeCommentToDelete.isPresent() &&
                userId.longValue() == maybeCommentToDelete.get().getCreatedByUserId()) {
            commentRepository.delete(maybeCommentToDelete.get().getId());
        }
    }

    @RequestMapping(value = "restaurants/{restaurantId}/comments", method = GET)
    @ResponseStatus(OK)
    public List<SerializedComment> get(@PathVariable String restaurantId) {
        return commentRepository.findForRestaurant(Long.parseLong(restaurantId));
    }

}
