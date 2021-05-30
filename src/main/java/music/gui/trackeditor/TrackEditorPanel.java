package music.gui.trackeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.sound.midi.Instrument;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import music.event.TrackSelectedEvent;
import music.gui.MainFrame;
import music.logic.MidiEngine;
import music.theory.Track;

public class TrackEditorPanel extends JPanel {

    private Track track;
    JPanel pnMeasures;
    private JLabel lbId;
    private static Color origColor;
    private JComboBox cbChannel;
    private JLabel lbChannel;

    public TrackEditorPanel(Track track) {
        super();
        setTrack(track);
        MainFrame.eventBus.register(this);
    }

    public void setTrack(Track track) {
        this.track = track;
        setLayout(new BorderLayout(0, 0));

        JPanel pnButtons = new JPanel();
        add(pnButtons, BorderLayout.WEST);

        lbId = new JLabel("lbId");
        lbId.setPreferredSize(new Dimension(100, 15));
        lbId.setOpaque(true);
        pnButtons.add(lbId);
        origColor = lbId.getBackground();

        lbId.addMouseListener(new MouseListener() {

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
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                MainFrame.eventBus.post(new TrackSelectedEvent(track.getId()));

            }
        });


        JComboBox<Instrument> cbInstr = new JComboBox();
        cbInstr.setModel(new DefaultComboBoxModel(MidiEngine.getSynth().getAvailableInstruments()));
        pnButtons.add(cbInstr);

        cbInstr.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                track.setInstrument(cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram());
                System.out.println("instrument changed: track: " + track.getId() + " -> " + track.getInstrument());
            }
        });

        pnMeasures = new JPanel();
        add(pnMeasures, BorderLayout.CENTER);

        lbId.setText(track.getId() + "");

        lbChannel = new JLabel("MIDI channel");
        pnButtons.add(lbChannel);

        cbChannel = new JComboBox();
        cbChannel.setModel(new DefaultComboBoxModel(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"}));
        pnButtons.add(cbChannel);

        cbChannel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                track.setChannel(cbChannel.getSelectedIndex());
                System.out.println("channel changed: track: " + track.getId() + " -> " + track.getChannel());
            }
        });


    }

    public void refresh() {
        pnMeasures.removeAll();
        this.track.getMeasures().forEach(m -> {
            MeasureButton btn = new MeasureButton(m, track.getId());
            pnMeasures.add(btn);
        });
        this.validate();
    }

    public Track getTrack() {
        return this.track;
    }

    public void setSelected(boolean s) {
        if(s) {
            lbId.setBackground(Color.RED);
        } else {
            lbId.setBackground(origColor);
        }
    }

}
