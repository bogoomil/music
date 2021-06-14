package music.gui;

import java.util.Arrays;
import java.util.List;

import javax.sound.midi.Instrument;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import music.logic.MidiEngine;

public class InstrumentCombo extends JComboBox {

    private static final List<Instrument> instruments = Arrays.asList(MidiEngine.getSynth().getAvailableInstruments());

    public InstrumentCombo(){
        super();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        instruments.forEach(i -> {
            model.addElement(i.getName());
        });
        this.setModel(model);
    }
    public int getProgram() {
        return instruments.get(this.getSelectedIndex()).getPatch().getProgram();
    }
}
