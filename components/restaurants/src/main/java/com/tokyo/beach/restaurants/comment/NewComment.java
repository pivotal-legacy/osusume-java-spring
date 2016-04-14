package com.tokyo.beach.restaurants.comment;

public class NewComment {
    private String content;

    @SuppressWarnings("unused")
    public NewComment() {};

    @SuppressWarnings("unused")
    public NewComment(String content) {
        this.content = content;
    }

    @SuppressWarnings("unused")
    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewComment that = (NewComment) o;

        return content != null ? content.equals(that.content) : that.content == null;

    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "NewComment{" +
                "content='" + content + '\'' +
                '}';
    }
}
