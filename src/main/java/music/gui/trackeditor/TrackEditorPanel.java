package music.gui.trackeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.TrackSelectedEvent;
import music.gui.InstrumentCombo;
import music.model.Track;

public class TrackEditorPanel extends JPanel {

    private Track track;
    JPanel pnMeasures;
    private JLabel lbId;
    private static Color origColor;
    private JComboBox cbChannel;
    InstrumentCombo cbInstr = new InstrumentCombo();
    private JLabel lbChannel;
    private int id;

    private static int counter;

    private boolean selected;

    public TrackEditorPanel(Track track) {
        super();
        this.id = counter;
        counter++;
        setTrack(track);
    }

    public void setTrack(Track track) {
        this.track = track;
        setLayout(new BorderLayout(0, 0));
        App.eventBus.register(this);

        JPanel pnButtons = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) pnButtons.getLayout();
        flowLayout_1.setAlignment(FlowLayout.LEFT);
        add(pnButtons, BorderLayout.WEST);

        lbId = new JLabel("lbId");
        lbId.setPreferredSize(new Dimension(50, 15));
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
                App.eventBus.post(new TrackSelectedEvent(track));

            }
        });

        pnButtons.add(cbInstr);

        try {
            cbInstr.setSelectedIndex(track.getInstrument());
        }catch(Exception ex) {
            ex.printStackTrace();
        }

        cbInstr.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                track.setInstrument(cbInstr.getProgram());
            }
        });

        pnMeasures = new JPanel();
        FlowLayout flowLayout = (FlowLayout) pnMeasures.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        add(pnMeasures, BorderLayout.CENTER);

        lbId.setText(track.getId() + "");

        lbChannel = new JLabel("MIDI channel");
        pnButtons.add(lbChannel);

        cbChannel = new JComboBox();
        cbChannel.setModel(new DefaultComboBoxModel(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"}));
        pnButtons.add(cbChannel);

        cbChannel.setSelectedIndex(track.getChannel());

        cbChannel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                track.setChannel(cbChannel.getSelectedIndex());
            }
        });

        JSlider slVolume = new JSlider();
        slVolume.setMaximum(127);
        slVolume.setMinimum(0);
        slVolume.setSnapToTicks(true);
        slVolume.setPaintTicks(true);
        slVolume.setPaintLabels(true);
        slVolume.setMajorTickSpacing(50);
        slVolume.setMinorTickSpacing(5);
        slVolume.setBorder(new TitledBorder(null, "Volume", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        pnButtons.add(slVolume);

        slVolume.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                track.setVolume(slVolume.getValue());

            }
        });

        slVolume.setValue(track.getVolume());

        App.eventBus.post(new TrackSelectedEvent(track));

    }


    public Track getTrack() {
        return this.track;
    }

    public void setSelected(boolean s) {
        this.selected = s;
        if(s) {
            lbId.setBackground(App.RED);
            // App.eventBus.post(new TrackSelectedEvent(track));
        } else {
            lbId.setBackground(origColor);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    @Subscribe
    private void handleTrackSelectionEvent(TrackSelectedEvent e) {
        this.setSelected(e.getTrack().getId() == this.track.getId());
    }


}
