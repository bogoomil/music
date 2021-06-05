package music.gui.trackeditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.Instrument;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import music.App;
import music.event.tracks.AddMeasureToTrackEvent;
import music.event.tracks.DeleteNotesFromTrackEvent;
import music.event.tracks.PlayTrackEvent;
import music.event.tracks.TrackVolumeChangedEvent;
import music.event.tracks.ZoomEvent;
import music.logic.MidiEngine;
import music.theory.NoteName;
import music.theory.Pitch;
import music.theory.Scale;
import music.theory.Tone;

public class TrackPropertiesPanel extends JPanel {
    private static JComboBox<NoteName> cbRoot;
    private static JComboBox<Tone> cbTone;
    private JComboBox<Instrument> cbInstr;
    private JComboBox cbChannel;
    private JSlider slTempo;
    private JPanel panel;
    private JPanel panel_1;
    private JSlider slVolume;
    private JButton btnPlay;
    private JButton btnStop;

    private JSlider slZoom;
    private JButton btnAddMeasure;
    private JButton btnClear;

    public TrackPropertiesPanel() {
        setPreferredSize(new Dimension(202, 493));

        btnPlay = new JButton("Play");
        btnPlay.setPreferredSize(new Dimension(90, 25));
        btnPlay.setBackground(App.GREEN);
        add(btnPlay);
        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new PlayTrackEvent(cbChannel.getSelectedIndex(),cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram(), slTempo.getValue()));

            }
        });

        btnStop = new JButton("Stop");
        btnStop.setPreferredSize(new Dimension(90, 25));
        btnStop.setBackground(App.RED);
        add(btnStop);
        btnStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MidiEngine.getSynth().close();

            }
        });

        btnClear = new JButton("Clear");
        add(btnClear);
        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new DeleteNotesFromTrackEvent());
            }
        });

        cbRoot = new JComboBox();
        cbRoot.setModel(new DefaultComboBoxModel(NoteName.values()));
        add(cbRoot);

        cbTone = new JComboBox();
        cbTone.setModel(new DefaultComboBoxModel(Tone.values()));
        add(cbTone);

        slTempo = new JSlider();
        slTempo.setValue(200);
        slTempo.setSnapToTicks(true);
        slTempo.setPaintTicks(true);
        slTempo.setPaintLabels(true);
        slTempo.setMinorTickSpacing(10);
        slTempo.setMinimum(60);
        slTempo.setMaximum(300);
        slTempo.setMajorTickSpacing(60);
        slTempo.setBorder(new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(slTempo);

        slVolume = new JSlider();
        slVolume.setMaximum(127);
        slVolume.setMinimum(0);
        slVolume.setSnapToTicks(true);
        slVolume.setPaintTicks(true);
        slVolume.setPaintLabels(true);
        slVolume.setMajorTickSpacing(50);
        slVolume.setMinorTickSpacing(5);
        slVolume.setBorder(new TitledBorder(null, "Volume", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(slVolume);

        slVolume.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                App.eventBus.post(new TrackVolumeChangedEvent(slVolume.getValue()));

            }
        });

        panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Instrument", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panel_1);

        cbInstr = new JComboBox<>();
        panel_1.add(cbInstr);
        cbInstr.setModel(new DefaultComboBoxModel(MidiEngine.getSynth().getAvailableInstruments()));
        cbInstr.setPreferredSize(new Dimension(180, 24));

        panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Midi channel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panel);

        cbChannel = new JComboBox();
        panel.add(cbChannel);
        cbChannel.setPreferredSize(new Dimension(180, 24));
        cbChannel.setModel(new DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));

        slZoom = new JSlider();
        slZoom.setValue(100);
        slZoom.setSnapToTicks(true);
        slZoom.setPaintTicks(true);
        slZoom.setPaintLabels(true);
        slZoom.setMinimum(10);
        slZoom.setMaximum(100);
        slZoom.setMajorTickSpacing(10);
        slZoom.setBorder(new TitledBorder(null, "Zoom", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        slZoom.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                App.eventBus.post(new ZoomEvent(slZoom.getValue()));

            }
        });
        add(slZoom);

        btnAddMeasure = new JButton("Add measure");
        add(btnAddMeasure);
        btnAddMeasure.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new AddMeasureToTrackEvent());

            }
        });


    }

    public static Pitch[] getScale() {
        return Scale.getScale(new Pitch(cbRoot.getItemAt(cbRoot.getSelectedIndex()).getMidiCode()), cbTone.getItemAt(cbTone.getSelectedIndex()));
    }

}
