package music.gui.measure;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import music.gui.PianoKey;
import music.theory.Pitch;

public class NoteLinePanel extends JPanel {
    private Pitch pitch;

    TickRowEditorPanel trep;;

    public NoteLinePanel(Pitch pitch) {
        super();
        trep = new TickRowEditorPanel(pitch);
        setPreferredSize(new Dimension(700, 20));
        this.pitch = pitch;
        setLayout(new BorderLayout(0, 0));
        this.add(new PianoKey(pitch), BorderLayout.WEST);
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
