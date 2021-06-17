package music.gui;

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import music.theory.NoteLength;

public class NoteLengthCombo extends JComboBox<NoteLength> {

    public NoteLengthCombo() {
        super();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        Arrays.asList(NoteLength.values()).forEach(i -> {
            model.addElement(i);
        });
        this.setModel(model);
        this.setSelectedIndex(6);
    }
}
