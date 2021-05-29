package music.gui.measure;

import java.awt.BorderLayout;
import java.util.Comparator;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.google.common.eventbus.Subscribe;

import music.event.MeasureSelectedEvent;
import music.gui.MainFrame;
import music.theory.Measure;
import music.theory.Note;
import music.theory.NoteName;
import music.theory.Pitch;

public class MeasureEditorPanel extends JPanel{

    private Measure measure;

    private JPanel pnToolbar = new JPanel();
    private JPanel pnCenter = new JPanel();
    private JPanel pnProperties = new JPanel();

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
        for (int o = minOctave + 2; o >= minOctave; o--) {
            for(int i = 11; i >=0; i--) {


                Pitch pitch = new Pitch(NoteName.byCode(i).getMidiCode()).shift(o);
                NoteLinePanel nlp = new NoteLinePanel(pitch);
                pnCenter.add(nlp);



            }
        }
        this.validate();

    }
    @Subscribe
    public void handleMeasureEvent(MeasureSelectedEvent ev) {
        this.measure = ev.getMeasure();
        refreshUI();
    }

    private int getMinOctave() {
        return measure.getNotes().stream().min(new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                Integer oct1 = o1.getPitch().getOctave();
                Integer oct2 = o2.getPitch().getOctave();
                return oct1.compareTo(oct2);
            }
        }).get().getPitch().getOctave();
    }


}
