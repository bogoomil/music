package music.event;

import music.theory.Measure;

public class MeasureSelectedEvent {
    private Measure measure;

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public MeasureSelectedEvent(Measure measure) {
        super();
        this.measure = measure;
    }



}
