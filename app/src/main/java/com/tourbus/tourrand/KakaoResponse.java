package com.tourbus.tourrand;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class KakaoResponse {
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public static class Route {
        private Summary summary;

        public Summary getSummary() {
            return summary;
        }

        public void setSummary(Summary summary) {
            this.summary = summary;
        }
    }

    public static class Summary {
        private int duration;

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }
}
