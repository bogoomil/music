package music.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
        int firstTickToShift = measureNum * 32;
        notes.forEach(n -> {
            if(n.getStartTick() >= firstTickToShift) {
                n.setStartTick(n.getStartTick() + 32 * by);
            }
        });
    }

    public void duplicateMeasure(int measureNum) {

        List<Note> origNotes = this.getCloneOfNotesInMeasure(measureNum);

        this.shiftNotesFromMeasureBy(measureNum + 1, 1);

        this.getCloneOfNotesInMeasure(measureNum).forEach(n -> {
            Note duplicate = n.clone();
            duplicate.setStartTick(n.getStartTick() + 32);
            this.notes.add(duplicate);
        });
    }

    /**
     * visszaadja egy measure note-jainak a klónját
     * @param measureNum
     * @return
     */
    public List<Note> getCloneOfNotesInMeasure(int measureNum){
        return notes.stream().filter(n -> n.getStartTick() >= measureNum * 32 && n.getStartTick() < (measureNum + 1) * 32).map(n -> n.clone()).collect(Collectors.toList());
    }

    public List<Note> getNotesInMeasure(int measureNum){
        return notes.stream().filter(n -> n.getStartTick() >= measureNum * 32 && n.getStartTick() < (measureNum + 1) * 32).collect(Collectors.toList());
    }

    public void deleteMeasure(int measureNum) {
        notes.removeAll(notes.stream().filter(n -> n.getStartTick() >= measureNum * 32 && n.getStartTick() < (measureNum + 1) * 32).collect(Collectors.toList()));
        this.shiftNotesFromMeasureBy(measureNum + 1, -1);
    }

    public void randomize(int seed) {
        random = new Random(seed);
        int measureNum = this.getMeasureNum();
        for(int i = 0; i < measureNum; i++) {
            List<Note> notesOfMeasure = this.getNotesInMeasure(i);


            for(int j = 0; j < notesOfMeasure.size(); j++) {
                Note currentNote = notesOfMeasure.get(j);
                notes.remove(currentNote);

                int sumLength = 0;
                while(sumLength < 32) {
                    Note newNote = generateRandomNoteOfMeasure(currentNote.getPitch(), i, sumLength);
                    sumLength += newNote.getStartTick() + newNote.getLength().getErtek();

                    notes.add(newNote);
                }
            }
        }
    }

    private Note generateRandomNoteOfMeasure(Pitch pitch, int measureNum, int fromTick) {
        List<NoteLength> lengths = Arrays.asList(NoteLength.NYOLCAD, NoteLength.NEGYED);
        Note gen = new Note();
        gen.setPitch(pitch);
        gen.setLength(chooseFromList(lengths));
        int startTick = fromTick + random.nextInt(32 - fromTick) + (measureNum * 32);
        gen.setStartTick(startTick);

        return gen;
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
                while(sumLength < 32) {
                    Note newNote = new Note();
                    newNote.setLength(hossz);
                    int startTick = sumLength + (i * 32) + (shift * j);
                    newNote.setStartTick(startTick);
                    newNote.setPitch(currentNote.getPitch());
                    sumLength += hossz.getErtek() + szunet.getErtek();
                    notes.add(newNote);
                }
            }
        }
    }
}
