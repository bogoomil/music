package music.gui.measure;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;

import music.event.MeasureNotesUpdatedEvent;
import music.event.NoteSelectionEvent;
import music.gui.MainFrame;
import music.theory.Note;
import music.theory.NoteLength;

public class NotePropertiesPanel extends JPanel{

    private Note note;
    private JSlider slVolume;
    private JComboBox<NoteLength> cbLength;

    public NotePropertiesPanel() {
        super();
        this.setPreferredSize(new Dimension(230, 600));

        MainFrame.eventBus.register(this);


    }

    @Subscribe
    private void handleNoteSelectionEvent(NoteSelectionEvent e) {
        this.note = e.getNote();
        removeAll();

        if(e.getNote() != null) {
            TitledBorder tbVol = new TitledBorder(null, "Volume", TitledBorder.LEADING, TitledBorder.TOP, null, null);;

            slVolume = new JSlider();
            slVolume.setMaximum(127);
            slVolume.setMinimum(0);
            slVolume.setSnapToTicks(true);
            slVolume.setPaintTicks(true);
            slVolume.setPaintLabels(true);
            slVolume.setMajorTickSpacing(50);
            slVolume.setMinorTickSpacing(5);
            slVolume.setBorder(tbVol);
            this.slVolume.setValue(e.getNote().getVol());

            slVolume.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    if(note != null) {
                        note.setVol(slVolume.getValue());
                        MainFrame.eventBus.post(new MeasureNotesUpdatedEvent());
                    }
                }
            });

            cbLength = new JComboBox();
            cbLength.setModel(new DefaultComboBoxModel(NoteLength.values()));
            cbLength.setSelectedItem(e.getNote().getLength());
            cbLength.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if(note != null) {
                        note.setLength(cbLength.getItemAt(cbLength.getSelectedIndex()));
                        MainFrame.eventBus.post(new MeasureNotesUpdatedEvent());
                    }

                }
            });
            add(cbLength);
            add(slVolume);
        }
        validate();
        repaint();
    }

}
