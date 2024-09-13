package com.tourbus.tourrand;

public class Friend {
    private String nickname;
    private String uuid;
    private String userProfileImg;

    public Friend(String nickname, String uuid, String userProfileImg) {
        this.nickname = nickname;
        this.uuid = uuid;
        this.userProfileImg = userProfileImg;
    }

    public String getNickname() {
        return nickname;
    }
    public String getUserProfileImg(){ return userProfileImg;}

    public String getUuid() {
        return uuid;
    }
}
