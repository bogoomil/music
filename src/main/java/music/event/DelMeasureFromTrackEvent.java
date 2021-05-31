package music.event;

import music.theory.Measure;

public class DelMeasureFromTrackEvent {
    Measure measure;
    int trackId;
    public DelMeasureFromTrackEvent(Measure measure, int trackId) {
        super();
        this.measure = measure;
        this.trackId = trackId;
    }
    public Measure getMeasure() {
        return measure;
    }
    public void setMeasure(Measure measure) {
        this.measure = measure;
    }
    public int getTrackId() {
        return trackId;
    }
    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }


}
