package music.gui.trackeditor;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import music.App;
import music.event.tracks.KeyBoardClearButtonEvent;
import music.event.tracks.KeyBoardFillButtonEvent;
import music.event.tracks.KeyBoardSelectButtonEvent;
import music.gui.measure.PianoKey;
import music.theory.NoteName;
import music.theory.Pitch;

public class KeyBoard extends JPanel{

    int minOctave = 3;

    List<Pitch> pitches;

    public KeyBoard() {
        setLayout(new GridLayout(0, 2));
        //        setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(-15, 0, 10, 0),  new EtchedBorder()));
        setBorder(new EmptyBorder(-16, 0, 10, 0));

        //setAlignmentY(TOP_ALIGNMENT);
        this.minOctave = minOctave;
        initGui();
    }

    private void initGui() {
        this.removeAll();
        pitches = new ArrayList<>();
        int counter = 0;
        for (int o = minOctave + 2; o >= minOctave - 1; o--) {
            for (int i = 11; i >= 0; i--) {

                Pitch pitch = new Pitch(NoteName.byCode(i).getMidiCode()).shift(o);

                pitches.add(pitch);

                PianoKey pk = new PianoKey(pitch);
                add(pk);

                JPanel pn = new JPanel();
                pn.setLayout(new BoxLayout(pn, BoxLayout.X_AXIS));
                add(pn);

                JButton btn = new JButton("x");
                btn.setMargin(new Insets(2,2,2,2));
                btn.setBackground(App.RED);
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        App.eventBus.post(new KeyBoardClearButtonEvent(pitch));

                    }
                });
                pn.add(btn);

                btn = new JButton("s");
                btn.setMargin(new Insets(2,2,2,2));
                btn.setBackground(App.SELECT_COLOR);
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        App.eventBus.post(new KeyBoardSelectButtonEvent(pitch));

                    }
                });
                pn.add(btn);

                btn = new JButton("f");
                btn.setMargin(new Insets(2,2,2,2));
                btn.setBackground(App.DEFAULT_NOTE_LABEL_COLOR);
                btn.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        App.eventBus.post(new KeyBoardFillButtonEvent(pitch));

                    }
                });
                pn.add(btn);
            }
        }
        Collections.reverse(pitches);
    }

    public List<Pitch> getPitches() {
        return this.pitches;
    }

    public int getMinOctave() {
        return minOctave;
    }

    public void setMinOctave(int minOctave) {
        this.minOctave = minOctave;
        initGui();
    }


}
