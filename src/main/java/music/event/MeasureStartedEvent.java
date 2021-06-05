package music.event;

public class MeasureStartedEvent {
    int measureNum;

    public MeasureStartedEvent(int measureNum) {
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
