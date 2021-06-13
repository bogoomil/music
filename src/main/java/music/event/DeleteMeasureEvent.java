package music.event;

public class DeleteMeasureEvent {
    int measureNum;

    public DeleteMeasureEvent(int measureNum) {
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
