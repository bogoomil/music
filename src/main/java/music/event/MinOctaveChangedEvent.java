package music.event;

public class MinOctaveChangedEvent {
    int minOctave;

    public int getMinOctave() {
        return minOctave;
    }

    public void setMinOctave(int minOctave) {
        this.minOctave = minOctave;
    }

    public MinOctaveChangedEvent(int minOctave) {
        super();
        this.minOctave = minOctave;
    }



}
