package music.gui.chords;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.Synthesizer;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import music.App;
import music.event.ChordEvent;
import music.event.MeasureSelectedEvent;
import music.event.tracks.AddNotesToTrackEvent;
import music.logic.MidiEngine;
import music.theory.Chord;
import music.theory.ChordDegree;
import music.theory.ChordType;
import music.theory.Measure;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;

public class ChordPanel extends JPanel {

    private Chord chord;
    private ChordDegree degree;
    private NoteLength chordLength;
    private NoteLength arpeggioOffset;

    private JButton btnPlay;
    private JButton btnPlayPlus;
    private JPanel pnOctaves;
    private JPanel pnSubstitutions;
    private JButton btnAddToTrack;

    private static int instrument = 0;



    /**
     *
     * @param synth
     * @param chord
     * @param deg
     * @param chordLength
     * @param arpeggioOffset
     * @param root
     * @param hangnem
     * @param listener
     */
    public ChordPanel(Synthesizer synth, Chord chord, ChordDegree deg, NoteLength chordLength, NoteLength arpeggioOffset, NoteName root, ChordType hangnem) {
        this.chord = chord;
        this.degree = deg;
        this.chordLength = chordLength;
        this.arpeggioOffset = arpeggioOffset;
        setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        setLayout(new GridLayout(3, 0, 0, 0));
        pnSubstitutions = new JPanel();
        FlowLayout flowLayout = (FlowLayout) pnSubstitutions.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);


        Optional<Chord> counter = Chord.getChordCounterPart(chord);
        counter.ifPresent(cntr -> {
            JButton btnCounter = new JButton("c");
            btnCounter.setFont(new Font("Arial", Font.PLAIN, 8));
            btnCounter.setMargin(new Insets(0, 3, 0, 3));
            pnSubstitutions.add(btnCounter);
            btnCounter.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Measure measure = Measure.createMeasureFromChord(0, counter.get(), getChordLength(), getArpeggioOffset(), root, hangnem);
                    MeasureSelectedEvent ev = new MeasureSelectedEvent(measure);

                    playChord(synth, counter.get(), instrument);

                    App.eventBus.post(new ChordEvent(counter.get(), degree));

                }
            });

        });
        btnPlay = new JButton("" + chord);
        btnPlay.setFont(new Font("Arial", Font.PLAIN, 8));
        add(btnPlay);
        btnPlay.setMargin(new Insets(0, 0, 0, 0));
        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                playChord(synth, chord, instrument);

                Measure measure = Measure.createMeasureFromChord(0, chord, getChordLength(), getArpeggioOffset(), root, hangnem);
                MeasureSelectedEvent ev = new MeasureSelectedEvent(measure);

                App.eventBus.post(ev);

                App.eventBus.post(new ChordEvent(chord, degree));
            }
        });
        btnAddToTrack = new JButton("ADD");
        btnAddToTrack.setFont(new Font("Arial", Font.PLAIN, 8));
        pnSubstitutions.add(btnAddToTrack);
        btnAddToTrack.setMargin(new Insets(0, 3, 0, 3));
        btnAddToTrack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                playChord(synth, chord, instrument);

                Note[] notes = new Note[chord.getPitches().length];
                for(int i = 0; i < chord.getPitches().length; i++) {
                    notes[i] = new Note();
                    notes[i].setLength(NoteLength.EGESZ);
                    notes[i].setPitch(chord.getPitches()[i]);
                    notes[i].setStartTick(0);
                }
                App.eventBus.post(new AddNotesToTrackEvent(notes));

            }
        });

        pnOctaves = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) pnOctaves.getLayout();
        flowLayout_1.setAlignment(FlowLayout.LEFT);
        add(pnOctaves);

        JButton btnPlayMinus = new JButton("-o");
        btnPlayMinus.setFont(new Font("Arial", Font.PLAIN, 8));
        pnOctaves.add(btnPlayMinus);
        btnPlayMinus.setMargin(new Insets(0, 0, 0, 0));
        btnPlayPlus = new JButton("+o");
        btnPlayPlus.setFont(new Font("Arial", Font.PLAIN, 8));
        pnOctaves.add(btnPlayPlus);
        btnPlayPlus.setMargin(new Insets(0, 0, 0, 0));

        btnPlayPlus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Chord plusOctave = chord.shiftOctave(1);

                Measure measure = Measure.createMeasureFromChord(0, plusOctave, getChordLength(), getArpeggioOffset(), root, hangnem);
                App.eventBus.post(new MeasureSelectedEvent(measure));
                App.eventBus.post(new ChordEvent(plusOctave, degree));

                playChord(synth, plusOctave, instrument);
            }
        });
        btnPlayMinus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Chord minusOctave = chord.shiftOctave(-1);

                Measure measure = Measure.createMeasureFromChord(0, minusOctave, getChordLength(), getArpeggioOffset(), root, hangnem);
                App.eventBus.post(new MeasureSelectedEvent(measure));
                App.eventBus.post(new ChordEvent(minusOctave, degree));
                playChord(synth, minusOctave, instrument);
            }
        });

        add(pnSubstitutions);

        JButton btnInversion = new JButton("i");
        btnInversion.setFont(new Font("Arial", Font.PLAIN, 8));
        pnSubstitutions.add(btnInversion);
        btnInversion.setMargin(new Insets(0, 3, 0, 3));

        btnInversion.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Chord inv = Chord.getChordInversion(chord);
                Measure measure = Measure.createMeasureFromChord(0, inv, getChordLength(), getArpeggioOffset(), root, hangnem);
                App.eventBus.post(new MeasureSelectedEvent(measure));
                App.eventBus.post(new ChordEvent(inv, degree));

                playChord(synth, inv, instrument);

            }
        });
    }

    public Chord getChord() {
        return chord;
    }

    public ChordDegree getDegree() {
        return degree;
    }

    public void setColor(Color color) {
        btnPlay.setBackground(color);

    }

    public static int getInstrument() {
        return instrument;
    }

    public static void setInstrument(int instrument) {
        ChordPanel.instrument = instrument;
    }


    public NoteLength getChordLength() {
        return chordLength;
    }

    public void setChordLength(NoteLength chordLength) {
        this.chordLength = chordLength;
    }

    public NoteLength getArpeggioOffset() {
        return arpeggioOffset;
    }

    public void setArpeggioOffset(NoteLength arpeggioOffset) {
        this.arpeggioOffset = arpeggioOffset;
    }

    public static void playChord(Synthesizer synth, Chord chord, int instrument) {

        MidiChannel[] channels = synth.getChannels();
        channels[0].programChange(instrument);
        int counter = 0;
        for(Pitch p : chord.getPitches()) {
            Note n = new Note();
            n.setPitch(p);
            n.setLength(NoteLength.EGESZ);
            MidiEngine.playNote(n, channels[0], App.getTEMPO());
            counter++;
        }
    }

}
