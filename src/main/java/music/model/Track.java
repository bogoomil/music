package music.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import music.theory.Note;
import music.theory.Pitch;
import music.theory.Tone;

public class Track {

    private int id;
    private String name;
    private int instrument;
    private int channel;

    private Pitch root;
    private Tone hangnem;

    private List<Note> notes = new ArrayList<>();

    //    private int measureNum;

    private int volume = 100;

    //TODO törölni
    public Track(int id) {
        this();
        this.id = id;
    }

    public Track() {
        super();
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public Pitch getRoot() {
        return root;
    }

    public void setRoot(Pitch root) {
        this.root = root;
    }

    public Tone getHangnem() {
        return hangnem;
    }

    public void setHangnem(Tone hangnem) {
        this.hangnem = hangnem;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }


    @JsonIgnore
    public int getMeasureNum() {
        int calculatedNum = 0;
        if(this.notes.size() != 0) {
            Note n = this.notes.stream().max(Comparator.comparing(Note::getStartTick)).get();
            calculatedNum = (n.getStartTick() + n.getLength().getErtek()) / 32 + 1;
        }
        return calculatedNum + 1;
    }

    @JsonIgnore
    public int getMinOctave() {
        if(this.notes != null && this.notes.size() > 0) {
            Note n = this.notes.stream().min(Comparator.comparing(Note::getMidiCode)).get();
            return n.getPitch().getOctave();
        }
        return 3;
    }

    //    public void setMeasureNum(int measureNum) {
    //        this.measureNum = measureNum;
    //    }

    public void removePitches(Pitch p) {
        for(int i = 0; i < this.notes.size(); i++) {
            if(notes.get(i).getPitch().getMidiCode() == p.getMidiCode()) {
                notes.remove(i);
            }
        }
    }

    public void setVolume(int value) {
        this.volume = value;
        this.notes.stream().forEach(n -> n.setVol(value));
    }

    public int getVolume() {
        return volume;
    }

    @Override
    public Track clone() {
        Track t = new Track();
        //        t.setMeasureNum(this.getMeasureNum());
        List<Note> nots = new ArrayList<>() ;
        this.notes.stream().forEach(n -> {
            nots.add(n.clone());
        });

        t.setNotes(nots);
        return t;
    }

    public void shiftNotesFromMeasureBy(int measureNum, int by) {
        int firstTickToShift = measureNum * 32;
        notes.forEach(n -> {
            if(n.getStartTick() >= firstTickToShift) {
                n.setStartTick(n.getStartTick() + 32 * by);
            }
        });
    }

    public void duplicateMeasure(int measureNum) {

        System.out.println("duplicating measure: " + notes.size());

        List<Note> origNotes = this.getNotesOfMeasure(measureNum);

        this.shiftNotesFromMeasureBy(measureNum + 1, 1);

        this.getNotesOfMeasure(measureNum).forEach(n -> {
            Note duplicate = n.clone();
            duplicate.setStartTick(n.getStartTick() + 32);
            this.notes.add(duplicate);
        });
        System.out.println("duplicating measure: " + notes.size());
    }

    /**
     * visszaadja egy measure note-jainak a klónját
     * @param measureNum
     * @return
     */
    public List<Note> getNotesOfMeasure(int measureNum){
        return notes.stream().filter(n -> n.getStartTick() >= measureNum * 32 && n.getStartTick() < (measureNum + 1) * 32).map(n -> n.clone()).collect(Collectors.toList());
    }

    public void deleteMeasure(int measureNum) {
        notes.removeAll(notes.stream().filter(n -> n.getStartTick() >= measureNum * 32 && n.getStartTick() < (measureNum + 1) * 32).collect(Collectors.toList()));
        this.shiftNotesFromMeasureBy(measureNum + 1, -1);
    }

}
