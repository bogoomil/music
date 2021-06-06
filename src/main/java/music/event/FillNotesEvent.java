package music.event;

import music.theory.NoteLength;
import music.theory.Pitch;

public class FillNotesEvent {
    private Pitch pitch;
    private NoteLength length;
    private NoteLength beat;
    private int measureNum;
    private int fromMeasure;
    public FillNotesEvent(Pitch pitch, NoteLength length, NoteLength beat, int measureNum, int fromMeasure) {
        super();
        this.pitch = pitch;
        this.length = length;
        this.beat = beat;
        this.measureNum = measureNum;
        this.fromMeasure = fromMeasure;
    }
    public Pitch getPitch() {
        return pitch;
    }
    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }
    public NoteLength getLength() {
        return length;
    }
    public void setLength(NoteLength length) {
        this.length = length;
    }
    public NoteLength getBeat() {
        return beat;
    }
    public void setBeat(NoteLength beat) {
        this.beat = beat;
    }
    public int getMeasureNum() {
        return measureNum;
    }
    public void setMeasureNum(int measureNum) {
        this.measureNum = measureNum;
    }
    public int getFromMeasure() {
        return fromMeasure;
    }
    public void setFromMeasure(int fromMeasure) {
        this.fromMeasure = fromMeasure;
    }
}
