package music.gui.chords;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.MidiChannel;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.ChordEvent;
import music.gui.InstrumentCombo;
import music.gui.NoteLengthCombo;
import music.gui.TempoSlider;
import music.logic.MidiEngine;
import music.theory.Chord;
import music.theory.ChordDegree;
import music.theory.ChordType;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;

public class ChordsPanel extends JPanel{
    private JComboBox<String> cbOctave, cbRoot, cbMinMaj;

    private JPanel centerPanel;

    private JPanel panel_5;
    private InstrumentCombo cbInstr = new InstrumentCombo();

    private static final NoteLengthCombo cbNoteLength = new NoteLengthCombo();
    private static final TempoSlider tempoSlider = new TempoSlider();

    private List<ChordPanel> generatedChordPanels;
    private JPanel pnChords;

    private Color defaultColor = this.getBackground();
    private JPanel panel;
    private JPanel panel_1;

    public ChordsPanel() {

        App.eventBus.register(this);

        this.setLayout(new BorderLayout());


        centerPanel = new JPanel();
        //centerPanel.setPreferredSize(new Dimension(1800, 700));
        //        getContentPane().add(centerPanel, BorderLayout.WEST);
        centerPanel.setLayout(new GridLayout(1, 0, 3, 3));

        //        JScrollPane scroll = new JScrollPane(centerPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        pnChords = new JPanel();
        this.add(pnChords, BorderLayout.CENTER);
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
        cbOctave.setSelectedIndex(5);
        panel_3.add(cbOctave);

        cbOctave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                generateChords();

            }
        });

        panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "NoteLength", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        northPanel.add(panel);
        panel.add(cbNoteLength);
        cbNoteLength.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ChordPanel.setNoteLength(cbNoteLength.getItemAt(cbNoteLength.getSelectedIndex()));
                // TODO Auto-generated method stub

            }
        });

        panel_1 = new JPanel();
        northPanel.add(panel_1);

        TempoSlider tempoSlider = new TempoSlider();
        panel_1.add(tempoSlider);

        panel_5 = new JPanel();
        panel_5.setBorder(new TitledBorder(null, "Instrument", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        northPanel.add(panel_5);

        cbInstr.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                ChordPanel.setInstrument(cbInstr.getProgram());
                // TODO Auto-generated method stub

            }
        });
        panel_5.add(cbInstr);
        ChordPanel.setInstrument(cbInstr.getProgram());

        this.setVisible(true);

        this.generateChords();

    }
    private void generateChords() {

        this.generatedChordPanels = new ArrayList<>();

        String minMaj = this.cbMinMaj.getSelectedItem().toString();

        List<ChordDegree> degrees = Arrays.asList(ChordDegree.values());

        ChordType chordType = ChordType.valueOf(minMaj);


        this.centerPanel.removeAll();

        for (ChordDegree deg : degrees) {

            JPanel jp = new JPanel();
            jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
            this.centerPanel.add(jp);

            JLabel lbDeg = new JLabel(deg.name());
            lbDeg.setHorizontalAlignment(JLabel.CENTER);
            lbDeg.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            jp.add(lbDeg);



            Chord c = Chord.getChordDegree(getRootKey(), chordType, deg);


            ChordPanel cp = new ChordPanel(c, deg, NoteLength.EGESZ, null, getRootKey().getName(), chordType);
            this.generatedChordPanels.add(cp);
            jp.add(cp);

            List<Chord> subs = Chord.getChordsOfPitch(c.getPitches()[0]);

            int subCounter = 0;
            for (Chord s : subs) {
                if(s.equals(c)) {
                    continue;
                }
                cp = new ChordPanel(s, deg, NoteLength.EGESZ, null, getRootKey().getName(), chordType);
                this.generatedChordPanels.add(cp);
                jp.add(cp);
                subCounter++;
            }
        }
        this.resetColor();
        this.repaint();
        this.validate();
    }

    @Subscribe
    public void chordEvent(ChordEvent event) {

        this.resetColor();

        String minMaj = this.cbMinMaj.getSelectedItem().toString();
        ChordDegree deg = event.getDegree();

        if(deg != null) {
            List<ChordDegree> possibleDegrees = Chord.getPossibleDegrees(deg, ChordType.valueOf(minMaj));

            for (ChordDegree cd : possibleDegrees) {
                List<Chord> progr = Chord.getChordProgressions(cd, getRootKey(), ChordType.valueOf(minMaj));

                for (Chord pro : progr) {
                    for (ChordPanel pn : this.generatedChordPanels) {

                        if(pn.getChord().equals(pro)) {
                            pn.setColor(App.GREEN);
                        }
                    }
                }
            }

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

    public static void playChord(Chord chord, int instrument) {

        MidiChannel[] channels = MidiEngine.getSynth().getChannels();
        channels[0].programChange(instrument);
        int counter = 0;
        for(Pitch p : chord.getPitches()) {
            Note n = new Note();
            n.setPitch(p);
            n.setLength(cbNoteLength.getItemAt(cbNoteLength.getSelectedIndex()));
            MidiEngine.playNote(n, channels[0], tempoSlider.getValue());
            counter++;
        }
    }


}
