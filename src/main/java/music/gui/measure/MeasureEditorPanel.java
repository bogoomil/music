package music.gui.measure;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.google.common.eventbus.Subscribe;

import music.event.EnablePitchesEvent;
import music.event.MeasureNotesUpdatedEvent;
import music.event.MeasurePropertiesChangedEvent;
import music.event.MeasureSelectedEvent;
import music.gui.MainFrame;
import music.theory.ChordType;
import music.theory.Measure;
import music.theory.Note;
import music.theory.NoteName;
import music.theory.Pitch;
import music.theory.Scale;

public class MeasureEditorPanel extends JPanel {

    private Measure measure;

    private MeasurePropertiesPanel pnToolbar = new MeasurePropertiesPanel();
    private JPanel pnCenter = new JPanel();
    private NotePropertiesPanel pnProperties = new NotePropertiesPanel();

    public MeasureEditorPanel() {
        super();
        MainFrame.eventBus.register(this);
        setLayout(new BorderLayout(10, 10));

        this.add(pnToolbar, BorderLayout.WEST);
        this.add(pnCenter, BorderLayout.CENTER);
        pnCenter.setLayout(new BoxLayout(pnCenter, BoxLayout.Y_AXIS));
        this.add(pnProperties, BorderLayout.EAST);

    }

    public void refreshUI() {

        pnCenter.removeAll();

        int minOctave = this.getMinOctave();
        for (int o = minOctave + 1; o >= minOctave - 1; o--) {
            for (int i = 11; i >= 0; i--) {

                Pitch pitch = new Pitch(NoteName.byCode(i).getMidiCode()).shift(o);
                NoteLinePanel nlp = new NoteLinePanel(pitch);
                pnCenter.add(nlp);

            }
        }

        measure.getNotes().forEach(n -> {
            NoteLinePanel nlp = getLineByPitch(n.getPitch());
            NoteLabel nl = new NoteLabel(nlp.getTrep(), n);

            nlp.getTrep().add(nl);
        });
        pnCenter.repaint();
        pnCenter.validate();

        this.validate();
        this.repaint();

    }

    @Subscribe
    public void handleMeasureSelectedEvent(MeasureSelectedEvent ev) {
        this.measure = ev.getMeasure();
        refreshUI();
    }

    @Subscribe
    public void handleMeasurePropertiesChangedEvent(MeasurePropertiesChangedEvent ev) {
        refreshUI();
    }

    private int getMinOctave() {
        if (measure.getNotes().size() > 0) {
            return measure.getNotes().stream().min(new Comparator<Note>() {
                @Override
                public int compare(Note o1, Note o2) {
                    Integer oct1 = o1.getPitch().getOctave();
                    Integer oct2 = o2.getPitch().getOctave();
                    return oct1.compareTo(oct2);
                }
            }).get().getPitch().getOctave();
        }
        return 4;
    }

    private NoteLinePanel getLineByPitch(Pitch p) {
        for (int i = 0; i < pnCenter.getComponentCount(); i++) {
            NoteLinePanel nlp = (NoteLinePanel) pnCenter.getComponent(i);
            if (nlp.getPitch().getMidiCode() == p.getMidiCode()) {
                return nlp;
            }

        }
        return null;
    }

    @Subscribe
    private void setRowsEnabled(EnablePitchesEvent e) {

        List<NoteName> scaleNotes = new ArrayList<>();
        Pitch[] scale = this.measure.getHangnem() == ChordType.MAJ ? Scale.majorScale(this.measure.getRoot()) : Scale.minorScale(this.measure.getRoot());

        for (Pitch p : scale) {
            scaleNotes.add(p.getName());
        }

        for (Component comp : pnCenter.getComponents()) {
            NoteLinePanel nlp = (NoteLinePanel) comp;
            if (!scaleNotes.contains(nlp.getPitch().getName())) {
                nlp.getTrep().setEnabled(e.isEnable());
            }
        }
        this.validate();
    }


    private List<Note> generateNotes() {
        List<Note> notes = new ArrayList<>();
        for (int i = 0; i < pnCenter.getComponentCount(); i++) {
            NoteLinePanel nlp = (NoteLinePanel) pnCenter.getComponent(i);
            for(Component c :nlp.getTrep().getComponents()) {
                NoteLabel l = (NoteLabel) c;
                notes.add(l.getNote());
            }
        }
        return notes;
    }

    @Subscribe
    private void handleMeasureNotesUpdatedEvent(MeasureNotesUpdatedEvent e) {
        this.measure.setNotes(this.generateNotes());
        pnCenter.revalidate();
        pnCenter.repaint();
        for(Component c :pnCenter.getComponents()) {
            c.revalidate();
            c.repaint();
        }
        for (int i = 0; i < pnCenter.getComponentCount(); i++) {
            NoteLinePanel nlp = (NoteLinePanel) pnCenter.getComponent(i);
            for(Component t : nlp.getTrep().getComponents()) {
                NoteLabel nl = (NoteLabel) t;
                nl.reCalculateSizeAndLocation();
            }

        }

    }

}
