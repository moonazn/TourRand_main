package com.tourbus.tourrand;

public class MapData {
    private int colorcode;
    private String visited;
    private String visitCnt;

    public MapData(int colorcode, String visited, String visitCnt) {
        this.colorcode = colorcode;
        this.visited = visited;
        this.visitCnt = visitCnt;
    }

    public int getColorcode() {
        return colorcode;
    }

    public String getVisited() {

        return visited;
    }

    public String getVisitCnt() {
        return visitCnt;
    }
}

