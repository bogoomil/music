package music.gui.measure;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import music.event.MeasureNotesUpdatedEvent;
import music.gui.MainFrame;
import music.gui.PianoKey;
import music.theory.Pitch;

public class NoteLinePanel extends JPanel {
    private Pitch pitch;

    TickRowEditorPanel trep;

    JButton btnClear = new JButton("x");

    public NoteLinePanel(Pitch pitch) {
        super();
        trep = new TickRowEditorPanel(pitch);
        setPreferredSize(new Dimension(700, 20));
        this.pitch = pitch;
        setLayout(new BorderLayout(0, 0));

        JPanel pnButtons = new JPanel();
        //        pnButtons.setPreferredSize(new Dimension(10, 30));
        //pnButtons.setPreferredSize(new Dimension(10, 40));
        pnButtons.add(new PianoKey(pitch));
        btnClear.setFont(new Font("Dialog", Font.PLAIN, 8));
        btnClear.setMargin(new Insets(0, 0, 0, 0));
        pnButtons.add(btnClear);

        this.add(pnButtons, BorderLayout.WEST);
        btnClear.setBackground(Color.RED);

        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                trep.removeAll();
                MainFrame.eventBus.post(new MeasureNotesUpdatedEvent());

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
}
