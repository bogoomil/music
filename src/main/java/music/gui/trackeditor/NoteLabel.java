package music.gui.trackeditor;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.NoteDragStartEvent;
import music.event.NoteLabelDragEndEvent;
import music.event.NoteLabelDraggedEvent;
import music.event.NoteSelectionEvent;
import music.event.TickOffEvent;
import music.event.TickOnEvent;
import music.event.tracks.TrackNotesUpdatedEvent;
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

    private boolean selected;

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
                    App.eventBus.post(new NoteLabelDraggedEvent(id, e.getX() - startDragX));
                }
            }
        });

        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if(NoteLabel.this.isEnabled()) {
                    int x = getBounds().x;
                    int newCellIndex = trackPanel.getColByX(x);
                    snap(newCellIndex);
                    App.eventBus.post(new TrackNotesUpdatedEvent());
                    App.eventBus.post(new NoteLabelDragEndEvent(id, x));

                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(NoteLabel.this.isEnabled()) {
                    startDragX = e.getX();
                    startDragY = e.getY();
                    App.eventBus.post(new NoteDragStartEvent(id));
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
                    NoteLength old = note.getLength();
                    if(e.getX() > getWidth() - 15) {
                        NoteLength uj =NoteLength.ofErtek(note.getLength().getErtek() * 2);
                        note.setLength(uj);
                        reCalculateSizeAndLocation();
                        App.eventBus.post(new TrackNotesUpdatedEvent());

                    } else if (e.getX() < 15) {
                        if(old.getErtek() > 1) {
                            NoteLength uj = NoteLength.ofErtek(note.getLength().getErtek() / 2);
                            note.setLength(uj);
                            reCalculateSizeAndLocation();
                            App.eventBus.post(new TrackNotesUpdatedEvent());
                        }

                    } else {
                        if(e.getClickCount() == 2) {
                            Container c = NoteLabel.this.getParent();
                            c.remove(NoteLabel.this);
                            c.repaint();
                            App.eventBus.post(new TrackNotesUpdatedEvent());

                        }else {
                            setSelected(!selected);
                        }
                    }

                }
            }
        });
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        setBackground(selected ? selectColor : origColor);
        if(selected) {
            App.eventBus.post(new NoteSelectionEvent(note));
        }else {
            App.eventBus.post(new NoteSelectionEvent(null));
        }

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
        //g.drawString("" + id, 30, 15);

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

    @Subscribe
    void handleDragEvent(NoteLabelDraggedEvent e) {
        if(this.id != e.getId() && this.selected && this.isEnabled()) {
            this.setBounds(getX() + e.getX(), getY(), getWidth(), getHeight());
        }
    }

    @Subscribe
    void handleDragEndEvent(NoteLabelDragEndEvent e) {
        if(this.id != e.getId() && this.selected && this.isEnabled()) {
            int x = getBounds().x;
            int newCellIndex = trackPanel.getColByX(x);
            snap(newCellIndex);
        }

    }

    public boolean getSelected() {
        return selected;
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

}
