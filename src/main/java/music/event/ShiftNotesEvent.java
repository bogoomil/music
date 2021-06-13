package music.event;

public class ShiftNotesEvent {
    int measureNum, by;

    public ShiftNotesEvent(int measureNum, int by) {
        super();
        this.measureNum = measureNum;
        this.by = by;
    }

    public int getMeasureNum() {
        return measureNum;
    }

    public void setMeasureNum(int measureNum) {
        this.measureNum = measureNum;
    }

    public int getBy() {
        return by;
    }

    public void setBy(int by) {
        this.by = by;
    }



}
