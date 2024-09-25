package com.tourbus.tourrand;

import java.util.UUID;

public class Friend {
    private String nickname;
    private String id;
    private String userProfileImg;
    private String uuid;
    public Friend (){

    }

    public Friend(String nickname, String id, String userProfileImg, String uuid) {
        this.nickname = nickname;
        this.id = id;
        this.userProfileImg = userProfileImg;
        this.uuid = uuid;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfileImage(String userProfileImg) {
        this.userProfileImg = userProfileImg;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setUuid(String uuid){this.uuid = uuid;}

    public String getNickname() {
        return nickname;
    }
    public String getUserProfileImg(){ return userProfileImg;}
    public String getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }
}
