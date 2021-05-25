package music.model;

import java.util.HashMap;
import java.util.Map;

import music.theory.Track;

public class Project {
    private String name;
    Map<String, Track> tracks = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Track> getTracks() {
        return tracks;
    }

    public void setTracks(Map<String, Track> tracks) {
        this.tracks = tracks;
    }

}
