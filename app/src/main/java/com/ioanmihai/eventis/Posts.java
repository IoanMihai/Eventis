package com.ioanmihai.eventis;

public class Posts {
    public String date, description, eventDate, eventTime, fullname, postimage, time, title, uid;
    public boolean games, music, nature, other, political;

    public Posts() { }

    public Posts(String date, String description, String eventDate, String eventTime, String fullname, String postimage, String time, String title, String uid, boolean games, boolean music, boolean nature, boolean other, boolean political) {
        this.date = date;
        this.description = description;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.fullname = fullname;
        this.postimage = postimage;
        this.time = time;
        this.title = title;
        this.uid = uid;
        this.games = games;
        this.music = music;
        this.nature = nature;
        this.other = other;
        this.political = political;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isGames() {
        return games;
    }

    public void setGames(boolean games) {
        this.games = games;
    }

    public boolean isMusic() {
        return music;
    }

    public void setMusic(boolean music) {
        this.music = music;
    }

    public boolean isNature() {
        return nature;
    }

    public void setNature(boolean nature) {
        this.nature = nature;
    }

    public boolean isOther() {
        return other;
    }

    public void setOther(boolean other) {
        this.other = other;
    }

    public boolean isPolitical() {
        return political;
    }

    public void setPolitical(boolean political) {
        this.political = political;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", fullname='" + fullname + '\'' +
                ", postimage='" + postimage + '\'' +
                ", time='" + time + '\'' +
                ", title='" + title + '\'' +
                ", uid='" + uid + '\'' +
                ", games=" + games +
                ", music=" + music +
                ", nature=" + nature +
                ", other=" + other +
                ", political=" + political +
                '}';
    }
}
