package com.tourbus.tourrand;

import java.util.UUID;

public class UserManager {

    private static UserManager instance;
    private String userNickname;
    private String userUUID;
    private String userProfileImg;

    private UserManager() {}



    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
    public String getUserNickname() {
        return userNickname;
    }
    public void setUserId(String userUUID) {
        this.userUUID = userUUID;
    }
    public String getUserId() {
        return userUUID;
    }
    public void setUserProfileImg(String userProfileImg){this.userProfileImg = userProfileImg;}
    public String getUserProfileImg(){return userProfileImg;}
}
