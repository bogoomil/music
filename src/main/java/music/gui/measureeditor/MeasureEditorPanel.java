package music.gui.measureeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;

import music.event.MeasureSelectedEvent;
import music.event.PianoKeyEvent;
import music.gui.MainFrame;
import music.logic.MidiEngine;
import music.theory.ChordType;
import music.theory.Measure;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;
import music.theory.Scale;

public class MeasureEditorPanel extends JPanel{
    private JPanel pnTickRows = new JPanel();
    private int currentInstrument;
    private JLabel lblHangnem;
    private JCheckBox chckbxEnableAllPitches;
    private JSlider slVolume;
    private JSlider slTempo;
    TitledBorder tbTempo;
    TitledBorder tbVolume;
    JComboBox cbChannel;

    private List<JLabel> labels = new ArrayList<>();
    private Color defaultLabelColor;
    private JButton btnClear;
    private JToggleButton tglbtnLoop;

    private JPanel pnCenter = new JPanel(new BorderLayout());

    private static final int VISIBLE_MEASURE_COUNT = 4;
    private JPanel panel_1;
    private JPanel panel_2;
    private JPanel panel_3;
    private JComboBox cbArpeggio;
    private JButton btnGenerateArp;

    private int measureCount;

    private static final Logger LOG = LoggerFactory.getLogger(MeasureEditorPanel.class);

    //    private NoteName root;
    //    private ChordType chordType;

    private Measure[] measures = new Measure[4];

