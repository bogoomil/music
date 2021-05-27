package music.gui.measureeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
    private Measure measure;
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

    public MeasureEditorPanel() {
        super();
        UIManager.put("ToggleButton.select", new ColorUIResource( Color.RED ));

        MainFrame.eventBus.register(this);

        this.setLayout(new BorderLayout());

        this.add(pnCenter, BorderLayout.CENTER);

        pnCenter.add(pnTickRows, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.NORTH);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

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
                measure.setNotes(new ArrayList<Note>());
                Note n = new Note();
                n.setLength(NoteLength.HARMICKETTED);
                n.setPitch(new Pitch(NoteName.C.getMidiCode()).shift(4));
                n.setStartInTick(0);

                measure.addNote(n);
                setMeasure(measure);

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

        JComboBox<Instrument> cbInstr = new JComboBox();
        cbInstr.setModel(new DefaultComboBoxModel(MidiEngine.getSynth().getAvailableInstruments()));
        cbInstr.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                currentInstrument = cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram();

            }
        });

        slTempo = new JSlider();
        slTempo.setMinimum(60);

        tbTempo = new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null);

        tglbtnLoop = new JToggleButton("Loop");
        panel.add(tglbtnLoop);
        cbInstr.setModel(new DefaultComboBoxModel(MidiEngine.getSynth().getAvailableInstruments()));
        cbInstr.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                currentInstrument = cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram();

            }
        });

        slTempo = new JSlider();
        slTempo.setMinimum(60);

        tbTempo = new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null);

        slTempo.setBorder(tbTempo);
        slTempo.setMaximum(300);
        panel.add(slTempo);
        slTempo.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                measure.setTempo(slTempo.getValue());
                tbTempo.setTitle("Tempo: " + slTempo.getValue());;

            }
        });

        slVolume = new JSlider();
        tbVolume = new TitledBorder(null, "Volume", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        slVolume.setBorder(tbVolume);
        slVolume.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tbVolume.setTitle("Volume: " + slVolume.getValue());

            }
        });
        panel.add(slVolume);


        panel.add(cbInstr);

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
        cbChannel.setBorder(new TitledBorder(null, "MIDI ch", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        cbChannel.setModel(new DefaultComboBoxModel(new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"}));
        panel.add(cbChannel);




    }

    @Subscribe
    public void handleMeasureEvent(MeasureSelectedEvent ev) {


        this.setMeasure(ev.getMeasure());


    }

    @Subscribe
    public void handlePianoKeyEvent(PianoKeyEvent ev) {


        this.playNote(ev.getPitch());

    }

    private void setMeasure(Measure measure) {
        this.measure = measure;

        chckbxEnableAllPitches.setSelected(true);

        lblHangnem.setText(measure.getRoot().name() + " " + measure.getHangnem().name());

        slTempo.setValue(measure.getTempo());

        tbVolume.setTitle("Volume: " + slVolume.getValue());

        int tickCount = MidiEngine.RESOLUTION  * 4 * VISIBLE_MEASURE_COUNT;

        GridLayout gl = new GridLayout(0, 1);

        this.pnTickRows.setLayout(gl);

        this.pnTickRows.removeAll();

        this.getOctaves(measure).forEach(o -> {
            for(int i = 11; i >=0; i--) {

                Pitch pitch = new Pitch(NoteName.byCode(i).getMidiCode()).shift(o);
                this.pnTickRows.add(new TickRowPanel(pitch));


            }

        });

        for(Note note : measure.getNotes()) {
            for(Component comp : this.pnTickRows.getComponents()) {
                TickRowPanel trp = (TickRowPanel) comp;
                if(trp.getPitch().equals(note.getPitch())) {
                    int endTick = note.getStartInTick() + ( MidiEngine.TICKS_IN_MEASURE * (measure.getNum() + 1) / note.getLength().getErtek());

                    for(int i = note.getStartInTick(); i < endTick; i++) {
                        trp.setSelectedTick(i);
                    }

                }
            }

        }

        this.validate();


    }

    private List<Integer> getOctaves(Measure measure) {

        List<Integer> octaves = new ArrayList<>();

        int min = 100;
        for(int i = 0; i < measure.getNotes().size(); i++) {

            Note curr = measure.getNotes().get(i);

            if(!octaves.contains(curr.getPitch().getOctave())) {
                octaves.add(curr.getPitch().getOctave());
                if(min > curr.getPitch().getOctave()) {
                    min = curr.getPitch().getOctave();
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
        this.measure.setNotes(getNotes());
        MidiEngine.playMeasure(this.measure, channels[cbChannel.getSelectedIndex()]);
    }

    private List<Note> getNotes(){
        List<Note> retVal = new ArrayList<>();
        for(Component comp : pnTickRows.getComponents()) {
            TickRowPanel trp = (TickRowPanel) comp;
            retVal.addAll(trp.getNotes());
        }
        retVal.forEach(n -> n.setVol(slVolume.getValue()));
        return retVal;
    }

    private void setRowsEnabled(boolean selected) {

        if(selected) {
            this.setMeasure(this.measure);
        } else {
            List<NoteName> scaleNotes = new ArrayList<>();
            Pitch[] scale = this.measure.getHangnem() == ChordType.MAJ ? Scale.majorScale(this.measure.getRoot()) : Scale.minorScale(this.measure.getRoot());
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


    private void changeOctave(int i) {
        this.measure.getNotes().forEach(n -> {
            n.setPitch(n.getPitch().shift(i));
        });
        this.setMeasure(this.measure);

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


}
