package music.event;

import music.theory.Pitch;

public class KeyBoardClearButtonEvent{

    private Pitch pitch;

    public KeyBoardClearButtonEvent(Pitch pitch) {
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
