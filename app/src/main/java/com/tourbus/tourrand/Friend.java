package com.tourbus.tourrand;

public class Friend {
    private String nickname;
    private String uuid;
    private String userProfileImg;
    public Friend (){

    }

    public Friend(String nickname, String uuid, String userProfileImg) {
        this.nickname = nickname;
        this.uuid = uuid;
        this.userProfileImg = userProfileImg;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfileImage(String userProfileImg) {
        this.userProfileImg = userProfileImg;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNickname() {
        return nickname;
    }
    public String getUserProfileImg(){ return userProfileImg;}

    public String getUuid() {
        return uuid;
    }
}
