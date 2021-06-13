package music.event;

public class DuplicateMeasureEvent {
    int measureNum;

    public DuplicateMeasureEvent(int measureNum) {
        super();
        this.measureNum = measureNum;
    }

    public int getMeasureNum() {
        return measureNum;
    }

    public void setMeasureNum(int measureNum) {
        this.measureNum = measureNum;
    }



}
