package music.gui.measure;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;

import music.event.AddMeasureToTrackEvent;
import music.event.EnablePitchesEvent;
import music.event.MeasurePropertiesChangedEvent;
import music.event.MeasureSelectedEvent;
import music.gui.MainFrame;
import music.logic.MidiEngine;
import music.theory.Measure;

public class MeasurePropertiesPanel extends JPanel{
    private final JPanel panel = new JPanel();
    private final JLabel lblHangnem = new JLabel("");
    private final JButton btnClear = new JButton("Clear");
    private final JButton btnOctUp = new JButton("Oct up");
    private final JButton btnOctDown = new JButton("Oct down");
    private final JCheckBox chckbxEnableAllPitches = new JCheckBox("Enable all pitches");
    private final JButton btnPlay = new JButton("Play");
    private final JSlider slTempo = new JSlider();
    private final JPanel panel_1 = new JPanel();
    private final JComboBox<Instrument> cbInstr = new JComboBox<>();
    private final JPanel panel_2 = new JPanel();
    private final JComboBox cbChannel = new JComboBox();
    private final JPanel panel_3 = new JPanel();
    private final JComboBox cbArpeggio = new JComboBox();
    private final JButton btnGenerateArp = new JButton("Generate arp.");
    private final JButton btnAddToTrack = new JButton("Add to track");
    private final TitledBorder tbTempo = new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null);;


    private int currentInstrument;

    private Measure measure;


    public MeasurePropertiesPanel() {
        super();
        MainFrame.eventBus.register(this);
        this.setPreferredSize(new Dimension(230, 600));
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        this.add(lblHangnem);

        btnClear.setMargin(new Insets(0, 0, 0, 0));
        this.add(btnClear);
        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(measure != null) {
                    measure.setNotes(new ArrayList<>());
                    MainFrame.eventBus.post(new MeasurePropertiesChangedEvent());
                }

            }
        });


        btnOctUp.setMargin(new Insets(0, 0, 0, 0));

        this.add(btnOctUp);
        btnOctDown.setMargin(new Insets(0, 0, 0, 0));

        btnOctUp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(measure != null) {
                    measure.shiftOctave(1);
                    MainFrame.eventBus.post(new MeasurePropertiesChangedEvent());
                }

            }
        });


        this.add(btnOctDown);
        btnOctDown.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(measure != null) {
                    measure.shiftOctave(-1);
                    MainFrame.eventBus.post(new MeasurePropertiesChangedEvent());
                }

            }
        });



        chckbxEnableAllPitches.setSelected(true);
        chckbxEnableAllPitches.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                MainFrame.eventBus.post(new EnablePitchesEvent(chckbxEnableAllPitches.isSelected()));

            }
        });

        this.add(chckbxEnableAllPitches);

        this.add(btnPlay);

        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                play(cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram());
            }
        });

        slTempo.setValue(200);
        slTempo.setSnapToTicks(true);
        slTempo.setPaintTicks(true);
        slTempo.setPaintLabels(true);
        slTempo.setMinorTickSpacing(10);
        slTempo.setMinimum(60);
        slTempo.setMaximum(300);
        slTempo.setMajorTickSpacing(60);

        slTempo.setBorder(tbTempo);

        slTempo.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                measure.setTempo(slTempo.getValue());

            }
        });

        this.add(slTempo);
        panel_1.setBorder(new TitledBorder(null, "Instrument", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        DefaultComboBoxModel<Instrument> model = new DefaultComboBoxModel(MidiEngine.getSynth().getAvailableInstruments());
        cbInstr.setModel(model);
        cbInstr.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent arg0) {

                currentInstrument = cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram();

            }
        });


        this.add(panel_1);
        cbInstr.setPreferredSize(new Dimension(200, 24));

        panel_1.add(cbInstr);
        panel_2.setBorder(new TitledBorder(null, "MIDI Channel", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        this.add(panel_2);
        cbChannel.setPreferredSize(new Dimension(200, 24));
        cbChannel.setModel(new DefaultComboBoxModel(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"}));

        panel_2.add(cbChannel);
        panel_3.setBorder(new TitledBorder(null, "Arpeggio preset", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        this.add(panel_3);
        cbArpeggio.setPreferredSize(new Dimension(200, 24));

        panel_3.add(cbArpeggio);

        this.add(btnGenerateArp);

        this.add(btnAddToTrack);

        btnAddToTrack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.eventBus.post(new AddMeasureToTrackEvent(measure));

            }
        });

    }

    @Subscribe
    public void handleMeasureEvent(MeasureSelectedEvent ev) {
        this.measure = ev.getMeasure();
    }

    private void play(int instrument) {
        MidiChannel[] channels = MidiEngine.getSynth().getChannels();
        channels[MidiEngine.CHORD_CHANNEL].programChange(instrument);

        Measure m = measure.clone();
        m.setNum(0);
        MidiEngine.playMeasure(m, channels[cbChannel.getSelectedIndex()]);


    }


}
