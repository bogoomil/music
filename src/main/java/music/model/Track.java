package music.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import music.theory.Measure;

public class Track {

    private int id;
    private String name;
    private int instrument;
    private int channel;

    public Track(int id) {
        super();
        this.id = id;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addMeasure(Measure m) {
        m.setNum(measures.size());
        this.measures.add(m);

    }

    public Optional<Measure> getMeasureByNum(int num) {
        return this.measures.stream().filter(m -> m.getNum() == num).findAny();
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void removeMeasure(int measureNum) {
        this.measures.remove(measureNum);
    }

}
