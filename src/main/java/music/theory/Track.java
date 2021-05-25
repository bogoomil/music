package music.theory;

import java.util.ArrayList;
import java.util.List;

public class Track {
    private String name;
    private int instrument;

    public Track() {
        super();
    }

    private List<Measure> measures = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getInstrument() {
        return instrument;
    }

    public void setInstrument(int instrument) {
        this.instrument = instrument;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    public void setMeasures(List<Measure> measures) {
        this.measures = measures;
    }




}
