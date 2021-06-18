package music.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import music.logic.MidiEngine;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;
import music.theory.Tone;

public class Track {

    private int id;
    private String name;
    private int instrument;
    private int channel;

    private Pitch root = new Pitch(NoteName.C.getMidiCode());
    private Tone hangnem = Tone.MAJ;

    private List<Note> notes = new ArrayList<>();

    private int volume = 100;

    private Random random;


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
            calculatedNum = (n.getStartTick() + n.getLength().getErtek()) / MidiEngine.TICKS_IN_MEASURE + 1;
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

    public void removePitches(Pitch p) {

        notes.removeIf(n -> n.getPitch().getMidiCode() == p.getMidiCode());
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
        t.setRoot(new Pitch(root.getMidiCode()));
        t.setHangnem(hangnem);
        t.setChannel(channel);
        t.setInstrument(instrument);
        t.setName(name);
        t.setVolume(volume);

        return t;
    }

    public void shiftNotesFromMeasureBy(int measureNum, int by) {
        int firstTickToShift = measureNum * MidiEngine.TICKS_IN_MEASURE;
        notes.forEach(n -> {
            if(n.getStartTick() >= firstTickToShift) {
                n.setStartTick(n.getStartTick() + MidiEngine.TICKS_IN_MEASURE * by);
            }
        });
    }

    public void duplicateMeasure(int measureNum) {

        List<Note> origNotes = this.getCloneOfNotesInMeasure(measureNum);

        this.shiftNotesFromMeasureBy(measureNum + 1, 1);

        this.getCloneOfNotesInMeasure(measureNum).forEach(n -> {
            Note duplicate = n.clone();
            duplicate.setStartTick(n.getStartTick() + MidiEngine.TICKS_IN_MEASURE);
            this.notes.add(duplicate);
        });
    }

    /**
     * visszaadja egy measure note-jainak a klónját
     * @param measureNum
     * @return
     */
    public List<Note> getCloneOfNotesInMeasure(int measureNum){
        return notes.stream().filter(n -> n.getStartTick() >= measureNum * MidiEngine.TICKS_IN_MEASURE && n.getStartTick() < (measureNum + 1) * MidiEngine.TICKS_IN_MEASURE).map(n -> n.clone()).collect(Collectors.toList());
    }

    public List<Note> getNotesInMeasure(int measureNum){
        return notes.stream().filter(n -> n.getStartTick() >= measureNum * MidiEngine.TICKS_IN_MEASURE && n.getStartTick() < (measureNum + 1) * MidiEngine.TICKS_IN_MEASURE).collect(Collectors.toList());
    }

    public List<Note> getDistinctNotesInMeasure(int measureNum){
        List<Note> notes = getNotesInMeasure(measureNum);
        notes.removeIf(n -> n.getStartTickRelativeToMeasure() > 0);

        return notes;
    }

    public void deleteMeasure(int measureNum) {
        notes.removeAll(notes.stream().filter(n -> n.getStartTick() >= measureNum * MidiEngine.TICKS_IN_MEASURE && n.getStartTick() < (measureNum + 1) * MidiEngine.TICKS_IN_MEASURE).collect(Collectors.toList()));
        this.shiftNotesFromMeasureBy(measureNum + 1, -1);
    }

    public void randomize(int seed, int maxNoteLength) {
        random = new Random(seed);
        List<NoteLength> rythm = generateRythm(random.nextInt(7) + 1, maxNoteLength);
        int measureNum = this.getMeasureNum();
        for(int measureIdx = 0; measureIdx < measureNum; measureIdx++) {
            List<Note> notesOfMeasure = this.getDistinctNotesInMeasure(measureIdx);
            for(int noteIdx = 0; noteIdx < notesOfMeasure.size(); noteIdx++) {
                Note currentNote = notesOfMeasure.get(noteIdx);
                notes.remove(currentNote);
                int sumLength = 0;


                for(int i = 0; i < rythm.size(); i+=2) {
                    Note newNote = new Note();
                    newNote.setPitch(currentNote.getPitch());
                    newNote.setStartTick(sumLength + (measureIdx * MidiEngine.TICKS_IN_MEASURE));
                    newNote.setLength(rythm.get(i));

                    sumLength += newNote.getLength().getErtek();
                    try {
                        sumLength += rythm.get(i + 1).getErtek();
                    }catch(Exception e) {

                    }
                    if(newNote.getStartTickRelativeToMeasure() + newNote.getLength().getErtek() <= 32) {
                        notes.add(newNote);
                    }
                }
            }
        }
    }


    private <T> T chooseFromList(List<T> l) {
        int indx = random.nextInt(l.size());
        return l.get(indx);
    }



    public void generateArpeggio(int shift, NoteLength hossz, NoteLength szunet) {
        int measureNum = this.getMeasureNum();
        for(int i = 0; i < measureNum; i++) {
            List<Note> notesOfMeasure = this.getNotesInMeasure(i);
            for(int j = 0; j < notesOfMeasure.size(); j++) {
                Note currentNote = notesOfMeasure.get(j);
                notes.remove(currentNote);
                int sumLength = 0;
                while(sumLength < MidiEngine.TICKS_IN_MEASURE) {
                    Note newNote = new Note();
                    newNote.setLength(hossz);
                    int startTick = sumLength + (i * MidiEngine.TICKS_IN_MEASURE) + (shift * j);
                    newNote.setStartTick(startTick);
                    newNote.setPitch(currentNote.getPitch());
                    sumLength += hossz.getErtek() + szunet.getErtek();
                    notes.add(newNote);
                }
            }
        }
    }

    private List<NoteLength> generateRythm(int noteNum, int maxNoteLength){

        List<NoteLength> retVal = new ArrayList<>();

        int sumLength = 0;
        while(sumLength < MidiEngine.TICKS_IN_MEASURE) {
            NoteLength nl = null;
            while(nl == null) {
                nl = chooseFromList(Arrays.asList(NoteLength.values()));
                if(nl.getErtek() != maxNoteLength) {
                    nl = null;
                }
            }
            sumLength += nl.getErtek();
            retVal.add(nl);


            if(sumLength < MidiEngine.TICKS_IN_MEASURE) {
                NoteLength szunet = null;

                while(szunet == null) {
                    szunet = chooseFromList(Arrays.asList(NoteLength.values()));
                    if(szunet.getErtek() + sumLength > MidiEngine.TICKS_IN_MEASURE) {
                        szunet = null;
                    }
                }
                sumLength += szunet.getErtek();
                retVal.add(szunet);
            }
        }
        System.out.println("Rythm: " + retVal);
        return retVal;
    }
}
