package com.example.socialmedia.Model;

public class NotificationModel {



    String notificationType,notificationBy,postid,notificationid,postedby;
    long notificationAt;
    boolean isSeen;

    public NotificationModel(String notificationType, String notificationBy, String postid, String notificationid, long notificationAt, boolean isSeen) {
        this.notificationType = notificationType;
        this.notificationBy = notificationBy;
        this.postid = postid;
        this.notificationid = notificationid;
        this.notificationAt = notificationAt;
        this.isSeen = isSeen;
    }

    public NotificationModel() {
    }

    public NotificationModel(String postedby) {
        this.postedby = postedby;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationBy() {
        return notificationBy;
    }

    public void setNotificationBy(String notificationBy) {
        this.notificationBy = notificationBy;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getNotificationid() {
        return notificationid;
    }

    public void setNotificationid(String notificationid) {
        this.notificationid = notificationid;
    }

    public long getNotificationAt() {
        return notificationAt;
    }

    public void setNotificationAt(long notificationAt) {
        this.notificationAt = notificationAt;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getPostedby() {
        return postedby;
    }

    public void setPostedby(String postedby) {
        this.postedby = postedby;
    }
}
