package music.gui.measure;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import com.google.common.eventbus.Subscribe;

import music.event.MeasureNotesUpdatedEvent;
import music.event.NoteSelectionEvent;
import music.event.TickOffEvent;
import music.event.TickOnEvent;
import music.gui.MainFrame;
import music.theory.Note;
import music.theory.NoteLength;

public class NoteLabel extends JLabel {

    private Note note;
    TickRowEditorPanel trep;
    int startDragX;

    private Color origColor = Color.CYAN;
    private Color selectColor = Color.ORANGE;
    private Color playingColor = Color.PINK;

    private boolean selected;


    public NoteLabel(TickRowEditorPanel trep, Note note) {
        super();

        MainFrame.eventBus.register(this);

        setBorder(new LineBorder(new Color(0, 0, 0), 3, true));
        this.note = note;
        this.trep = trep;

        this.setOpaque(true);
        this.setBackground(Color.CYAN);
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
                }
            }
        });

        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if(NoteLabel.this.isEnabled()) {
                    int x = getBounds().x;
                    int newCellIndex = trep.getCellIndexByX(x);
                    snap(newCellIndex);
                    MainFrame.eventBus.post(new MeasureNotesUpdatedEvent());

                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(NoteLabel.this.isEnabled()) {
                    startDragX = e.getX();
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


                if(NoteLabel.this.isEnabled()) {

                    if(e.getClickCount() == 2) {
                        Container c = NoteLabel.this.getParent();
                        c.remove(NoteLabel.this);
                        c.repaint();
                        MainFrame.eventBus.post(new MeasureNotesUpdatedEvent());

                    }else {
                        NoteLength old = note.getLength();
                        if(e.getX() > getWidth() - 10) {
                            NoteLength uj =NoteLength.ofErtek(note.getLength().getErtek() * 2);
                            note.setLength(uj);
                            reCalculateSizeAndLocation();
                            MainFrame.eventBus.post(new MeasureNotesUpdatedEvent());

                        } else if (e.getX() < 10) {
                            if(old.getErtek() > 1) {
                                NoteLength uj = NoteLength.ofErtek(note.getLength().getErtek() / 2);
                                note.setLength(uj);
                                reCalculateSizeAndLocation();
                            }
                            MainFrame.eventBus.post(new MeasureNotesUpdatedEvent());

                        } else {
                            selected = !selected;
                            setBackground(selected ? selectColor : origColor);
                            if(selected) {
                                MainFrame.eventBus.post(new NoteSelectionEvent(note));
                            }else {
                                MainFrame.eventBus.post(new NoteSelectionEvent(null));
                            }
                        }
                    }
                }
            }
        });
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

        g.drawRect(0, 0, 10, this.getHeight());

        g.drawRect(this.getWidth() -10, 0, this.getWidth(), this.getHeight());

        if(note.getLength().getErtek() > 1) {
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, 9, this.getHeight());
        }

        g.setColor(Color.RED);
        g.fillRect(this.getWidth() -9, 0, this.getWidth(), this.getHeight());

    }

    public void reCalculateSizeAndLocation() {
        int x = trep.getCellXByIndex(note.getStartTick());
        int width = trep.getCellWidth() * note.getLength().getErtek();
        this.setBounds(x, 0, width, trep.getHeight());

    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    @Subscribe
    void handleTickOnEvent(TickOnEvent e) {
        if(e.getTick()  == note.getStartTick()) {
            setBackground(playingColor);
        }
    }

    @Subscribe
    void handleTickOffEvent(TickOffEvent e) {
        if(e.getTick()  == note.getStartTick()) {
            setBackground(selected ? selectColor : origColor);
        }
    }


}
