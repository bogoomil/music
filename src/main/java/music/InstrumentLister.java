package music;

import java.io.IOException;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Soundbank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstrumentLister {
    private static final Logger LOG = LoggerFactory.getLogger(InstrumentLister.class);

    public static void printInstruments() throws InvalidMidiDataException, IOException, MidiUnavailableException {
        //        File file = new File("/home/kunb/Java/workspace/music/src/main/resources/roland_sc_8820_1.sf2");
        //        Soundbank soundbank = MidiSystem.getSoundbank(file);
        Soundbank soundbank =
                MidiSystem.getSynthesizer().getDefaultSoundbank();

        Instrument[] instrs = soundbank.getInstruments();
        for (int i = 0; i < instrs.length; i++) {
            LOG.debug("{} = {}", i, instrs[i].getName());
        }

        LOG.debug("========================================================================");

    }

}
