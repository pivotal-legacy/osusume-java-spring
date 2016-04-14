package com.tokyo.beach.restaurants.comment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewCommentWrapper {
    private NewComment newComment;

    @SuppressWarnings("unused")
    public NewCommentWrapper() {};

    @SuppressWarnings("unused")
    public NewCommentWrapper(NewComment newComment) {
        this.newComment = newComment;
    }

    @JsonProperty("comment")
    public NewComment getNewComment() {
        return newComment;
    }
}
