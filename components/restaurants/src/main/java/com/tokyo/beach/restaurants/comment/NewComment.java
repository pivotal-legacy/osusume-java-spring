package com.tokyo.beach.restaurants.comment;

public class NewComment {
    private String comment;

    @SuppressWarnings("unused")
    public NewComment() {};

    @SuppressWarnings("unused")
    public NewComment(String comment) {
        this.comment = comment;
    }

    @SuppressWarnings("unused")
    public String getComment() {
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewComment that = (NewComment) o;

        return comment != null ? comment.equals(that.comment) : that.comment == null;

    }

    @Override
    public int hashCode() {
        return comment != null ? comment.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "NewComment{" +
                "comment='" + comment + '\'' +
                '}';
    }
}
