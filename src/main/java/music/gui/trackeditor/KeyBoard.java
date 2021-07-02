package music.gui.trackeditor;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import music.App;
import music.event.FillNotesEvent;
import music.event.KeyBoardClearButtonEvent;
import music.event.KeyBoardFillButtonEvent;
import music.event.KeyBoardSelectButtonEvent;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;

public class KeyBoard extends JPanel{

    private static int minOctave = 3;

    private static List<Pitch> pitches;

    private JDialog dialog;

    public KeyBoard() {
        setLayout(new GridLayout(0, 2));
        //        setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(-15, 0, 10, 0),  new EtchedBorder()));
        setBorder(new EmptyBorder(-16, 0, 10, 0));

        //setAlignmentY(TOP_ALIGNMENT);
        initGui();
    }

    private void initGui() {
        this.removeAll();
        pitches = new ArrayList<>();
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

                final JButton btnFill = new JButton("f");
                btnFill.setMargin(new Insets(2,2,2,2));
                btnFill.setBackground(App.DEFAULT_NOTE_LABEL_COLOR);
                btnFill.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        App.eventBus.post(new KeyBoardFillButtonEvent(pitch));
                        JDialog m = createDialog(pitch);
                        m.setLocationRelativeTo(btnFill);
                        m.setVisible(true);

                    }
                });
                pn.add(btnFill);
            }
        }
        Collections.sort(pitches, new Comparator<Pitch>() {

            @Override
            public int compare(Pitch o1, Pitch o2) {
                // TODO Auto-generated method stub
                return Integer.compare(o1.getMidiCode(), o2.getMidiCode());
            }
        });
        Collections.reverse(pitches);
    }

    public static List<Pitch> getPitches() {
        return pitches;
    }


    public void setMinOctave(int minOctave) {
        KeyBoard.minOctave = minOctave;
        initGui();
    }

    public static int getMinOctave() {
        return minOctave;
    }
    private JDialog createDialog(Pitch pitch){
        dialog = new JDialog();
        dialog.setTitle("Rythm");

        JPanel pn = new JPanel();
        pn.setLayout(new GridLayout(0,2));

        JLabel l = new JLabel("Notes length:");
        pn.add(l);


        JComboBox<NoteLength> cbHossz = new JComboBox<>();
        cbHossz.setModel(new DefaultComboBoxModel<>(NoteLength.values())) ;
        cbHossz.setSelectedIndex(9);
        pn.add(cbHossz);

        l = new JLabel("Beats:");
        pn.add(l);

        JComboBox<NoteLength> cbBeat = new JComboBox<>();
        cbBeat.setModel(new DefaultComboBoxModel<>(NoteLength.values())) ;
        cbBeat.setSelectedIndex(9);
        pn.add(cbBeat);

        l = new JLabel("Measures:");
        pn.add(l);

        JComboBox<Integer> cbMeasureNum = new JComboBox<>();
        cbMeasureNum.setModel(new DefaultComboBoxModel<>(new Integer[] {1,2,3,4,5,6,7,8,9,10,11})) ;
        pn.add(cbMeasureNum);

        l = new JLabel("From meas:");
        pn.add(l);

        JComboBox<Integer> cbFromMeasure= new JComboBox<>();
        cbFromMeasure.setModel(new DefaultComboBoxModel<>(new Integer[] {0,1,2,3,4,5,6,7,8,9,10,11})) ;
        pn.add(cbFromMeasure);



        JButton btnOk = new JButton("ok");
        pn.add(btnOk);
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new FillNotesEvent(pitch,
                        cbHossz.getItemAt(cbHossz.getSelectedIndex()),
                        cbBeat.getItemAt(cbBeat.getSelectedIndex()),
                        cbMeasureNum.getItemAt(cbMeasureNum.getSelectedIndex()),
                        cbFromMeasure.getItemAt(cbFromMeasure.getSelectedIndex())));
                dialog.setVisible(false);
            }
        });

        JButton btnCancel = new JButton("cancel");
        pn.add(btnCancel);

        dialog.add(pn);
        dialog.pack();

        return dialog;
    }



}
