package music.event.tracks;

import music.theory.Pitch;

public class KeyBoardFillButtonEvent{

    private Pitch pitch;

    public KeyBoardFillButtonEvent(Pitch pitch) {
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
