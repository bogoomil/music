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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import music.event.ChordEvent;
import music.event.EventListener;
import music.event.PianoKeyEvent;
import music.event.PlayEvent;
import music.logic.MidiEngine;
import music.theory.Chord;
import music.theory.ChordDegree;
import music.theory.ChordType;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;

public class MainFrame extends JFrame implements EventListener {

    //private static final Logger LOG = LoggerFactory.getLogger(MainFrame.class);


    private JComboBox<String> cbOctave, cbRoot, cbMinMaj;

    private JPanel centerPanel;
    private JPanel panelRecord;

    public static final EventBus eventBus = new EventBus();

    private JPanel panel;
    private JComboBox<NoteLength> cbArpeggio;
    private JPanel panel_1;
    private JComboBox<NoteLength> cbChordLength;
    private JPanel panel_5;
    private JComboBox<Instrument> cbInstr;

    private List<ChordPanel> generatedChordPanels;
    private JButton btnPlayRecorded;
    private JPanel panel_6;
    private JCheckBox chckbxArp;
    private JButton btnStop;
    private JToggleButton tglbtnRec;
    private JTabbedPane tabbedPane;
    private JPanel pnChords;
    private JPanel pnProject;

    private Color defaultColor = this.getBackground();

    ProjectEditorPanel pep2 = new ProjectEditorPanel();


    public MainFrame(String title) throws HeadlessException {
        super(title);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                MidiEngine.getSynth().close();

            }
        });
        eventBus.register(this);

        setTitle("MusicApp");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(1800, 700));
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
        chckbxArp.setSelected(false);
        panel_4.add(chckbxArp);
        chckbxArp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                cbArpeggio.setEnabled(chckbxArp.isSelected());
                generateChords();

            }
        });

        cbArpeggio = new JComboBox<>();
        panel_4.add(cbArpeggio);
        cbArpeggio.setModel(new DefaultComboBoxModel(NoteLength.values()));
        cbArpeggio.setSelectedIndex(4);
        cbArpeggio.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                generateChords();

            }

        });
        cbArpeggio.setEnabled(false);

        panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        northPanel.add(panel);


        panel_5 = new JPanel();
        panel_5.setBorder(new TitledBorder(null, "Instrument", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        northPanel.add(panel_5);

        cbInstr = new JComboBox();
        Instrument[] instrs = MidiEngine.getSynth().getAvailableInstruments();
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
                MidiEngine.getSequencer().stop();

            }
        });

        pnProject = new JPanel();
        tabbedPane.addTab("Project", null, pnProject, null);
        pnProject.setLayout(new BorderLayout());

        pnProject.add(pep2, BorderLayout.CENTER);



        btnPlayRecorded.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                eventBus.post(new PlayEvent());
            }
        });

        this.setVisible(true);

        this.generateChords();

        cbChordLength.setSelectedIndex(3);
        cbArpeggio.setSelectedIndex(7);

    }



    private void generateChords() {

        this.generatedChordPanels = new ArrayList<>();

        String minMaj = this.cbMinMaj.getSelectedItem().toString();

        List<ChordDegree> degrees = Arrays.asList(ChordDegree.values());

        ChordType chordType = ChordType.valueOf(minMaj);


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

            ChordPanel cp = new ChordPanel(MidiEngine.getSynth(), c, deg, cbChordLength.getItemAt(cbChordLength.getSelectedIndex()), arpOffset, getRootKey().getName(), chordType, this);
            this.generatedChordPanels.add(cp);
            jp.add(cp);

            List<Chord> subs = Chord.getChordsOfPitch(c.getPitches()[0]);
            for (Chord s : subs) {

                if(s.equals(c)) {
                    continue;
                }

                cp = new ChordPanel(MidiEngine.getSynth(), s, deg, cbChordLength.getItemAt(cbChordLength.getSelectedIndex()), arpOffset, getRootKey().getName(), chordType, this);
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

            panelRecord.add(new ChordPanel(MidiEngine.getSynth(), event.getChord(), null, cbChordLength.getItemAt(cbChordLength.getSelectedIndex()), arpOffset, getRootKey().getName(), ChordType.valueOf(minMaj), null));
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

    @Subscribe
    private void handlePianoKeyEvent(PianoKeyEvent e) {
        MidiChannel[] channels = MidiEngine.getSynth().getChannels();
        channels[0].programChange(cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram());

        Note n = new Note();
        n.setPitch(e.getPitch());
        n.setLength(NoteLength.NEGYED);
        n.setStartTick(0);

        MidiEngine.playNote(0, n, channels[0], 120);


    }

}
