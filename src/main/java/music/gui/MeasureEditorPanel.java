package music.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;

import com.google.common.eventbus.Subscribe;

import music.event.MeasureSelectedEvent;
import music.theory.Measure;
import music.theory.Note;
import music.theory.NoteName;
import music.theory.Pitch;

public class MeasureEditorPanel extends JPanel{

    private Map<Pitch, JToggleButton[]> rows;

    public MeasureEditorPanel() {
        super();
        MainFrame.eventBus.register(this);
    }

    @Subscribe
    public void setMeasure(MeasureSelectedEvent ev) {

        GridLayout gl = new GridLayout(1 + (this.getOctaves(ev.getMeasure()).size() * 12), 65);

        this.setLayout(gl);

        rows = new HashMap<>();

        this.removeAll();

        for(int i = 0; i < 65; i++) {
            JLabel negyed = new JLabel() ;
            negyed.setOpaque(true);
            if((i - 1) % 32 == 0 ) {
                negyed.setBackground(Color.green);
            }
            else if((i - 1) % 16 == 0 ) {
                negyed.setBackground(Color.red);
            }
            else if((i - 1) % 4 == 0 ) {
                negyed.setBackground(Color.black);
            }
            this.add(negyed);
        }



        this.getOctaves(ev.getMeasure()).forEach(oct -> {
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
                this.add(bill);

                JToggleButton[] row = new JToggleButton[64];


                Pitch rowKey =new Pitch(nn.getMidiCode() + (oct * 12));

                this.rows.put(rowKey, row);

                for(int j = 0; j < 64; j++) {
                    PitchToggleButton btn = new PitchToggleButton(j, rowKey);

                    btn.setBounds(0, 0, 0, 0);
                    btn.setPreferredSize(new Dimension(10, 10));
                    row[j] = btn;

                    btn.setBackground(Color.WHITE);

                    btn.addMouseListener(new MouseListener() {

                        @Override
                        public void mouseReleased(MouseEvent arg0) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void mousePressed(MouseEvent arg0) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void mouseExited(MouseEvent arg0) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void mouseEntered(MouseEvent arg0) {
                            if(arg0.isShiftDown()) {
                                PitchToggleButton btn = (PitchToggleButton) arg0.getComponent();
                                btn.setSelected(!btn.isSelected());
                            }
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void mouseClicked(MouseEvent arg0) {
                            // TODO Auto-generated method stub

                        }
                    });

                    this.add(btn);

                }
            }

        });


        for(Note note : ev.getMeasure().getNotes()) {
            JToggleButton[] row = this.rows.get(note.getPitch());

            int endTick = note.getStartInTick() + ( 32 / note.getLength().getErtek());

            for(int i = note.getStartInTick(); i < endTick; i++) {
                row[i].setSelected(true);
            }
        }
    }

    private void resetButtons() {

        rows.keySet().forEach(nn -> {
            JToggleButton[] row = rows.get(nn);
            for(int i = 0; i < row.length; i++) {
                row[i].setBackground(Color.CYAN);
                row[i].setSelected(false);
            }
        });
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


}
