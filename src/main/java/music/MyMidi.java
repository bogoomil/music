package music;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.theory.Chord;
import music.theory.ChordType;
import music.theory.NoteName;
import music.theory.Pitch;
import music.theory.Scale;

public class MyMidi {

    private static final Logger LOG = LoggerFactory.getLogger(MyMidi.class);
    protected static final int CHORD_INSTRUMENT = 0;

    public void test() throws MidiUnavailableException, InvalidMidiDataException, IOException {

        int instrument1 = 0;
        int instrument2 = 119;

        int note = 60;

        File file = new File("/home/kunb/Java/workspace/music/src/main/resources/roland_sc_8820_1.sf2");
        Soundbank soundbank = MidiSystem.getSoundbank(file);
        //        Soundbank soundbank =
        //                MidiSystem.getSynthesizer().getDefaultSoundbank();

        Instrument[] instrs = soundbank.getInstruments();
        for (int i = 0; i < instrs.length; i++) {
            LOG.debug("{} = {}", i, instrs[i].getName());
        }

        LOG.debug("========================================================================");


        Synthesizer synth = MidiSystem.getSynthesizer();



        for (int i = 0; i < synth.getLoadedInstruments().length; i++) {
            LOG.debug("{} = {}", i, synth.getLoadedInstruments()[i].getName());
        }
        LOG.debug("========================================================================");




        synth.unloadAllInstruments(MidiSystem.getSynthesizer().getDefaultSoundbank());
        synth.open();
        synth.loadAllInstruments(soundbank);




        for (int i = 0; i < synth.getLoadedInstruments().length; i++) {
            LOG.debug("{} = {}", i, synth.getLoadedInstruments()[i].getName());
        }




        LOG.debug("SUPPORTED: {}",synth.isSoundbankSupported(soundbank));
        LOG.debug("========================================================================");
        for (int i = 0; i < synth.getLoadedInstruments().length; i++) {
            LOG.debug("{} = {}", i, synth.getLoadedInstruments()[i].getName());
        }

        Sequencer sequencer = MidiSystem.getSequencer(false);

        sequencer.getTransmitter().setReceiver(synth.getReceiver());

        sequencer.setTempoInBPM(60);

        sequencer.open();

        Sequence sequence = new Sequence(Sequence.PPQ, 16);
        Track track = sequence.createTrack();

        // Set the instrument type
        track.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 1, instrument1, 0, 0));



        Pitch[] scale = Scale.minorScale(NoteName.Eb);


        List<Pitch> l = Arrays.asList(scale);

        int tck = 0;

        for(Pitch p : l) {
            p = p.shift(4);
            track.add(makeEvent(ShortMessage.NOTE_ON, 1, p.getMidiCode(), 100, tck));
            track.add(makeEvent(ShortMessage.NOTE_OFF, 1, p.getMidiCode(), 100, tck+6));
            tck += 4;

        }



        Track track2 = sequence.createTrack();

        track2.add(makeEvent(ShortMessage.PROGRAM_CHANGE, 2, instrument2, 0, tck));

        track2.add(makeEvent(ShortMessage.NOTE_ON, 2, note + 12, 100, tck));
        track2.add(makeEvent(ShortMessage.NOTE_OFF, 2, note + 12, 100, tck + 4));

        sequencer.setSequence(sequence);

        LocalDateTime now = LocalDateTime.now();

        LOG.debug("start time: {}", now.getSecond());

        sequencer.addControllerEventListener(new ControllerEventListener() {

            @Override
            public void controlChange(ShortMessage event) {
                LOG.debug("event: {}", event.getChannel());

            }
        }, new int[] {});

        sequencer.addMetaEventListener(new MetaEventListener() {

            @Override
            public void meta(MetaMessage meta) {
                LOG.debug("meta: {}", meta.getType());
                Pitch[] scale = Scale.majorScale(NoteName.Cs);

                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < scale.length; i++) {
                    scale[i] = scale[i].shift(4);
                    sb.append(scale[i]).append("; ");
                }

                LOG.debug("scale: {}", sb.toString());


                Chord[] chordAry = new Chord[] {
                        Chord.getChord(scale[0], ChordType.MAJ),
                        Chord.getChord(scale[1], ChordType.MAJ),
                        Chord.getChord(scale[2], ChordType.MAJ),
                        Chord.getChord(scale[3], ChordType.MAJ),
                        Chord.getChord(scale[4], ChordType.MAJ),
                        Chord.getChord(scale[5], ChordType.MAJ),
                        Chord.getChord(scale[6], ChordType.MAJ),
                        Chord.getChord(scale[7], ChordType.MAJ)
                };

                playChords(Arrays.asList(chordAry));

                synth.close();

            }
        });

        sequencer.start();

    }

    public MidiEvent makeEvent(int command, int channel,
            int note, int velocity, int tick) {

        MidiEvent event = null;

        try {

            ShortMessage a = new ShortMessage();
            a.setMessage(command, channel, note, velocity);

            event = new MidiEvent(a, tick);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return event;
    }

    public MidiEvent makeEvent(int command, int channel,
            int data1, int tick) {

        MidiEvent event = null;

        try {

            ShortMessage a = new ShortMessage();
            a.setMessage(command, channel, data1);

            event = new MidiEvent(a, tick);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return event;
    }

    public void playChords(List<Chord> chords) {

        try {

            Sequencer player = MidiSystem.getSequencer();

            player.addMetaEventListener(new MetaEventListener() {

                @Override
                public void meta(MetaMessage meta) {
                    LOG.debug("EXITING: ", meta.getType());
                    System.exit(0);

                }
            });


            LOG.debug("tempo: {}", player.getTempoInBPM() );

            player.setTempoInBPM(120);
            player.open();

            Sequence seq = new Sequence(Sequence.PPQ, 4);
            Track track = seq.createTrack();

            MidiEvent event = null;

            ShortMessage first = new ShortMessage();
            first.setMessage(ShortMessage.PROGRAM_CHANGE, 1, CHORD_INSTRUMENT, 0);

            MidiEvent changeInstrument = new MidiEvent(first, 1);
            track.add(changeInstrument);

            final int beatDuration = 4; // how many beats to hold each chord


            int c = 0;
            // c is for chord
            for (Chord ch : chords) {

                //ch = ch.shiftOctave(4);

                LOG.debug("chord: {}", ch);



                // n is for note
                Pitch[] pitches = ch.getPitches();

                for (Pitch p : pitches) {

                    ShortMessage a = new ShortMessage();
                    a.setMessage(ShortMessage.NOTE_ON, 1, p.getMidiCode(), 100);
                    MidiEvent noteOn = new MidiEvent(a, 1 + (c * beatDuration));
                    track.add(noteOn);
                }

                for (Pitch p : pitches) {
                    ShortMessage b = new ShortMessage();
                    b.setMessage(ShortMessage.NOTE_OFF, 1, p.getMidiCode(), 100);
                    MidiEvent noteOff = new MidiEvent(b, (c + 1) * beatDuration);
                    track.add(noteOff);
                } // n for loop

                c++;

            } // c for loop

            player.setSequence(seq);
            player.start();
            Thread.sleep(5000);
            player.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } // close play

}
