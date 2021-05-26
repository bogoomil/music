package music.logic;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.App;
import music.theory.Measure;
import music.theory.Note;
import music.theory.NoteLength;

/**
 * Soundfont sf2 fájlokat a /usr/local/share/soundfonts könyvtárban keresi, default.sf2 néven
 * @author kunb
 *
 */

public class Player {

    public static final int CHORD_CHANNEL = 0;

    private static final Logger LOG = LoggerFactory.getLogger(Player.class);

    private static Map<String, Track> namedTracks = new HashMap<>();

    /**
     * negyed hang felbontása tick-ekre. 4 negyed = 32 tick;
     */
    public static final int RESOLUTION = 8;

    public static final int TICKS_IN_MEASURE = RESOLUTION * 4;

    private static Synthesizer synth;

    private static Sequencer sequencer;

    public static Sequencer getSequencer() {
        if(sequencer == null) {
            try {
                initSequencer();
            } catch (MidiUnavailableException | InvalidMidiDataException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(!sequencer.isOpen()) {
            try {
                sequencer.open();
            } catch (MidiUnavailableException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return sequencer;
    }

    public static void addNotesToTrack(Track track, int channel, Measure measure) throws InvalidMidiDataException {


        for(int i = 0; i < measure.getNotes().size();i++) {
            Note note = measure.getNotes().get(i);

            int startInTick = note.getStartInTick() + (measure.getNum() * Player.TICKS_IN_MEASURE);
            int endInTick = startInTick + (Player.TICKS_IN_MEASURE / note.getLength().getErtek());

            if(endInTick > (measure.getNum() + 1) * Player.TICKS_IN_MEASURE) {
                endInTick = (measure.getNum() + 1) * Player.TICKS_IN_MEASURE;
            }

            ShortMessage a = new ShortMessage();
            a.setMessage(ShortMessage.NOTE_ON, channel, note.getPitch().getMidiCode(), note.getVol());
            MidiEvent noteOn = new MidiEvent(a, startInTick);
            track.add(noteOn);

            ShortMessage b = new ShortMessage();
            b.setMessage(ShortMessage.NOTE_OFF, channel, note.getPitch().getMidiCode(), 0);

            LOG.debug("pitch: {}, start: {}, end {}", note.getPitch(), startInTick, endInTick);

            MidiEvent noteOff = new MidiEvent(b, endInTick);
            track.add(noteOff);
        }
    }

    public static int getNoteLenghtInMs(NoteLength length, int tempo) {
        int tickCount = Player.TICKS_IN_MEASURE / length.getErtek();
        return getTickLengthInMeasureMs(tickCount, tempo);
    }

    public static Synthesizer getSynth() {
        if(synth == null) {
            initSynth();
        }
        return synth;
    }
    public static int getTickLengthInMeasureMs(int tick, int tempo) {
        int msInNegyed = 60000 / tempo; // 120-as tempo esetén 500 ms egy negyed hang hossza
        int measureLengthInMs = msInNegyed * 4; // ütem hossza 120-as temponál 2000 ms
        return (measureLengthInMs / Player.TICKS_IN_MEASURE) * tick;
    }

    public static int getTickLengthInMs(int tempo) {
        int msInNegyed = 60000 / tempo; // 120-as tempo esetén 500 ms egy negyed hang hossza
        int measureLengthInMs = msInNegyed; // ütem hossza 120-as temponál 2000 ms
        return (measureLengthInMs / Player.TICKS_IN_MEASURE);
    }


    private static void initSynth() {
        try {



            //            File file = new File("/home/kunb/Java/workspace/music/src/main/resources/roland_sc_8820_1.sf2");
            //            Soundbank rolandSB = MidiSystem.getSoundbank(file);
            //            LOG.debug("ROLAND SB LENGTH: {}", rolandSB.getInstruments().length);

            synth = MidiSystem.getSynthesizer();
            synth.open();

            //            Soundbank defaultSB = synth.getDefaultSoundbank();
            //            LOG.debug("DEFAULT SB LENGTH: {}", defaultSB.getInstruments().length);
            //
            //            if(synth.isSoundbankSupported(rolandSB)) {
            //
            //                synth.unloadAllInstruments(defaultSB);
            //                //                Arrays.asList(defaultSB.getInstruments()).forEach(instr -> {
            //                //                    synth.unloadInstrument(instr);
            //                //                });
            //
            //                //                synth.unloadAllInstruments(synth.getDefaultSoundbank());
            //                LOG.debug("unloaded instruments, synth available instr count: {}", synth.getAvailableInstruments().length);
            //                synth.loadAllInstruments(rolandSB);
            //                LOG.debug("loaded instruments, synth available instr count: {}", synth.getAvailableInstruments().length);
            //            }
            //
            //            Arrays.asList(synth.getAvailableInstruments()).forEach(instr -> {
            //                LOG.debug("Instrument: {}", instr);
            //            });
            //

            LOG.debug("synthy initialized...");

        } catch (MidiUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void initSequencer() throws MidiUnavailableException, InvalidMidiDataException {
        sequencer = MidiSystem.getSequencer();
        sequencer.setTempoInBPM(App.getTEMPO());
    }



    /**
     *
     * @param note
     * @param channel
     * @param tempo
     */
    public static void playNote(Note note, MidiChannel channel, int tempo) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    int offset = getTickLengthInMeasureMs(note.getStartInTick(), tempo );
                    int length = getNoteLenghtInMs(note.getLength(), tempo);

                    LOG.debug("pitch: {}, offset: {}, length: {}", note.getPitch(), offset, length);

                    Thread.sleep(offset);
                    channel.noteOn(note.getPitch().getMidiCode(), note.getVol());


                    Thread.sleep(length);
                    channel.noteOff(note.getPitch().getMidiCode());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    public static void setSynth(Synthesizer synth) {
        Player.synth = synth;
    }


    public static Track getInstrumentTrack(int channel, int program) throws InvalidMidiDataException {

        Sequence seq = new Sequence(Sequence.PPQ, Player.RESOLUTION);
        Track track = seq.createTrack();

        Player.getSequencer().setSequence(seq);


        ShortMessage instrumentChange = new ShortMessage();
        instrumentChange.setMessage(ShortMessage.PROGRAM_CHANGE, channel, program,0);
        MidiEvent changeInstrument = new MidiEvent(instrumentChange, 1);
        track.add(changeInstrument);

        return track;
    }

    public static void playMeasure(Measure measure, MidiChannel channel) {
        measure.getNotes().forEach(n -> {
            playNote(n, channel, measure.getTempo());
        });
    }


}
