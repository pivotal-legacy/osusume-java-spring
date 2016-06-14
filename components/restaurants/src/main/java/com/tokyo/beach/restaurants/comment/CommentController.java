package com.tokyo.beach.restaurants.comment;

import com.tokyo.beach.restaurants.user.User;
import com.tokyo.beach.restaurants.user.UserDataMapper;
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
    private CommentDataMapper commentDataMapper;
    private UserDataMapper userDataMapper;

    @Autowired
    public CommentController(CommentDataMapper commentDataMapper, UserDataMapper userDataMapper) {
        this.commentDataMapper = commentDataMapper;
        this.userDataMapper = userDataMapper;
    }

    @RequestMapping(value = "restaurants/{restaurantId}/comments", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public SerializedComment create(@RequestBody NewCommentWrapper newCommentWrapper, @PathVariable String restaurantId) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");
        Comment persistedComment = commentDataMapper.create(
                newCommentWrapper.getNewComment(),
                userId.longValue(),
                restaurantId
        );
        User currentUser = userDataMapper.get(userId.longValue()).get();
        return new SerializedComment(persistedComment, currentUser);
    }

    @RequestMapping(value = "comments/{commentId}", method = DELETE)
    @ResponseStatus(OK)
    public void delete(@PathVariable String commentId) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = sra.getRequest();
        Number userId = (Number) request.getAttribute("userId");

        Optional<Comment> maybeCommentToDelete = commentDataMapper.get(Long.parseLong(commentId));

        if (maybeCommentToDelete.isPresent() &&
                userId.longValue() == maybeCommentToDelete.get().getCreatedByUserId()) {
            commentDataMapper.delete(maybeCommentToDelete.get().getId());
        }
    }

    @RequestMapping(value = "restaurants/{restaurantId}/comments", method = GET)
    @ResponseStatus(OK)
    public List<SerializedComment> get(@PathVariable String restaurantId) {
        return commentDataMapper.findForRestaurant(Long.parseLong(restaurantId));
    }

}
