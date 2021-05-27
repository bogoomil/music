package music.event;

import music.theory.Pitch;

public class NoteOnEvent {

    private Pitch pitch;
    private int tick;

    public NoteOnEvent(Pitch pitch, int tick) {
        super();
        this.pitch = pitch;
        this.tick = tick;
    }

    public Pitch getPitch() {
        return pitch;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }



}
