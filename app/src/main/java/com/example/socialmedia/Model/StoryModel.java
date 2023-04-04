package com.example.socialmedia.Model;

public class StoryModel {
    String suserid, imageurl,storyid;
    long timestart,timeend;

    public StoryModel() {
    }

    public StoryModel(String suserid, String imageurl, String storyid, long timestart, long timeend) {
        this.suserid = suserid;
        this.imageurl = imageurl;
        this.storyid = storyid;
        this.timestart = timestart;
        this.timeend = timeend;
    }

    public String getSuserid() {
        return suserid;
    }

    public void setSuserid(String suserid) {
        this.suserid = suserid;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeend() {
        return timeend;
    }

    public void setTimeend(long timeend) {
        this.timeend = timeend;
    }
}
