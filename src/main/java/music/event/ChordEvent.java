package music.event;

import music.theory.Chord;
import music.theory.ChordDegree;

public class ChordEvent {
    private Chord chord;
    private ChordDegree degree;
    public Chord getChord() {
        return chord;
    }
    public void setChord(Chord chord) {
        this.chord = chord;
    }
    public ChordEvent(Chord chord, ChordDegree degree) {
        super();
        this.chord = chord;
        this.degree = degree;
    }
    public ChordDegree getDegree() {
        return degree;
    }
    public void setDegree(ChordDegree degree) {
        this.degree = degree;
    }



}
