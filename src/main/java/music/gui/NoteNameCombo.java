package music.gui;

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import music.theory.NoteName;

public class NoteNameCombo extends JComboBox<NoteName> {

    public NoteNameCombo() {
        super();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        Arrays.asList(NoteName.values()).forEach(i -> {
            model.addElement(i);
        });
        this.setModel(model);
        this.setSelectedIndex(0);
    }
}
