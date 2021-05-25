package music.gui;

import javax.swing.JToggleButton;

import music.theory.Pitch;

public class PitchToggleButton extends JToggleButton {

    int tick;
    Pitch pitch;
    public PitchToggleButton(int tick, Pitch pitch) {
        super();
        this.tick = tick;
        this.pitch = pitch;
    }
    public int getTick() {
        return tick;
    }
    public void setTick(int tick) {
        this.tick = tick;
    }
    public Pitch getPitch() {
        return pitch;
    }
    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

}
