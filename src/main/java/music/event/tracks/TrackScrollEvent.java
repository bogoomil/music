package music.event.tracks;

public class TrackScrollEvent {
    int measureNum;

    public TrackScrollEvent(int measureNum) {
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
