package music.event;

import music.theory.Measure;

public class AddMeasureToTrackEvent {

    Measure measure;

    public AddMeasureToTrackEvent(Measure measure) {
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
