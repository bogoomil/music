package music.event;

public class TrackSelectedEvent {
    int trackId;

    public TrackSelectedEvent(int trackId) {
        super();
        this.trackId = trackId;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }



}
