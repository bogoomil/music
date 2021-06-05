package music.gui.trackeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import music.App;
import music.event.PianoKeyEvent;
import music.theory.NoteName;
import music.theory.Pitch;

public class PianoKey extends JButton{

    private Pitch pitch;

    public PianoKey() {
        this(new Pitch(NoteName.C.getMidiCode()).shift(3));
    }

    public PianoKey(Pitch pitch) {
        super();
        setFont(new Font("Dialog", Font.PLAIN, 9));

        this.setMargin(new Insets(0, 0, 0, 0));
        this.pitch = pitch;
        setHorizontalAlignment(SwingConstants.TRAILING);

        NoteName nn = this.pitch.getName();
        int oct = this.pitch.getOctave();

        this.setText(nn.name() + " (" + oct +  ")");

        if(nn == NoteName.Ab || nn == NoteName.Bb || nn == NoteName.Cs || nn == NoteName.Eb || nn == NoteName.Fs) {
            this.setBackground(Color.BLACK);
            this.setForeground(Color.WHITE);
        } else {
            this.setBackground(Color.WHITE);
            this.setForeground(Color.BLACK);
        }
        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                App.eventBus.post(new PianoKeyEvent(pitch));

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        });

        this.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new PianoKeyEvent(pitch));
            }
        });
    }
}
