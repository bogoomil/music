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

import music.App;
import music.event.MeasureNotesUpdatedEvent;
import music.event.NoteDragStartEvent;
import music.event.NoteLabelDragEndEvent;
import music.event.NoteLabelDraggedEvent;
import music.event.NoteSelectionEvent;
import music.event.PianoKeyEvent;
import music.event.TickOffEvent;
import music.event.TickOnEvent;
import music.theory.Note;
import music.theory.NoteLength;

public class NoteLabel extends JLabel {

    private static int ID;

    private Note note;
    TickRowEditorPanel trep;
    int startDragX;

    private int id;

    private Color origColor = App.DEFAULT_NOTE_LABEL_COLOR;
    private Color selectColor = App.SELECT_COLOR;
    private Color playingColor = App.PLAYING_COLOR;

    private boolean selected;


    public NoteLabel(TickRowEditorPanel trep, Note note) {
        super();

        this.id = ID;
        ID++;
        App.eventBus.register(this);

        setBorder(new LineBorder(new Color(0, 0, 0), 3, true));
        this.note = note;
        this.trep = trep;

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
                    int newCellIndex = trep.getCellIndexByX(x);
                    snap(newCellIndex);
                    App.eventBus.post(new MeasureNotesUpdatedEvent());
                    App.eventBus.post(new NoteLabelDragEndEvent(id, x));

                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(NoteLabel.this.isEnabled()) {
                    startDragX = e.getX();
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
                    if(e.getX() > getWidth() - 10) {
                        NoteLength uj =NoteLength.ofErtek(note.getLength().getErtek() * 2);
                        note.setLength(uj);
                        reCalculateSizeAndLocation();
                        App.eventBus.post(new MeasureNotesUpdatedEvent());

                    } else if (e.getX() < 10) {
                        if(old.getErtek() > 1) {
                            NoteLength uj = NoteLength.ofErtek(note.getLength().getErtek() / 2);
                            note.setLength(uj);
                            reCalculateSizeAndLocation();
                        }
                        App.eventBus.post(new MeasureNotesUpdatedEvent());

                    } else {
                        if(e.getClickCount() == 2) {
                            Container c = NoteLabel.this.getParent();
                            c.remove(NoteLabel.this);
                            c.repaint();
                            App.eventBus.post(new MeasureNotesUpdatedEvent());

                        }else {
                            selected = !selected;
                            setBackground(selected ? selectColor : origColor);
                            if(selected) {
                                App.eventBus.post(new NoteSelectionEvent(note));
                            }else {
                                App.eventBus.post(new NoteSelectionEvent(null));
                            }
                        }
                    }

                    App.eventBus.post(new PianoKeyEvent(note.getPitch()));
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
            g.setColor(App.RED);
            g.fillRect(0, 0, 10, this.getHeight());
        }

        g.setColor(App.GREEN);
        g.fillRect(this.getWidth() -9, 0, this.getWidth(), this.getHeight());

        g.setColor(Color.black);
        g.drawString("" + id, 30, 15);

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

    @Subscribe
    void handleDragEvent(NoteLabelDraggedEvent e) {
        if(this.id != e.getId() && this.selected && this.isEnabled()) {
            this.setBounds(getX() + e.getX(), getY(), getWidth(), getHeight());
            System.out.println("id: " + id + "x: " + getBounds().x);

            this.note.setStartTick(trep.getCellIndexByX(this.getBounds().x));

        }
    }

    @Subscribe
    void handleDragEndEvent(NoteLabelDragEndEvent e) {
        if(this.id != e.getId() && this.selected && this.isEnabled()) {
            int x = getBounds().x;
            System.out.println("id: " + id +"x (end): " + getBounds().x);
            int newCellIndex = trep.getCellIndexByX(x);
            snap(newCellIndex);
        }

    }

    //    @Subscribe
    //    void handleDragStartEvent(NoteDragStartEvent e) {
    //        if(this.id != e.getId() && this.selected && this.isEnabled()) {
    //            startDragX = getBounds().x;
    //
    //        }
    //
    //    }

}
