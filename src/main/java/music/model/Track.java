package music.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    private int measureNum;

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

    public int getMeasureNum() {
        int calculatedNum = 0;
        if(this.notes.size() != 0) {
            Note n = this.notes.stream().max(Comparator.comparing(Note::getStartTick)).get();
            calculatedNum = n.getStartTick() / 32 + 1;
        }
        return calculatedNum > this.measureNum ? calculatedNum : this.measureNum;
    }

    @JsonIgnore
    public int getMinOctave() {
        if(this.notes != null && this.notes.size() > 0) {
            Note n = this.notes.stream().min(Comparator.comparing(Note::getMidiCode)).get();
            return n.getPitch().getOctave();
        }
        return 3;
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
        t.setMeasureNum(this.getMeasureNum());
        List<Note> nots = new ArrayList<>() ;
        //        for(int i = 0; i < this.notes.size(); i++) {
        //            Note newNote = new Note();
        //            newNote.setLength(notes.get(i).getLength());
        //            newNote.setPitch(notes.get(i).getPitch());
        //            newNote.setStartTick(notes.get(i).getStartTick());
        //            newNote.setVol(notes.get(i).getVol());
        //            System.out.println("Cloning: n.id: " + notes.get(i) + " new: " + newNote);
        //            nots.add(newNote);
        //        }
        //
        this.notes.stream().forEach(n -> {
            nots.add(n.clone());
        });

        t.setNotes(nots);
        return t;
    }

}
