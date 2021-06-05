package music.service;

import java.util.ArrayList;
import java.util.List;

import music.interfaces.NoteProducer;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.Pitch;

public class RythmProducer implements NoteProducer {

    private List<Note> notes = new ArrayList<>();
    private NoteLength[] noteLengths;

    private NoteLength[] beats;

    @Override
    public List<Note> getNotes(int midiCode) {
        for(int i = 0; i < beats.length; i++) {
            NoteLength beat = beats[i];
            Note n = new Note();
            n.setLength(noteLengths[i]);
            n.setStartTick(beat.getErtek() * (i * 32));
            n.setPitch(new Pitch(midiCode));
        }
        return notes;
    }

    public NoteLength[] getNoteLengths() {
        return noteLengths;
    }

    public void setNoteLengths(NoteLength[] noteLengths) {
        this.noteLengths = noteLengths;
    }

    public NoteLength[] getBeats() {
        return beats;
    }

    public void setBeats(NoteLength[] beats) {
        this.beats = beats;
    }

    @Override
    public List<Note> getNotes() {
        return notes;
    }



}
