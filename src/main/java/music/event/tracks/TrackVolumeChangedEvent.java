package music.event.tracks;

public class TrackVolumeChangedEvent {
    int value;

    public TrackVolumeChangedEvent(int value) {
        super();
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }


}
