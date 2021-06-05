package music.event.tracks;

import music.model.Track;

public class TrackSelectedEvent {

    private Track track;

    public TrackSelectedEvent(Track track) {
        super();
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }



}
