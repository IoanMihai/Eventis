package com.ioanmihai.eventis;

public class User {
    private String id;
    private String fullname;
    private boolean games, music, nature, other, political;

    public User(String id, String fullname, boolean games, boolean music, boolean nature, boolean other, boolean political) {
        this.id = id;
        this.fullname = fullname;
        this.games = games;
        this.music = music;
        this.nature = nature;
        this.other = other;
        this.political = political;
    }

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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
}
