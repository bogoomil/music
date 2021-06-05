package music.event.tracks;

import music.theory.Pitch;

public class KeyBoardSelectButtonEvent{

    private Pitch pitch;

    public KeyBoardSelectButtonEvent(Pitch pitch) {
        super();
        this.pitch = pitch;
    }

    public Pitch getPitch() {
        return pitch;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }




}
