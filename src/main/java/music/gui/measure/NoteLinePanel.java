package music.gui.measure;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import music.App;
import music.event.MeasureNotesUpdatedEvent;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.Pitch;

public class NoteLinePanel extends JPanel {
    private Pitch pitch;

    TickRowEditorPanel trep;

    JButton btnClear = new JButton("x");

    JButton btnToggleSelection = new JButton("s");

    JButton btnRythm = new JButton("f");

    JDialog dialog = createDialog();

    public NoteLinePanel(Pitch pitch) {
        super();
        trep = new TickRowEditorPanel(pitch);
        setPreferredSize(new Dimension(700, 20));
        this.pitch = pitch;
        setLayout(new BorderLayout(0, 0));

        JPanel pnButtons = new JPanel();
        this.add(pnButtons, BorderLayout.WEST);

        pnButtons.add(new PianoKey(pitch));

        btnClear.setFont(new Font("Dialog", Font.PLAIN, 8));
        btnClear.setMargin(new Insets(0, 0, 0, 0));
        pnButtons.add(btnClear);
        btnClear.setBackground(App.RED);
        btnClear.setToolTipText("Összes hangjegy törlése.");
        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                trep.removeAll();
                App.eventBus.post(new MeasureNotesUpdatedEvent());

            }
        });

        btnToggleSelection.setFont(new Font("Dialog", Font.PLAIN, 8));
        btnToggleSelection.setMargin(new Insets(0, 0, 0, 0));
        pnButtons.add(btnToggleSelection);
        btnToggleSelection.setBackground(App.DEFAULT_NOTE_LABEL_COLOR);
        btnToggleSelection.setToolTipText("Összes kijelölése");
        btnToggleSelection.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                trep.setToggleSelectionAll();
            }
        });

        btnRythm.setFont(new Font("Dialog", Font.PLAIN, 8));
        btnRythm.setMargin(new Insets(0, 0, 0, 0));
        pnButtons.add(btnRythm);
        btnRythm.setBackground(App.SELECT_COLOR);
        btnRythm.setToolTipText("Sor kitöltése ritmussal");
        btnRythm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog m = createDialog();
                m.setLocationRelativeTo(btnRythm);
                m.setVisible(true);
            }
        });


        this.add(trep, BorderLayout.CENTER);

    }

    public Pitch getPitch() {
        return pitch;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

    public TickRowEditorPanel getTrep() {
        return trep;
    }

    public void setTrep(TickRowEditorPanel trep) {
        this.trep = trep;
    }

    private JDialog createDialog(){
        dialog = new JDialog();
        dialog.setTitle("Rythm");

        JPanel pn = new JPanel();
        pn.setLayout(new GridLayout(0,2));

        JLabel l = new JLabel("Hangjegyek hossza:");
        pn.add(l);


        JComboBox<NoteLength> cbHossz = new JComboBox<>();
        cbHossz.setModel(new DefaultComboBoxModel<>(NoteLength.values())) ;
        cbHossz.setSelectedIndex(9);
        pn.add(cbHossz);

        l = new JLabel("Összes ütemre:");
        pn.add(l);

        JComboBox<NoteLength> cbBeat = new JComboBox<>();
        cbBeat.setModel(new DefaultComboBoxModel<>(NoteLength.values())) ;
        cbBeat.setSelectedIndex(9);
        pn.add(cbBeat);

        JButton btnOk = new JButton("ok");
        pn.add(btnOk);
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                trep.removeAll();

                fillNotes(cbHossz.getItemAt(cbHossz.getSelectedIndex()), cbBeat.getItemAt(cbBeat.getSelectedIndex()));
                App.eventBus.post(new MeasureNotesUpdatedEvent());

                dialog.setVisible(false);

            }

        });

        JButton btnCancel = new JButton("cancel");
        pn.add(btnCancel);

        dialog.add(pn);
        dialog.pack();

        return dialog;
    }

    private void fillNotes(NoteLength l, NoteLength b) {
        System.out.println("filling notes: " + l + " : " + b);
        int counter = 0;
        while(counter < 32) {
            Note n = new Note();
            n.setLength(l);
            n.setStartTick(counter);
            n.setPitch(pitch);
            counter += b.getErtek();
            trep.add(new NoteLabel(trep, n));
        }
        App.eventBus.post(new MeasureNotesUpdatedEvent());
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.trep.setEnabled(enabled);
        btnClear.setEnabled(enabled);
        btnRythm.setEnabled(enabled);
        btnToggleSelection.setEnabled(enabled);

    }

}
