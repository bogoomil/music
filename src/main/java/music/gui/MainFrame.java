package music.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Track;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import music.App;
import music.event.ChordEvent;
import music.event.EventListener;
import music.logic.Player;
import music.theory.Chord;
import music.theory.ChordDegree;
import music.theory.ChordType;
import music.theory.Measure;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;
import music.theory.Scale;

public class MainFrame extends JFrame implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);


    private JComboBox<String> cbOctave, cbRoot, cbMinMaj;

    private JPanel centerPanel;
    private JPanel panelRecord;

    public static final EventBus eventBus = new EventBus();

    private JPanel panel;
    private JTextField tfTempo;
    private JComboBox<NoteLength> cbArpeggio;
    private JPanel panel_1;
    private JComboBox<NoteLength> cbChordLength;
    private JPanel panel_5;
    private JComboBox<Instrument> cbInstr;

    private List<ChordPanel> generatedChordPanels;
    private JButton btnPlayRecorded;
    private JPanel panel_6;
    private JButton btnDel;
    private JCheckBox chckbxArp;
    private JButton btnStop;
    private JToggleButton tglbtnRec;
    private JTabbedPane tabbedPane;
    private JPanel pnChords;
    private JPanel pnProject;

    private Color defaultColor = this.getBackground();


    ProjectEditorPanel pep = new ProjectEditorPanel();


    private Pitch[] scale;

    public MainFrame(String title) throws HeadlessException {
        super(title);

        this.addWindowStateListener(new WindowStateListener() {

            @Override
            public void windowStateChanged(WindowEvent arg0) {

            }
        });

        eventBus.register(this);

        setTitle("MusicApp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(1200, 700));
        //        getContentPane().add(centerPanel, BorderLayout.WEST);
        centerPanel.setLayout(new GridLayout(0, 1, 3, 3));

        panelRecord = new JPanel();
        getContentPane().add(panelRecord, BorderLayout.SOUTH);
        panelRecord.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        pnChords = new JPanel();
        tabbedPane.addTab("Chords", null, pnChords, null);
        pnChords.setLayout(new BorderLayout(0, 0));
        // this.setPreferredSize(new Dimension(500, 400));

        pnChords.add(centerPanel, BorderLayout.CENTER);

        JPanel northPanel = new JPanel();
        pnChords.add(northPanel, BorderLayout.NORTH);
        northPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JPanel panel_2 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        panel_2.setBorder(new TitledBorder(null, "Key", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        northPanel.add(panel_2);

        cbRoot = new JComboBox<>();
        cbRoot.setModel(new DefaultComboBoxModel(NoteName.values()));
        panel_2.add(cbRoot);
        cbRoot.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                generateChords();

            }
        });

        cbMinMaj = new JComboBox<>();
        cbMinMaj.setModel(new DefaultComboBoxModel(new String[] { "MAJ", "MIN" }));
        panel_2.add(cbMinMaj);
        cbMinMaj.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                generateChords();

            }
        });

        JPanel panel_3 = new JPanel();
        panel_3.setBorder(new TitledBorder(null, "Octave", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        northPanel.add(panel_3);

        cbOctave = new JComboBox<>();
        cbOctave.setModel(new DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" }));
        cbOctave.setSelectedIndex(4);
        panel_3.add(cbOctave);
        cbOctave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                generateChords();

            }
        });

        panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "ChordLength", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        northPanel.add(panel_1);

        cbChordLength = new JComboBox();
        cbChordLength.setModel(new DefaultComboBoxModel(NoteLength.values()));
        panel_1.add(cbChordLength);
        cbChordLength.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                generateChords();

            }
        });

        JPanel panel_4 = new JPanel();
        northPanel.add(panel_4);
        panel_4.setBorder(new TitledBorder(null, "Arpeggio", TitledBorder.LEADING, TitledBorder.TOP, null, null));

        chckbxArp = new JCheckBox("Arp");
        chckbxArp.setSelected(true);
        panel_4.add(chckbxArp);
        chckbxArp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                cbArpeggio.setEnabled(chckbxArp.isSelected());
                generateChords();

            }
        });

        cbArpeggio = new JComboBox();
        panel_4.add(cbArpeggio);
        cbArpeggio.setModel(new DefaultComboBoxModel(NoteLength.values()));
        cbArpeggio.setSelectedIndex(4);
        cbArpeggio.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                generateChords();

            }

        });

        panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        northPanel.add(panel);

        tfTempo = new JTextField();
        tfTempo.setText("120");
        panel.add(tfTempo);
        tfTempo.setColumns(10);

        tfTempo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                App.setTEMPO(Integer.valueOf(tfTempo.getText()));
            }
        });

        panel_5 = new JPanel();
        panel_5.setBorder(new TitledBorder(null, "Instrument", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        northPanel.add(panel_5);

        cbInstr = new JComboBox();
        Instrument[] instrs = Player.getSynth().getAvailableInstruments();
        cbInstr.setModel(new DefaultComboBoxModel(instrs));
        cbInstr.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                ChordPanel.setInstrument(cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram());
                // TODO Auto-generated method stub

            }
        });
        panel_5.add(cbInstr);

        panel_6 = new JPanel();
        getContentPane().add(panel_6, BorderLayout.NORTH);

        tglbtnRec = new JToggleButton("Rec");
        panel_6.add(tglbtnRec);

        btnPlayRecorded = new JButton("Play");
        panel_6.add(btnPlayRecorded);

        btnStop = new JButton("Stop");
        panel_6.add(btnStop);

        btnStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                // TODO Auto-generated method stub

            }
        });

        btnDel = new JButton("Del");
        panel_6.add(btnDel);

        pnProject = new JPanel();
        tabbedPane.addTab("Project", null, pnProject, null);
        pnProject.setLayout(new BorderLayout());

        pnProject.add(pep, BorderLayout.CENTER);



        btnPlayRecorded.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    play();
                } catch (MidiUnavailableException | InvalidMidiDataException | InterruptedException | IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                panelRecord.removeAll();
                panelRecord.repaint();

            }
        });
        this.setVisible(true);

        this.generateChords();
    }



    private void generateChords() {

        this.generatedChordPanels = new ArrayList<>();

        String minMaj = this.cbMinMaj.getSelectedItem().toString();

        List<ChordDegree> degrees = Arrays.asList(ChordDegree.values());

        ChordType chordType = ChordType.valueOf(minMaj);

        scale = chordType == ChordType.MAJ ? Scale.majorScale(getRootKey().getName()) : Scale.minorScale(getRootKey().getName()) ;

        Arrays.asList(scale).forEach(s -> {
            LOG.debug("pitch: {}", s);
        });

        this.centerPanel.removeAll();

        for (ChordDegree deg : degrees) {

            JPanel jp = new JPanel();
            jp.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            this.centerPanel.add(jp);

            JLabel lbDeg = new JLabel(deg.name());
            lbDeg.setHorizontalAlignment(JLabel.CENTER);
            lbDeg.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            jp.add(lbDeg);



            Chord c = Chord.getChordDegree(getRootKey(), chordType, deg);


            NoteLength arpOffset = chckbxArp.isSelected() ? cbArpeggio.getItemAt(cbArpeggio.getSelectedIndex()) : null;

            ChordPanel cp = new ChordPanel(Player.getSynth(), c, deg, cbChordLength.getItemAt(cbChordLength.getSelectedIndex()), arpOffset, getRootKey().getName(), chordType, this);
            this.generatedChordPanels.add(cp);
            jp.add(cp);

            List<Chord> subs = Chord.getChordsOfPitch(c.getPitches()[0]);
            for (Chord s : subs) {

                if(s.equals(c)) {
                    continue;
                }

                cp = new ChordPanel(Player.getSynth(), s, deg, cbChordLength.getItemAt(cbChordLength.getSelectedIndex()), arpOffset, getRootKey().getName(), chordType, this);
                this.generatedChordPanels.add(cp);
                jp.add(cp);

            }
        }
        this.resetColor();
        this.pack();
    }

    @Override
    public void chordEvent(ChordEvent event) {
        //        LOG.debug("CHORD EVENT #################################");
        //        LOG.debug("chord: {}", event.getChord());
        //        LOG.debug("root key: {}", getRootKey());

        this.resetColor();

        String minMaj = this.cbMinMaj.getSelectedItem().toString();
        ChordDegree deg = event.getDegree();

        List<ChordDegree> possibleDegrees = Chord.getPossibleDegrees(deg, ChordType.valueOf(minMaj));

        for (ChordDegree cd : possibleDegrees) {
            List<Chord> progr = Chord.getChordProgressions(cd, getRootKey(), ChordType.valueOf(minMaj));

            for (Chord pro : progr) {
                for (ChordPanel pn : this.generatedChordPanels) {

                    if(pn.getChord().equals(pro)) {
                        pn.setColor(Color.GREEN);
                    }
                }
            }
        }

        if(tglbtnRec.isSelected()) {
            if (panelRecord.getComponentCount() == 16) {
                panelRecord.remove(0);
            }
            NoteLength arpOffset = chckbxArp.isSelected() ? cbArpeggio.getItemAt(cbArpeggio.getSelectedIndex()) : null;

            panelRecord.add(new ChordPanel(Player.getSynth(), event.getChord(), null, cbChordLength.getItemAt(cbChordLength.getSelectedIndex()), arpOffset, getRootKey().getName(), ChordType.valueOf(minMaj), null));
            this.pack();

        }


    }

    private void resetColor() {
        for(ChordPanel cp : this.generatedChordPanels) {
            cp.setColor(defaultColor);
        }



    }

    private Pitch getRootKey() {
        String root = this.cbRoot.getSelectedItem().toString();
        String octave = this.cbOctave.getSelectedItem().toString();
        Pitch rootKey = new Pitch(NoteName.valueOf(root).getMidiCode()).shift(Integer.valueOf(octave));

        return rootKey;
    }

    private void play() throws MidiUnavailableException, InvalidMidiDataException, InterruptedException, IOException {

        Track chordTrack = Player.getInstrumentTrack(Player.CHORD_CHANNEL, cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram());
        Measure m = null;

        LOG.debug("CHORD ==================================================");
        for(int i = 0; i < this.panelRecord.getComponents().length; i++) {


            ChordPanel cp = (ChordPanel) this.panelRecord.getComponent(i);
            m = new Measure(i, App.getTEMPO(), getRootKey().getName(), ChordType.valueOf(cbMinMaj.getItemAt(cbMinMaj.getSelectedIndex())));


            ChordDegree deg = cp.getDegree();
            Chord c = cp.getChord();
            LOG.debug("chord: {}, degree: {}", c, deg);

            Measure measure = Measure.createMeasureFromChord(i, c, cp.getChordLength(), cp.getArpeggioOffset(), getRootKey().getName(), ChordType.valueOf(cbMinMaj.getItemAt(cbMinMaj.getSelectedIndex())));
            Player.addNotesToTrack(chordTrack, Player.CHORD_CHANNEL, measure);

        }

        Player.getSequencer().start();
        File f = new File("piece.mid");
        LOG.debug("creating midi file: {}", f.getAbsolutePath());
        MidiSystem.write(Player.getSequencer().getSequence(),1,f);
    }

}
