package music.gui.trackeditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.ArpeggioEvent;
import music.event.DeleteNotesFromTrackEvent;
import music.event.MinOctaveChangedEvent;
import music.event.PianoKeyEvent;
import music.event.RandomizeEvent;
import music.event.TrackSelectedEvent;
import music.event.ZoomEvent;
import music.gui.NoteLengthCombo;
import music.gui.NoteNameCombo;
import music.gui.project.ProjectPanel;
import music.logic.MidiEngine;
import music.model.Track;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.Pitch;
import music.theory.Scale;
import music.theory.Tone;

public class TrackPropertiesPanel extends JPanel {
    private static NoteNameCombo cbRoot = new NoteNameCombo();
    private static JComboBox<Tone> cbTone;
    //private JComboBox<Instrument> cbInstr;
    //    InstrumentCombo cbInstr = new InstrumentCombo();
    //    private JComboBox cbChannel;
    //    private JSlider slTempo = new TempoSlider();
    private JPanel panel;
    private JPanel panel_1;
    //    private VolumeSlider slVolume = new VolumeSlider();
    private JButton btnPlay;
    private JButton btnStop;

    private JSlider slZoom;
    private JButton btnClear;

    private Track track;
    private JTextField tfSeed;
    private JButton btnRandomize;
    private JSlider slShift, slNoteLength;
    private JButton btnArpeggionator;
    private JButton btnOct;
    private JButton btnOct_1;

    public TrackPropertiesPanel() {
        App.eventBus.register(this);
        setPreferredSize(new Dimension(202, 722));

        btnPlay = new JButton("Play");
        btnPlay.setPreferredSize(new Dimension(90, 25));
        btnPlay.setBackground(App.GREEN);
        add(btnPlay);
        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {

                    MidiEngine.play(Arrays.asList(track), ProjectPanel.getTempo(), 1, TrackPanel.getCurrentTick());
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

        btnOct = new JButton("Oct+");
        add(btnOct);
        btnOct.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new MinOctaveChangedEvent(KeyBoard.getMinOctave() + 1));
                btnOct_1.setEnabled(true);

            }
        });

        btnOct_1 = new JButton("Oct-");
        add(btnOct_1);
        btnOct_1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(KeyBoard.getMinOctave() > 1) {
                    App.eventBus.post(new MinOctaveChangedEvent(KeyBoard.getMinOctave() - 1));
                    if(KeyBoard.getMinOctave() == 1) {
                        btnOct_1.setEnabled(false);
                    }
                }
            }
        });

        //        add(slTempo);
        //
        //        add(slVolume);
        //
        //        slVolume.addChangeListener(new ChangeListener() {
        //
        //            @Override
        //            public void stateChanged(ChangeEvent e) {
        //                App.eventBus.post(new VolumeChangedEvent(slVolume.getValue()));
        //
        //            }
        //        });

        //        panel_1 = new JPanel();
        //        panel_1.setBorder(new TitledBorder(null, "Instrument", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        //        add(panel_1);
        //
        //        //cbInstr = new JComboBox<>();
        //        panel_1.add(cbInstr);
        //        cbInstr.setPreferredSize(new Dimension(180, 24));
        //
        //        panel = new JPanel();
        //        panel.setBorder(new TitledBorder(null, "Midi channel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        //        add(panel);
        //
        //        cbChannel = new JComboBox();
        //        panel.add(cbChannel);
        //        cbChannel.setPreferredSize(new Dimension(180, 24));
        //        cbChannel.setModel(new DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15" }));

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

        add(new JSeparator(SwingConstants.HORIZONTAL));

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Random note generator"));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);
        tfSeed = new JTextField();
        tfSeed.setText("1234");
        panel.add(tfSeed);
        tfSeed.setColumns(10);

        JComboBox<NoteLength> cbNoteLength = new NoteLengthCombo();
        panel.add(cbNoteLength);

        btnRandomize = new JButton("Generate");
        panel.add(btnRandomize);
        btnRandomize.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int seed = Integer.valueOf(tfSeed.getText());
                App.eventBus.post(new RandomizeEvent(seed, cbNoteLength.getItemAt(cbNoteLength.getSelectedIndex()).getErtek()));
            }
        });

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Arpeggio generator"));
        add(panel);
        slShift = new JSlider();
        slShift.setMinorTickSpacing(2);
        slShift.setPaintLabels(true);
        slShift.setPaintTicks(true);
        slShift.setSnapToTicks(true);

        final TitledBorder b = new TitledBorder(null, "Shift", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        slShift.setBorder(b);
        slShift.setValue(0);
        slShift.setMaximum(16);
        slShift.setMinimum(-16);
        slShift.setMajorTickSpacing(8);
        slShift.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                b.setTitle("Shift: " + slShift.getValue());
            }
        });
        panel.add(slShift);

        JLabel l = new JLabel("Note length");
        panel.add(l);

        JComboBox<NoteLength> cbHossz = new NoteLengthCombo();
        cbHossz.setSelectedIndex(9);
        panel.add(cbHossz);

        l = new JLabel("Gap length");
        panel.add(l);

        JComboBox<NoteLength> cbSzunet = new NoteLengthCombo();
        cbSzunet.setSelectedIndex(9);
        panel.add(cbSzunet);

        btnArpeggionator = new JButton("Generate");
        panel.add(btnArpeggionator);
        btnArpeggionator.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new ArpeggioEvent(slShift.getValue(), cbHossz.getItemAt(cbHossz.getSelectedIndex()), cbSzunet.getItemAt(cbSzunet.getSelectedIndex())));
            }
        });

    }

    public static Pitch[] getScale() {
        return Scale.getScale(new Pitch(cbRoot.getItemAt(cbRoot.getSelectedIndex()).getMidiCode()), cbTone.getItemAt(cbTone.getSelectedIndex()));
    }

    @Subscribe
    private void handlePianoKeyEvent(PianoKeyEvent e) {



        MidiEngine.getSynth().getChannels()[track.getChannel()].programChange(track.getInstrument());

        Note n = new Note();
        n.setLength(NoteLength.NEGYED);
        n.setPitch(e.getPitch());
        n.setStartTick(0);


        MidiEngine.playNote(n, MidiEngine.getSynth().getChannels()[track.getChannel()], 120);
    }

    @Subscribe
    private void handleTrackSelectionEvent(TrackSelectedEvent e) {

        this.track = e.getTrack();

        //        cbInstr.setProgram(e.getTrack().getInstrument());
        //        slVolume.setValue(e.getTrack().getVolume());
        //        cbChannel.setSelectedIndex(e.getTrack().getChannel());
        cbRoot.setSelectedItem(e.getTrack().getRoot().getName());
        cbTone.setSelectedItem(e.getTrack().getHangnem());


    }


}
