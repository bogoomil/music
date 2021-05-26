package music;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.theory.Chord;
import music.theory.ChordDegree;
import music.theory.ChordType;
import music.theory.NoteName;
import music.theory.Pitch;

public class Generator {

    private static final Logger LOG = LoggerFactory.getLogger(Generator.class);

    //    private static Random random = new Random(9696);
    private static Random random = new Random(765);

    private static final int CHORD_INSTRUMENT = 9;
    private static final int DEFAULT_OCTAVE = 4;
    private static final int TEMPO = 120;
    private static final int SEQUENCE_RESOLUTION = 2;

    private static Pitch key;
    private static ChordType keyType;

    private static LocalDateTime start;

    private static ChordDegree[] DEGREES = new ChordDegree[]{
            ChordDegree.i,
            ChordDegree.i,
            ChordDegree.iv,
            ChordDegree.i,
            ChordDegree.i,
            ChordDegree.iv,
            ChordDegree.v,
            ChordDegree.i,
            ChordDegree.i,
            ChordDegree.iv,
            ChordDegree.v,
            ChordDegree.i,

    };


    public static void main(String[] args) throws MidiUnavailableException {

        try {
            InstrumentLister.printInstruments();
        } catch (InvalidMidiDataException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        start = LocalDateTime.now();
        try {

            //key = new Pitch(random.nextInt(NoteName.values().length)).shift(DEFAULT_OCTAVE);
            //            keyType = random.nextInt() % 2 == 0 ? ChordType.MAJ : ChordType.MIN;

            key = new Pitch(NoteName.A.getMidiCode()).shift(DEFAULT_OCTAVE);
            keyType = ChordType.MIN;

            LOG.debug("KEY: {} {}", key, keyType);


            new Generator().play();
        } catch (MidiUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void play() throws MidiUnavailableException, InvalidMidiDataException, InterruptedException, IOException {
        Sequencer player = MidiSystem.getSequencer();

        player.addMetaEventListener(new MetaEventListener() {

            @Override
            public void meta(MetaMessage meta) {
                LOG.debug("EXITING: ", meta.getType());

                LocalDateTime end = LocalDateTime.now();

                LOG.debug("length: {}, end: {}", start.until(end, ChronoUnit.SECONDS));

                System.exit(0);

            }
        });

        player.setTempoInBPM(TEMPO);

        LOG.debug("tempo: {}", player.getTempoInBPM() );

        player.open();

        Sequence seq = new Sequence(Sequence.PPQ, SEQUENCE_RESOLUTION);

        this.addChordProgression(seq);

        player.setSequence(seq);
        player.start();

        File f = new File("piece.mid");

        LOG.debug("creating midi file: {}", f.getAbsolutePath());
        MidiSystem.write(seq,1,f);
        //        Thread.sleep(10000);
        //        player.close();


    }

    private void addChordProgression(Sequence seq) throws InvalidMidiDataException {


        List<Chord> chords = this.generateChords();


        Track track = seq.createTrack();

        ShortMessage first = new ShortMessage();
        first.setMessage(ShortMessage.PROGRAM_CHANGE, 1, CHORD_INSTRUMENT, 0);
        MidiEvent changeInstrument = new MidiEvent(first, 1);
        track.add(changeInstrument);
        final int beatDuration = 4; // how many beats to hold each chord
        int c = 0;
        for (Chord ch : chords) {

            //            int inv = random.nextInt(3);
            //
            //            LOG.debug("inversion: {}", inv);
            //            switch (inv) {
            //            case 1:
            //                ch = Chord.getChordInversion(ch);
            //                break;
            //
            //            case 2:
            //                ch = Chord.getChordInversion(ch);
            //                break;
            //
            //            default:
            //                break;
            //            }


            Pitch[] pitches = ch.getPitches();



            for (int p = 0; p < pitches.length; p++) {

                int start = p + (c * beatDuration);
                int end = (c + 1) * beatDuration;

                Pitch pitch = pitches[p];
                LOG.debug("{} start: {}, end {}", pitch, start, end);

                this.createSound(track, 0, pitch, start, end, 100);


            }
            LOG.debug("++++++++++++++++++++++++++++++++++++++++++++++++++++++");


            c++;
        } // c for loop
    }

    private List<Chord> generateChords() {

        List<Chord> retVal = new ArrayList<>();

        retVal = Arrays.asList(new Chord("MIN7", 57, 60, 64, 67),// A
                new Chord("MIN7", 57, 60, 64, 67),// A
                new Chord("MIN7", 57, 60, 64, 67),// A
                new Chord("MIN7", 57, 60, 64, 67),// A
                new Chord("MIN", 62, 65, 69),// D
                new Chord("MIN7", 57, 60, 64, 67),// A
                new Chord("MIN7", 57, 60, 64, 67),// A
                new Chord("MIN7", 62, 65, 69, 72),// D
                new Chord("DOM7", 58, 62, 65, 68),// Bb
                new Chord("MIN7", 57, 60, 64, 67),// A
                new Chord("MIN7", 57, 60, 64, 67),// A
                new Chord("MIN", 62, 65, 69),// D
                new Chord("DOM7", 64, 68, 71, 74),// E
                new Chord("MIN", 57, 60, 64),// A
                new Chord("MIN", 57, 60, 64),// A
                new Chord("MIN", 57, 60, 64)// A
                );

        //Am G F E
        //        retVal = Arrays.asList(Chord.getChord(new Pitch(NoteName.A.getMidiCode()), ChordType.MIN).shiftOctave(4),// A
        //                Chord.getChord(new Pitch(NoteName.G.getMidiCode()), ChordType.MAJ).shiftOctave(4),
        //                Chord.getChord(new Pitch(NoteName.F.getMidiCode()), ChordType.MAJ).shiftOctave(4),
        //                Chord.getChord(new Pitch(NoteName.E.getMidiCode()), ChordType.MAJ).shiftOctave(4));


        //        ChordDegree currentDegree = ChordDegree.i;
        //        for(int i = 0; i < DEGREES.length; i++) {
        //            List<Chord> cs = Chord.getChordProgressions(DEGREES[i], key, keyType);
        //            Chord nextChord = this.chooseFromList( cs );
        //            LOG.debug("{}", nextChord);
        //            retVal.add(nextChord);
        //        }

        return retVal;
    }

    <T> T chooseFromList(List<T> l) {
        return l.get(random.nextInt(l.size()));
    }

    private void createSound(Track track, int measure, Pitch pitch, int start, int end, int vol) throws InvalidMidiDataException {
        ShortMessage a = new ShortMessage();
        a.setMessage(ShortMessage.NOTE_ON, 1, pitch.getMidiCode(), vol);
        MidiEvent noteOn = new MidiEvent(a, start);
        track.add(noteOn);

        ShortMessage b = new ShortMessage();
        b.setMessage(ShortMessage.NOTE_OFF, 1, pitch.getMidiCode(), vol);
        MidiEvent noteOff = new MidiEvent(b, end);
        track.add(noteOff);

    }

}
