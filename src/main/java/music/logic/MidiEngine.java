package music.logic;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
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
import music.event.MeasureStartedEvent;
import music.event.TickOffEvent;
import music.event.TickOnEvent;
import music.theory.Note;
import music.theory.NoteLength;

/**
 * Soundfont sf2 fájlokat a /usr/local/share/soundfonts könyvtárban keresi, default.sf2 néven
 * @author kunb
 *
 */

public class MidiEngine {

    public static final int CHORD_CHANNEL = 0;

    public static final int MEASURE_START_MESSAGE_TYPE = 39;

    private static final Logger LOG = LoggerFactory.getLogger(MidiEngine.class);

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

        return sequencer;
    }

    public static void addNotesToTrack(Track track, int channel, Note note) throws InvalidMidiDataException {


        int startInTick = note.getStartTick();
        int endInTick = startInTick + note.getLength().getErtek();


        ShortMessage a = new ShortMessage();
        a.setMessage(ShortMessage.NOTE_ON, channel, note.getPitch().getMidiCode(), note.getVol());
        MidiEvent noteOn = new MidiEvent(a, startInTick);
        track.add(noteOn);

        ShortMessage b = new ShortMessage();
        b.setMessage(ShortMessage.NOTE_OFF, channel, note.getPitch().getMidiCode(), 0);

        LOG.debug("pitch: {}, start: {}, end {}", note.getPitch(), startInTick, endInTick);

        MidiEvent noteOff = new MidiEvent(b, endInTick);
        track.add(noteOff);


        if(note.getStartTick() % 32 == 0) {
            byte[] m = String.valueOf(note.getStartTick() / 32).getBytes();
            final MetaMessage metaMessage = new MetaMessage(MEASURE_START_MESSAGE_TYPE, m, m.length);
            final MidiEvent me2 = new MidiEvent(metaMessage, note.getStartTick());
            track.add(me2);
        }
    }

    public static int getNoteLenghtInMs(NoteLength length, int tempo) {
        int tickCount = length.getErtek();
        return getTickLengthInMeasureMs(tickCount, tempo);
    }

    public static Synthesizer getSynth() {
        if(synth == null) {
            initSynth();
        }
        if(!synth.isOpen()) {
            try {
                synth.open();
            } catch (MidiUnavailableException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return synth;
    }

    public static int getTickLengthInMeasureMs(int tick, int tempo) {
        int msInNegyed = 60000 / tempo; // 120-as tempo esetén 500 ms egy negyed hang hossza
        int measureLengthInMs = msInNegyed * 4; // ütem hossza 120-as temponál 2000 ms
        return (measureLengthInMs / MidiEngine.TICKS_IN_MEASURE) * tick;
    }

    private static void initSynth() {
        try {



            //            File file = new File("/home/kunb/Java/workspace/music/src/main/resources/roland_sc_8820_1.sf2");
            //            Soundbank rolandSB;
            //            try {
            //                rolandSB = MidiSystem.getSoundbank(file);
            //                LOG.debug("ROLAND SB LENGTH: {}", rolandSB.getInstruments().length);
            //
            //                MidiSystem.getSynthesizer().loadAllInstruments(rolandSB);
            //
            //            } catch (InvalidMidiDataException | IOException e) {
            //                // TODO Auto-generated catch block
            //                e.printStackTrace();
            //            }

            synth = MidiSystem.getSynthesizer();

            //            Soundbank sb = synth.getDefaultSoundbank();
            //
            //            SF2Soundbank sf2sb = (SF2Soundbank) sb;
            //
            //
            //            Arrays.asList(sf2sb.getSamples()).forEach(s -> {
            //                System.out.println(s.getName());
            //            });
            //
            //            LOG.debug("soundbank: ", sb.getName());



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

            LOG.debug("synth initialized...");

        } catch (MidiUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void initSequencer() throws MidiUnavailableException, InvalidMidiDataException {
        sequencer = MidiSystem.getSequencer();
        sequencer.addMetaEventListener(new MetaEventListener() {

            @Override
            public void meta(MetaMessage meta) {
                if(meta.getType() == MEASURE_START_MESSAGE_TYPE) {
                    String s = new String(meta.getData());
                    int mn = Integer.parseInt(s);
                    App.eventBus.post(new MeasureStartedEvent(mn));
                }
            }
        });
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
                    int offset = getTickLengthInMeasureMs(note.getStartTick(), tempo );
                    int length = getNoteLenghtInMs(note.getLength(), tempo);
                    Thread.sleep(offset);
                    App.eventBus.post(new TickOnEvent(note.getStartTick() ));
                    channel.noteOn(note.getPitch().getMidiCode(), note.getVol());
                    if(note.getStartTick() % 32 == 0) {
                        int mn = note.getStartTick() / 32;
                        App.eventBus.post(new MeasureStartedEvent(mn));

                    }
                    Thread.sleep(length);
                    App.eventBus.post(new TickOffEvent(note.getStartTick() ));
                    channel.noteOff(note.getPitch().getMidiCode());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }

    public static void setSynth(Synthesizer synth) {
        MidiEngine.synth = synth;
    }

    //    public static Sequence getSequence() throws InvalidMidiDataException {
    //        Sequence seq = new Sequence(Sequence.PPQ, MidiEngine.RESOLUTION);
    //
    //        MidiEngine.getSequencer().setSequence(seq);
    //
    //        return seq;
    //
    //    }


    public static Track getInstrumentTrack(Sequence seq, int channel, int program) throws InvalidMidiDataException {


        Track track = seq.createTrack();
        ShortMessage instrumentChange = new ShortMessage();
        instrumentChange.setMessage(ShortMessage.PROGRAM_CHANGE, channel, program,0);
        MidiEvent changeInstrument = new MidiEvent(instrumentChange, 0);
        track.add(changeInstrument);
        return track;
    }


    public static void playTrack(music.model.Track t, MidiChannel ch, int tempo) {
        Collections.sort(t.getNotes(), new Comparator<Note>() {

            @Override
            public int compare(Note o1, Note o2) {

                return Integer.compare(o1.getStartTick(), o2.getStartTick());
            }
        });

        t.getNotes().forEach(n -> {
            playNote(n, ch, tempo);
        });
    }

    public static void play(List<music.model.Track> tracks) throws InvalidMidiDataException, IOException, MidiUnavailableException {
        play(tracks, 120, 1);
    }

    public static void play(List<music.model.Track> tracks, int tempo, float tempoFactor) throws InvalidMidiDataException, IOException, MidiUnavailableException {
        Sequence seq = new Sequence(Sequence.PPQ, MidiEngine.RESOLUTION);
        Sequencer sequencer = MidiEngine.getSequencer();

        sequencer.setTempoFactor(tempoFactor);
        for(music.model.Track t :tracks) {
            javax.sound.midi.Track track = MidiEngine.getInstrumentTrack(seq, t.getChannel(), t.getInstrument());
            for(Note n : t.getNotes()) {
                MidiEngine.addNotesToTrack(track, t.getChannel(), n);
            }
        }
        if(!sequencer.isOpen()) {
            sequencer.open();
        }
        sequencer.setSequence(seq);
        sequencer.start();
        sequencer.setTempoInBPM(tempo);
        File file = new File("piece.mid");
        MidiSystem.write(seq,1,file);

    }
}
