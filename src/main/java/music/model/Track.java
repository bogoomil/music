package music.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    private int measureNum;

    //TODO törölni
    public Track(int id) {
        super();
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

    public int getMeasureNum() {
        if(this.measureNum == 0 && this.notes.size() != 0) {
            Note n = this.notes.stream().max(Comparator.comparing(Note::getStartTick)).get();
            this.measureNum = n.getStartTick() / 32 + 1;
        }
        return this.measureNum;
    }

    public int getMinOctave() {
        Note n = this.notes.stream().min(Comparator.comparing(Note::getMidiCode)).get();
        return n.getPitch().getOctave();
    }

    public void setMeasureNum(int measureNum) {
        this.measureNum = measureNum;
    }

    public void removePitches(Pitch p) {
        for(int i = 0; i < this.notes.size(); i++) {
            if(notes.get(i).getPitch().getMidiCode() == p.getMidiCode()) {
                notes.remove(i);
            }
        }
    }
}
