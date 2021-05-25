package music.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;

import com.google.common.eventbus.Subscribe;

import music.event.MeasureSelectedEvent;
import music.logic.Player;
import music.theory.ChordType;
import music.theory.Measure;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;
import music.theory.Scale;

public class MeasureEditorPanel extends JPanel implements MouseListener{

    private Map<Pitch, PitchToggleButton[]> rows;

    private Measure measure;

    private JPanel buttons = new JPanel();

    private int currentInstrument;

    private JLabel lblHangnem;

    private JCheckBox chckbxEnableAllPitches;


    public MeasureEditorPanel() {
        super();
        MainFrame.eventBus.register(this);

        this.setLayout(new BorderLayout());

        this.add(buttons, BorderLayout.CENTER);

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
        cbInstr.setModel(new DefaultComboBoxModel(Player.getSynth().getAvailableInstruments()));
        cbInstr.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                currentInstrument = cbInstr.getItemAt(cbInstr.getSelectedIndex()).getPatch().getProgram();

            }
        });

        panel.add(cbInstr);


        UIManager.put("ToggleButton.select", new ColorUIResource( Color.RED ));
    }

    @Subscribe
    public void handleMeasureEvent(MeasureSelectedEvent ev) {


        this.setMeasure(ev.getMeasure());


    }

    private void setMeasure(Measure measure) {
        this.measure = measure;

        chckbxEnableAllPitches.setSelected(true);

        lblHangnem.setText(measure.getRoot().name() + " " + measure.getHangnem().name());


        GridLayout gl = new GridLayout(1 + (this.getOctaves(measure).size() * 12), 65);

        this.buttons.setLayout(gl);

        rows = new HashMap<>();

        this.buttons.removeAll();

        int counter = 0;
        for(int i = 0; i < 65; i++) {
            JLabel negyed = new JLabel() ;
            negyed.setOpaque(true);
            if((i - 1) % 8 == 0 ) {
                counter++;
                negyed.setText(counter + "/4");
            }
            //            if((i - 1) % 32 == 0 ) {
            //                negyed.setText("1");
            //            }
            this.buttons.add(negyed);
        }
        this.getOctaves(measure).forEach(oct -> {
            for(int i = 1; i < 13; i++) {

                NoteName nn = NoteName.values()[12 -i];
                JLabel bill = new JLabel(nn.name() + " (" + oct + ")");
                bill.setFont(new Font(Font.SERIF, Font.ITALIC, 8));
                bill.setOpaque(true);
                bill.setBorder(new LineBorder(new Color(0, 0, 0)));
                if(nn == NoteName.Ab || nn == NoteName.Bb || nn == NoteName.Cs || nn == NoteName.Eb || nn == NoteName.Fs) {
                    bill.setBackground(Color.BLACK);
                    bill.setForeground(Color.WHITE);
                } else {
                    bill.setBackground(Color.WHITE);
                    bill.setForeground(Color.BLACK);
                }
                this.buttons.add(bill);

                PitchToggleButton[] row = new PitchToggleButton[64];


                Pitch rowKey =new Pitch(nn.getMidiCode() + (oct * 12));

                this.rows.put(rowKey, row);

                for(int j = 0; j < 64; j++) {
                    PitchToggleButton btn = new PitchToggleButton(j, rowKey);

                    btn.setBounds(0, 0, 0, 0);
                    btn.setPreferredSize(new Dimension(10, 10));
                    row[j] = btn;

                    btn.setBackground(Color.BLUE);

                    btn.addMouseListener(this);
                    this.buttons.add(btn);
                }
            }

        });


        for(Note note : measure.getNotes()) {
            JToggleButton[] row = this.rows.get(note.getPitch());

            int endTick = note.getStartInTick() + ( 32 / note.getLength().getErtek());

            for(int i = note.getStartInTick(); i < endTick; i++) {
                row[i].setSelected(true);
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
        MidiChannel[] channels = Player.getSynth().getChannels();
        channels[Player.CHORD_CHANNEL].programChange(instrument);

        this.measure.setNotes(getNotes());

        Player.playMeasure(this.measure, channels[Player.CHORD_CHANNEL]);


    }

    private List<Note> getNotes(){
        List<Note> retVal = new ArrayList<>();
        rows.keySet().forEach(key -> {
            PitchToggleButton[] row = rows.get(key);

            for(int i = 0; i < row.length; i++) {
                if(!row[i].isSelected() || (i > 0 && row[i-1].isSelected())) {
                    continue;
                }
                int startTick = i;
                NoteLength length = getNoteLengthFromTick(row, startTick);
                Note note = new Note();
                note.setPitch(row[i].getPitch());
                note.setStartInTick(startTick);
                note.setLength(length);
                note.setVol(100);
                retVal.add(note);
            }
        });

        return retVal;

    }

    private NoteLength getNoteLengthFromTick(PitchToggleButton[] row, int startTick) {
        int harmincKettedCount = 0;
        for(int i = startTick; i < row.length; i++) {
            if(row[i].isSelected()) {
                harmincKettedCount++;
            }else {
                break;
            }
        }

        NoteLength length = NoteLength.ofErtek(32 / harmincKettedCount);

        return length;
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
            rows.keySet().forEach(p -> {
                if(!scaleNotes.contains(p.getName())) {
                    Arrays.asList(rows.get(p)).forEach(btn -> {
                        btn.setEnabled(selected);
                        if(!btn.isEnabled()) {
                            btn.setBackground(Color.GRAY);
                        }
                    });
                }
            });
        }
        this.validate();
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        if(arg0.isShiftDown()) {
            PitchToggleButton btn = (PitchToggleButton) arg0.getComponent();
            if(btn.isEnabled()) {
                btn.setSelected(!btn.isSelected());
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

}
