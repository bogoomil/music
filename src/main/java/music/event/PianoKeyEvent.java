package music.event;

import music.theory.Pitch;

public class PianoKeyEvent {

    Pitch pitch;

    public PianoKeyEvent(Pitch pitch) {
        this.pitch = pitch;
    }

    public Pitch getPitch() {
        return pitch;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }



}
