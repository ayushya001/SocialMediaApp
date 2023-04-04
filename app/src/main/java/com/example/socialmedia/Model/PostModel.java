package com.example.socialmedia.Model;

public class PostModel {
    String postid;
    String postImage;
    String postedBy;
    String postDescription;
    int postlikes;
    long postedAt;

    public PostModel(String postid, String postImage, String postedBy, String postDescription, long postedAt) {
        this.postid = postid;
        this.postImage = postImage;
        this.postedBy = postedBy;
        this.postDescription = postDescription;
        this.postedAt = postedAt;
    }

    public PostModel() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public long getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(long postedAt) {
        this.postedAt = postedAt;
    }

    public int getPostlikes() {
        return postlikes;
    }

    public void setPostlikes(int postlikes) {
        this.postlikes = postlikes;
    }
}
