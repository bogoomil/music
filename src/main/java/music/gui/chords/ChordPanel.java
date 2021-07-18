package music.gui.chords;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import music.App;
import music.event.AddNotesToTrackEvent;
import music.event.ChordEvent;
import music.theory.Chord;
import music.theory.ChordDegree;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Tone;

public class ChordPanel extends JPanel {

    private Chord chord;
    private ChordDegree degree;
    private NoteLength chordLength;
    private NoteLength arpeggioOffset;

    private JButton btnPlay;
    private JButton btnPlayPlus;
    //private JPanel pnOctaves;
    private JPanel pnSubstitutions;
    private JButton btnAddToTrack;

    private static int instrument = 0;

    private static boolean isRecording;

    //private static NoteLength noteLength = NoteLength.EGESZ;

    public static boolean isRecording() {
        return isRecording;
    }

    public static void setRecording(boolean isRecording) {
        ChordPanel.isRecording = isRecording;
    }

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
    public ChordPanel(Chord chord, ChordDegree deg, NoteLength chordLength, NoteLength arpeggioOffset, NoteName root, Tone hangnem) {
        this.chord = chord;
        this.degree = deg;
        this.chordLength = chordLength;
        this.arpeggioOffset = arpeggioOffset;
        setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new LineBorder(new Color(0, 0, 128), 3, true)));
        setLayout(new GridLayout(2, 0, 0, 0));

        JPanel pnOctaves = new JPanel();
        pnOctaves.setLayout(new BorderLayout());

        pnSubstitutions = new JPanel();

        pnSubstitutions.setLayout(new GridLayout(1, 2));

        JButton btnPlayMinus = new JButton("-o");
        btnPlayMinus.setFont(new Font("Arial", Font.PLAIN, 8));
        pnOctaves.add(btnPlayMinus, BorderLayout.WEST);
        btnPlayMinus.setMargin(new Insets(0, 0, 0, 0));
        btnPlayMinus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Chord minusOctave = chord.shiftOctave(-1);
                App.eventBus.post(new ChordEvent(minusOctave, degree));
                ChordsPanel.playChord(minusOctave, instrument);
                if(isRecording) {
                    sendRecordingEvent(minusOctave);
                }

            }
        });


        btnPlay = new JButton("" + chord);
        btnPlay.setFont(new Font("Arial", Font.PLAIN, 8));
        pnOctaves.add(btnPlay, BorderLayout.CENTER);
        btnPlay.setMargin(new Insets(0, 0, 0, 0));
        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                ChordsPanel.playChord(chord, instrument);
                App.eventBus.post(new ChordEvent(chord, degree));

                if(isRecording) {
                    sendRecordingEvent(chord);
                }

            }
        });


        btnPlayPlus = new JButton("+o");
        btnPlayPlus.setFont(new Font("Arial", Font.PLAIN, 8));
        pnOctaves.add(btnPlayPlus, BorderLayout.EAST);
        btnPlayPlus.setMargin(new Insets(0, 0, 0, 0));

        btnPlayPlus.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                Chord plusOctave = chord.shiftOctave(1);
                App.eventBus.post(new ChordEvent(plusOctave, degree));
                ChordsPanel.playChord(plusOctave, instrument);
                if(isRecording) {
                    sendRecordingEvent(plusOctave);
                }
            }
        });

        add(pnOctaves);
        add(pnSubstitutions);

        Chord inv = Chord.getChordInversion(chord);
        JButton btnInversion = new JButton("(i) " + inv);
        btnInversion.setFont(new Font("Arial", Font.PLAIN, 8));
        pnSubstitutions.add(btnInversion);
        btnInversion.setMargin(new Insets(0, 3, 0, 3));

        btnInversion.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                App.eventBus.post(new ChordEvent(inv, degree));

                ChordsPanel.playChord(inv, instrument);
                if(isRecording) {
                    sendRecordingEvent(inv);
                }

            }
        });
        Optional<Chord> counter = Chord.getChordCounterPart(chord);
        counter.ifPresent(cntr -> {
            JButton btnCounter = new JButton("(c) " + counter.get());
            btnCounter.setFont(new Font("Arial", Font.PLAIN, 8));
            btnCounter.setMargin(new Insets(0, 3, 0, 3));
            pnSubstitutions.add(btnCounter);
            btnCounter.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    ChordsPanel.playChord(counter.get(), instrument);
                    App.eventBus.post(new ChordEvent(counter.get(), degree));
                    if(isRecording) {
                        sendRecordingEvent(counter.get());
                    }
                }
            });

        });
        App.eventBus.register(this);

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

    private void sendRecordingEvent(Chord chord) {
        Note[] notes = new Note[chord.getPitches().length];
        for(int i = 0; i < chord.getPitches().length; i++) {
            notes[i] = new Note();
            notes[i].setLength(ChordsPanel.getNoteLength());
            notes[i].setPitch(chord.getPitches()[i]);
            notes[i].setStartTick(0);
        }
        App.eventBus.post(new AddNotesToTrackEvent(notes));

    }

}
