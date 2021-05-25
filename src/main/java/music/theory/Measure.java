package music.theory;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.App;

public class Measure {
    private int tempo;
    private static final Logger LOG = LoggerFactory.getLogger(Measure.class);
    private List<Note> notes = new ArrayList<>();
    int num;



    public Measure(int num, int tempo) {
        super();
        this.tempo = tempo;
        this.num = num;
    }

    //    public int getTickLengthInMs(int tickNum) {
    //        int msInTick = 60000 / this.tempo;
    //        return msInTick * tickNum;
    //
    //    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void addNote(Note note) {
        this.notes.add(note);
    }
    public static Measure createMeasureFromChord(int measureNum, Chord ch, NoteLength chordLength, NoteLength arpeggioOffset) {
        Measure measure = new Measure(measureNum, App.getTEMPO());
        int counter = 0;
        for(Pitch p : ch.getPitches()) {
            Note note = new Note();
            note.setPitch(p);
            note.setVol(100);
            note.setLength(chordLength);
            int start = 0;
            if(arpeggioOffset != null) {
                start = counter * (32 / arpeggioOffset.getErtek());
                LOG.debug("counter: {} * (32/{}) = {}", counter, arpeggioOffset.getErtek(), start);

            }
            note.setStartInTick(start);
            measure.addNote(note);
            counter++;
        }
        return measure;
    }

}