    public MeasureEditorPanel() {
        super();
        UIManager.put("ToggleButton.select", new ColorUIResource( Color.RED ));

        MainFrame.eventBus.register(this);

        this.setLayout(new BorderLayout());

        this.add(pnCenter, BorderLayout.CENTER);

        pnCenter.add(pnTickRows, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(230, 10));
        add(panel, BorderLayout.WEST);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        lblHangnem = new JLabel("");
        panel.add(lblHangnem);

        chckbxEnableAllPitches = new JCheckBox("Enable all pitches");
        chckbxEnableAllPitches.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                setRowsEnabled(chckbxEnableAllPitches.isSelected());

            }

        });

        btnClear = new JButton("Clear");
        btnClear.setMargin(new Insets(0, 0, 0, 0));
        btnClear.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {

                measures = new Measure[4];
                measureCount = 0;
                pnTickRows.removeAll();

                pnTickRows.repaint();

                //                for(int i = 0; i< measures.size(); i++) {
                //                    Measure m = measures.get(i);
                //
                //                    m.setNotes(new ArrayList<Note>());
                //                    Note n = new Note();
                //                    n.setLength(NoteLength.HARMICKETTED);
                //                    n.setPitch(new Pitch(NoteName.C.getMidiCode()).shift(4));
                //                    n.setStartInTick(0);
                //                    m.addNote(n);
                //
                //                }

                //generateNotes();

            }
        });
        panel.add(btnClear);

        JButton btnOctUp = new JButton("Oct up");
        panel.add(btnOctUp);
        btnOctUp.setMargin(new Insets(0, 0, 0, 0));

        btnOctUp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeOctave(1);

            }

        });

        JButton btnOctDown = new JButton("Oct down");
        panel.add(btnOctDown);
        btnOctDown.setMargin(new Insets(0, 0, 0, 0));

        btnOctDown.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeOctave(-1);
            }
        });
        chckbxEnableAllPitches.setSelected(true);
        panel.add(chckbxEnableAllPitches);

        JButton btnPlay = new JButton("Play");
        panel.add(btnPlay);

        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                play(currentInstrument);

            }
        });

        slTempo = new JSlider();
        slTempo.setMinimum(60);

        tbTempo = new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null);

        tglbtnLoop = new JToggleButton("Loop");
        panel.add(tglbtnLoop);

        slTempo = new JSlider();
        slTempo.setSnapToTicks(true);
        slTempo.setMinorTickSpacing(10);
        slTempo.setMajorTickSpacing(60);
        slTempo.setPaintLabels(true);
        slTempo.setPaintTicks(true);
        slTempo.setMinimum(60);

        tbTempo = new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null);

        slTempo.setBorder(tbTempo);
        slTempo.setMaximum(300);
        panel.add(slTempo);
        slTempo.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("Tempo: " + slTempo.getValue());
                for(int i= 0; i< measures.length; i++) {
                    if(measures[i] != null) {
                        measures[i].setTempo(slTempo.getValue());
                    }
                }
                //                measure.setTempo(slTempo.getValue());
                tbTempo.setTitle("Tempo: " + slTempo.getValue());;

            }
        });

        slVolume = new JSlider();
        slVolume.setMinorTickSpacing(5);
        slVolume.setPaintLabels(true);
        slVolume.setPaintTicks(true);
        slVolume.setSnapToTicks(true);
        slVolume.setMajorTickSpacing(20);
        tbVolume = new TitledBorder(null, "Volume", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        slVolume.setBorder(tbVolume);
        slVolume.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("Vol: " + slVolume.getValue());
                tbVolume.setTitle("Volume: " + slVolume.getValue());

            }
        });
        panel.add(slVolume);

        panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Instrument", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.add(panel_1);

        JComboBox<Instrument> cbInstr = new JComboBox();
        panel_1.add(cbInstr);
        cbInstr.setPreferredSize(new Dimension(200, 24));

        DefaultComboBoxModel<Instrument> model = new DefaultComboBoxModel(MidiEngine.getSynth().getAvailableInstruments());
        cbInstr.setModel(model);
        cbInstr.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                currentInstrument = cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram();

            }
        });
        cbInstr.setModel(new DefaultComboBoxModel(MidiEngine.getSynth().getAvailableInstruments()));
        cbInstr.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                currentInstrument = cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram();

            }
        });

        panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(null, "MIDI Channel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.add(panel_2);

        //        JPanel pnWest = new JPanel();
        //        add(pnWest, BorderLayout.WEST);
        //        pnWest.setLayout(new GridLayout(0, 1, 0, 0));
        //
        //        JPanel panel_1 = new JPanel();
        //        panel_1.setOpaque(false);
        //        panel_1.setMaximumSize(new Dimension(50, 32767));
        //        pnWest.add(panel_1);
        //        panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        cbChannel = new JComboBox();
        cbChannel.setPreferredSize(new Dimension(200, 24));
        panel_2.add(cbChannel);
        cbChannel.setModel(new DefaultComboBoxModel(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"}));

        panel_3 = new JPanel();
        panel_3.setBorder(new TitledBorder(null, "Arpeggio preset", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel.add(panel_3);

        cbArpeggio = new JComboBox();
        cbArpeggio.setPreferredSize(new Dimension(200, 24));
        cbArpeggio.setModel(new DefaultComboBoxModel(new String[] {"Preset 1 (1,2,3) 1/8", "Preset 1 (3,2,1) 1/8", "Preset 1 (1,2,3,2,1) 1/8", "Preset 1 (3,2,1,2,3) 1/8"}));
        panel_3.add(cbArpeggio);

        btnGenerateArp = new JButton("Generate arp.");
        panel.add(btnGenerateArp);

        btnGenerateArp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                generateArpeggio(cbArpeggio.getSelectedIndex());

            }

        });
    }
    private void generateArpeggio(int selectedIndex) {
        switch(selectedIndex) {
        case 0: {
            generatePreset1();
            break;
        }
        }

    }

    private void generatePreset1() {
        //        ArrayList<Note> newNotes = new ArrayList<>();
        //        int startTick = 0;
        //        for(int i = 0; i < 11; i++) {
        //            int pitchCount = measure.getNotes().size();
        //            for(int j = 0; j < pitchCount; j++) {
        //                Note n = new Note();
        //                n.setLength(NoteLength.NEGYED);
        //                n.setPitch(measure.getNotes().get(j).getPitch());
        //                n.setStartInTick(startTick);
        //                n.setVol(slVolume.getValue());
        //                newNotes.add(n);
        //                startTick += NoteLength.NEGYED.getErtek();
        //            }
        //        }
        //        measure.setNotes(newNotes);
        //        this.setMeasure(measure);
    }

    @Subscribe
    public void handleMeasureEvent(MeasureSelectedEvent ev) {

        measures[measureCount] = ev.getMeasure();
        measures[measureCount].setNum(measureCount);

        LOG.debug("measure event: count: {}, measure num: {}", measureCount, measures[measureCount].getRelativeNum());

        this.measureCount++;
        if(measureCount == 4) {
            measureCount = 0;
        }


        this.generateNotes();
    }

    @Subscribe
    public void handlePianoKeyEvent(PianoKeyEvent ev) {


        this.playNote(ev.getPitch());

    }

    private void generateNotes() {

        this.pnTickRows.removeAll();

        this.getOctaves().forEach(o -> {
            for(int i = 11; i >=0; i--) {
                Pitch pitch = new Pitch(NoteName.byCode(i).getMidiCode()).shift(o);
                this.pnTickRows.add(new TickRowPanel(pitch));
            }
        });

        for(int i = 0; i < this.measures.length; i++) {

            if(measures[i] != null) {
                Measure measure = measures[i];

                chckbxEnableAllPitches.setSelected(true);

                lblHangnem.setText(measure.getRoot().name() + " " + measure.getHangnem().name());

                slTempo.setValue(measure.getTempo());

                tbVolume.setTitle("Volume: " + slVolume.getValue());

                GridLayout gl = new GridLayout(0, 1);

                this.pnTickRows.setLayout(gl);

                this.pnCenter.add(new TickRowProgressBar(), BorderLayout.NORTH);


                for(Note note : measure.getNotes()) {

                    TickRowPanel trp = this.getTickRowByPitch(note.getPitch());

                    int startTick = measure.getRelativeNum() * (MidiEngine.TICKS_IN_MEASURE) + note.getStartInTick();

                    int endTick = startTick +  note.getLength().getErtek();

                    for(int j = startTick; j < endTick; j++) {
                        try {
                            trp.setSelectedTick(j);
                        }catch(Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }
        }
        this.validate();
    }

    private void setMeasure(Measure measure) {



    }

    private List<Integer> getOctaves() {

        List<Integer> octaves = new ArrayList<>();

        int min = 100;

        for(int i = 0; i < this.measures.length; i++) {
            if(measures[i] != null) {
                Measure m = this.measures[i];
                for(int j = 0; j < m.getNotes().size(); j++) {

                    Note curr = m.getNotes().get(j);

                    if(!octaves.contains(curr.getPitch().getOctave())) {
                        octaves.add(curr.getPitch().getOctave());
                        if(min > curr.getPitch().getOctave()) {
                            min = curr.getPitch().getOctave();
                        }
                    }
                }

            }

        }
        if(octaves.size() == 1) {
            octaves.add(octaves.get(0) - 1);
            octaves.add(octaves.get(0) + 1);
        } else {
            octaves.add(min-1);
        }
        Collections.sort(octaves);
        Collections.reverse(octaves);
        return octaves;
    }


    private void play(int instrument) {
        MidiChannel[] channels = MidiEngine.getSynth().getChannels();
        channels[MidiEngine.CHORD_CHANNEL].programChange(instrument);

        for(int i = 0; i < this.measures.length; i++) {
            if(this.measures[i] != null) {
                Measure m = this.measures[i];
                m.setNotes(getNotes(i));
                MidiEngine.playMeasure(m, channels[cbChannel.getSelectedIndex()]);

            }

        }

    }

    private List<Note> getNotes(int measureIndex){

        List<Note> retVal = new ArrayList<>();
        for(Component comp : pnTickRows.getComponents()) {
            TickRowPanel trp = (TickRowPanel) comp;
            retVal.addAll(trp.getNotes(measureIndex));
        }
        retVal.forEach(n -> n.setVol(slVolume.getValue()));
        return retVal;
    }

    private void setRowsEnabled(boolean selected) {

        if(selected) {
            this.generateNotes();
        } else {

            List<NoteName> scaleNotes = new ArrayList<>();
            Pitch[] scale = this.measures[0].getHangnem() == ChordType.MAJ ? Scale.majorScale(this.measures[0].getRoot()) : Scale.minorScale(this.measures[0].getRoot());
            for(Pitch p : scale) {
                scaleNotes.add(p.getName());
            }

            for(Component comp : pnTickRows.getComponents()) {
                TickRowPanel trp = (TickRowPanel) comp;
                if(!scaleNotes.contains(trp.getPitch().getName())) {
                    trp.setEnabled(selected);
                }
            }

        }
        this.validate();
    }


    private void changeOctave(int oct) {
        for(int i = 0; i < this.measures.length; i++) {
            if(measures[i] != null) {
                this.measures[i].getNotes().forEach(n -> {
                    n.setPitch(n.getPitch().shift(oct));
                });
            }
        }
        this.generateNotes();

    }

    private void playNote(Pitch pitch) {
        MidiChannel[] channels = MidiEngine.getSynth().getChannels();
        channels[MidiEngine.CHORD_CHANNEL].programChange(currentInstrument);


        Note note = new Note();
        note.setLength(NoteLength.NEGYED);
        note.setPitch(pitch);
        note.setStartInTick(0);
        note.setVol(slVolume.getValue());
        MidiEngine.playNote(note, channels[cbChannel.getSelectedIndex()], slTempo.getValue());

    }

    private TickRowPanel getTickRowByPitch(Pitch pitch) {
        for(int i = 0; i < this.pnTickRows.getComponentCount(); i++) {
            TickRowPanel trp = (TickRowPanel) this.pnTickRows.getComponent(i);
            if(trp.getPitch().equals(pitch)) {
                return trp;
            }
        }
        return null;
    }



}
