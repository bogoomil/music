package music.gui.trackeditor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.NoteDeletedEvent;
import music.event.NoteLabelDragEndEvent;
import music.event.NoteLabelDragEvent;
import music.gui.NoteLengthCombo;
import music.gui.VolumeSlider;
import music.theory.Note;
import music.theory.NoteLength;

public class NoteLabel extends JLabel {

    private static int ID;

    private Note note;
    int startDragX, startDragY;

    private int id;

    private Color origColor = App.DEFAULT_NOTE_LABEL_COLOR;
    private Color selectColor = App.SELECT_COLOR;
    private Color playingColor = App.PLAYING_COLOR;

    private TrackPanel trackPanel;


    public NoteLabel(TrackPanel trackPanel, Note note) {
        super();

        this.id = ID;
        ID++;
        App.eventBus.register(this);

        setBorder(new LineBorder(new Color(0, 0, 0), 3, true));
        this.note = note;
        this.trackPanel = trackPanel;

        this.setOpaque(true);
        this.setBackground(App.DEFAULT_NOTE_LABEL_COLOR);
        this.addMouseMotionListener(new MouseMotionListener() {


            @Override
            public void mouseMoved(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if(NoteLabel.this.isEnabled()) {
                    int x = getX();
                    x += e.getX() - startDragX;
                    NoteLabel.this.setBounds(x, getY(), getWidth(), getHeight());
                    App.eventBus.post(new NoteLabelDragEvent(id, e.getX() - startDragX));
                }
            }
        });

        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if(NoteLabel.this.isEnabled()) {
                    int x = getBounds().x;
                    int newCellIndex = trackPanel.getColByX(x);
                    note.setStartTick(newCellIndex);
                    snap(newCellIndex);
                    //App.eventBus.post(new TrackNotesUpdatedEvent());
                    App.eventBus.post(new NoteLabelDragEndEvent(id, x));

                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(NoteLabel.this.isEnabled()) {
                    startDragX = e.getX();
                    startDragY = e.getY();
                    //                    App.eventBus.post(new NoteDragStartEvent(id));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    if(NoteLabel.this.isEnabled()) {
                        NoteLength old = note.getLength();
                        if(e.getX() > getWidth() - 15) {
                            NoteLength uj = NoteLength.ofErtek(note.getLength().getErtek() * 2);
                            note.setLength(uj);
                            reCalculateSizeAndLocation();
                            //App.eventBus.post(new TrackNotesUpdatedEvent());

                        } else if (e.getX() < 15) {
                            if(old.getErtek() > 1) {
                                NoteLength uj = NoteLength.ofErtek(note.getLength().getErtek() / 2);
                                note.setLength(uj);
                                reCalculateSizeAndLocation();
                                //App.eventBus.post(new TrackNotesUpdatedEvent());
                            }

                        } else {
                            if(e.getClickCount() == 2) {
                                Container c = NoteLabel.this.getParent();
                                c.remove(NoteLabel.this);
                                c.repaint();
                                App.eventBus.post(new NoteDeletedEvent(note));

                            }else {
                                setSelected(!note.isSelected());
                            }
                        }
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    showPopup(e.getPoint());
                }
            }
        });
    }

    public void setSelected(boolean selected) {
        this.note.setSelected(selected);
        revalidate();
        repaint();

    }

    private void snap(int cellIndex) {
        if(cellIndex < 0) {
            cellIndex = 0;
        }
        note.setStartTick(cellIndex);
        this.reCalculateSizeAndLocation();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);

        g.drawRect(0, 0, 15, this.getHeight());


        if(note.getLength().getErtek() > 1) {
            g.setColor(App.RED);
            g.fillRect(0, 0, 15, this.getHeight());
        }

        g.setColor(Color.BLACK);
        g.drawRect(this.getWidth() -15, 0, this.getWidth(), this.getHeight());
        g.setColor(App.GREEN);
        g.fillRect(this.getWidth() -14, 0, this.getWidth(), this.getHeight());

        g.setColor(Color.black);

        setBackground(note.isSelected() ? selectColor : origColor);
        setBorder(note.isSelected() ? new LineBorder(selectColor, 3, true) : new LineBorder(new Color(0, 0, 0), 3, true));

    }

    public void reCalculateSizeAndLocation() {
        int x = trackPanel.getXByCol(note.getStartTick());
        int width = trackPanel.getTickWidth() * note.getLength().getErtek();
        int y = trackPanel.getYByRow(trackPanel.getRowByPitch(note.getPitch()));

        this.setBounds(x, y, width, trackPanel.getRowHeight());

    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    @Subscribe
    void handleDragEvent(NoteLabelDragEvent e) {
        if(this.id != e.getId() && this.note.isSelected() && this.isEnabled()) {
            this.setBounds(getX() + e.getX(), getY(), getWidth(), getHeight());
        }
    }

    @Subscribe
    void handleDragEndEvent(NoteLabelDragEndEvent e) {
        if(this.id != e.getId() && this.note.isSelected() && this.isEnabled()) {
            int x = getBounds().x;
            int newCellIndex = trackPanel.getColByX(x);
            note.setStartTick(newCellIndex);
            snap(newCellIndex);
        }

    }

    public boolean getSelected() {
        return note.isSelected();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((note == null) ? 0 : note.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NoteLabel other = (NoteLabel) obj;
        if (note == null) {
            if (other.note != null) {
                return false;
            }
        } else if (!note.equals(other.note)) {
            return false;
        }
        return true;
    }

    private void showPopup(Point pos){
        JDialog dialog = new JDialog();
        dialog.setTitle("Note props");

        JPanel pn = new JPanel();
        pn.setLayout(new GridLayout(0,2));

        JLabel l = new JLabel("Note length:");
        pn.add(l);


        JComboBox<NoteLength> cbHossz = new NoteLengthCombo();
        cbHossz.setSelectedItem(note.getLength());
        cbHossz.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                note.setLength(cbHossz.getItemAt(cbHossz.getSelectedIndex()));
                reCalculateSizeAndLocation();

            }
        });
        pn.add(cbHossz);

        l = new JLabel("Volume");
        pn.add(l);

        JSlider slVolume = new VolumeSlider();

        slVolume.setValue(note.getVol());

        pn.add(slVolume);

        slVolume.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                note.setVol(slVolume.getValue());
            }
        });

        l = new JLabel("Starttick:");
        pn.add(l);

        l = new JLabel("" + note.getStartTick());
        pn.add(l);

        JButton btnOk = new JButton("Close");
        pn.add(btnOk);
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });


        dialog.add(pn);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

    }


}
