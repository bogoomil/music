package music.event.tracks;

import music.model.Track;

public class TrackCreatedEvent {
    private Track track;

    public TrackCreatedEvent(Track track) {
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
