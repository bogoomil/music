package music.event;

import music.theory.Measure;

public class PlayMeasureEvent {
    Measure measure;

    public PlayMeasureEvent(Measure measure) {
        super();
        this.measure = measure;
    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }


}
