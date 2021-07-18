package music.event;

public class VolumeChangedEvent {
    int value;

    public VolumeChangedEvent(int value) {
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
