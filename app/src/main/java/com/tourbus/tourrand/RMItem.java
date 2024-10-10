package com.tourbus.tourrand;

public class RMItem {
    private String randUserNickname;
    private String randCount;
    public RMItem(String randUserNickname, String randCount){

        this.randUserNickname = randUserNickname;
        this.randCount = randCount;
    }
    public void setRandUserNickname(String randUserNickname) {
        this.randUserNickname = randUserNickname;
    }
    public void setRandCount(String randCount) {
        this.randCount = randCount;
    }
    public String getRandCount() {
        return randCount;
    }
    public String getRandUserNickname() {
        return randUserNickname;
    }
}
