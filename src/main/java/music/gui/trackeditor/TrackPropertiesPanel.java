package music.gui.trackeditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.DeleteNotesFromTrackEvent;
import music.event.PianoKeyEvent;
import music.event.TrackSelectedEvent;
import music.event.TrackVolumeChangedEvent;
import music.event.ZoomEvent;
import music.gui.InstrumentCombo;
import music.logic.MidiEngine;
import music.model.Track;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;
import music.theory.Scale;
import music.theory.Tone;

public class TrackPropertiesPanel extends JPanel {
    private static JComboBox<NoteName> cbRoot;
    private static JComboBox<Tone> cbTone;
    //private JComboBox<Instrument> cbInstr;
    InstrumentCombo cbInstr = new InstrumentCombo();
    private JComboBox cbChannel;
    private JSlider slTempo;
    private JPanel panel;
    private JPanel panel_1;
    private JSlider slVolume;
    private JButton btnPlay;
    private JButton btnStop;

    private JSlider slZoom;
    private JButton btnClear;

    private Track track;

    public TrackPropertiesPanel() {
        App.eventBus.register(this);
        setPreferredSize(new Dimension(202, 493));

        btnPlay = new JButton("Play");
        btnPlay.setPreferredSize(new Dimension(90, 25));
        btnPlay.setBackground(App.GREEN);
        add(btnPlay);
        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    MidiEngine.play(Arrays.asList(track), slTempo.getValue(), 1, TrackPanel.getCurrentTick());
                } catch (InvalidMidiDataException | IOException | MidiUnavailableException e1) {
                    e1.printStackTrace();
                } catch(NullPointerException np) {
                    JOptionPane.showMessageDialog(TrackPropertiesPanel.this, "Nincs besettelve a track", "Hiba", JOptionPane.ERROR_MESSAGE);

                }
            }
        });

        btnStop = new JButton("Stop");
        btnStop.setPreferredSize(new Dimension(90, 25));
        btnStop.setBackground(App.RED);
        add(btnStop);
        btnStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MidiEngine.getSequencer().stop();
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
        cbRoot.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(track != null) {
                    track.setRoot(new Pitch(cbRoot.getItemAt(cbRoot.getSelectedIndex()).getMidiCode()));
                }

            }
        });
        add(cbRoot);

        cbTone = new JComboBox();
        cbTone.setModel(new DefaultComboBoxModel(Tone.values()));
        cbTone.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(track != null) {
                    track.setHangnem(cbTone.getItemAt(cbTone.getSelectedIndex()));
                }

            }
        });
        add(cbTone);

        slTempo = new JSlider();
        slTempo.setSnapToTicks(true);
        slTempo.setPaintTicks(true);
        slTempo.setPaintLabels(true);
        slTempo.setMinorTickSpacing(10);
        slTempo.setMinimum(60);
        slTempo.setMaximum(300);
        slTempo.setValue(140);
        slTempo.setMajorTickSpacing(60);
        TitledBorder tbTempo = new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        slTempo.setBorder(tbTempo);
        add(slTempo);
        slTempo.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tbTempo.setTitle("Tempo: " + slTempo.getValue());

            }
        });

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

        //cbInstr = new JComboBox<>();
        panel_1.add(cbInstr);
        cbInstr.setPreferredSize(new Dimension(180, 24));

        panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Midi channel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panel);

        cbChannel = new JComboBox();
        panel.add(cbChannel);
        cbChannel.setPreferredSize(new Dimension(180, 24));
        cbChannel.setModel(new DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));

        slZoom = new JSlider();
        slZoom.setValue(20);
        slZoom.setSnapToTicks(false);
        slZoom.setPaintTicks(true);
        slZoom.setPaintLabels(true);
        slZoom.setMinimum(5);
        slZoom.setMaximum(100);
        slZoom.setMajorTickSpacing(10);
        TitledBorder zoomBorder = new TitledBorder(null, "Zoom", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        slZoom.setBorder(zoomBorder);

        slZoom.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                zoomBorder.setTitle("Zoom: " + slZoom.getValue());
                App.eventBus.post(new ZoomEvent(slZoom.getValue()));

            }
        });
        add(slZoom);

    }

    public static Pitch[] getScale() {
        return Scale.getScale(new Pitch(cbRoot.getItemAt(cbRoot.getSelectedIndex()).getMidiCode()), cbTone.getItemAt(cbTone.getSelectedIndex()));
    }

    @Subscribe
    private void handlePianoKeyEvent(PianoKeyEvent e) {



        MidiEngine.getSynth().getChannels()[cbChannel.getSelectedIndex()].programChange(track.getInstrument());

        Note n = new Note();
        n.setLength(NoteLength.NEGYED);
        n.setPitch(e.getPitch());
        n.setStartTick(0);


        MidiEngine.playNote(n, MidiEngine.getSynth().getChannels()[cbChannel.getSelectedIndex()], 120);
    }

    @Subscribe
    private void handleTrackSelectionEvent(TrackSelectedEvent e) {

        this.track = e.getTrack();

        cbInstr.setProgram(e.getTrack().getInstrument());
        slVolume.setValue(e.getTrack().getVolume());
        cbChannel.setSelectedIndex(e.getTrack().getChannel());
        cbRoot.setSelectedItem(e.getTrack().getRoot().getName());
        cbTone.setSelectedItem(e.getTrack().getHangnem());


    }


}
