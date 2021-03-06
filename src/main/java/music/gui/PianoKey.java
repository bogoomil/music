package music.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.SwingConstants;

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
        this.pitch = pitch;
        setHorizontalAlignment(SwingConstants.TRAILING);
        setPreferredSize(new Dimension(114, 16));
        setMargin(new Insets(3, 0, 3, 0));

        NoteName nn = this.pitch.getName();
        int oct = this.pitch.getOctave();

        this.setText(nn.name() + " (" + oct + "/ "+ ((oct * 12) +  nn.getMidiCode()) + ")");

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
                MainFrame.eventBus.post(new PianoKeyEvent(pitch));

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO Auto-generated method stub

            }
        });

        this.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.eventBus.post(new PianoKeyEvent(pitch));
            }
        });
    }
}
