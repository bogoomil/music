package music.gui.measure;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import music.event.MeasureNotesUpdatedEvent;
import music.gui.MainFrame;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.Pitch;

public class TickRowEditorPanel extends JPanel {

    private Pitch pitch;

    private List<Note> notes;

    private Color origColor;

    private Color disabledColor = Color.DARK_GRAY;

    public TickRowEditorPanel(Pitch pitch) {
        super();

        this.origColor = this.getBackground();

        this.pitch = pitch;
        setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setLayout(null);

        this.addComponentListener(new ComponentListener() {

            @Override
            public void componentShown(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void componentResized(ComponentEvent e) {
                for(int i = 0; i < TickRowEditorPanel.this.getComponents().length; i++) {
                    NoteLabel l = (NoteLabel) getComponent(i);
                    l.reCalculateSizeAndLocation();

                }

            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // TODO Auto-generated method stub

            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseMoved(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDragged(MouseEvent e) {


            }
        });

        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if(e.isControlDown()) {
                    if(TickRowEditorPanel.this.isEnabled()) {
                        Note note = new Note();
                        note.setPitch(pitch);
                        note.setStartTick(getCellIndexByX(e.getX()));
                        note.setLength(NoteLength.HARMICKETTED);

                        int cellIndex = getCellIndexByX(e.getX());
                        NoteLabel l = new NoteLabel(TickRowEditorPanel.this, note);
                        l.setBounds(getCellXByIndex(cellIndex), 0, getCellWidth(), TickRowEditorPanel.this.getHeight());
                        add(l);
                        validate();
                        repaint();

                        MainFrame.eventBus.post(new MeasureNotesUpdatedEvent());
                    }

                }
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(TickRowEditorPanel.this.isEnabled()) {
                    Note note = new Note();
                    note.setPitch(pitch);
                    note.setStartTick(getCellIndexByX(e.getX()));
                    note.setLength(NoteLength.HARMICKETTED);

                    int cellIndex = getCellIndexByX(e.getX());
                    NoteLabel l = new NoteLabel(TickRowEditorPanel.this, note);
                    l.setBounds(getCellXByIndex(cellIndex), 0, getCellWidth(), TickRowEditorPanel.this.getHeight());
                    add(l);
                    validate();
                    repaint();

                    MainFrame.eventBus.post(new MeasureNotesUpdatedEvent());
                }
            }
        });

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int incr = this.getWidth() / 32;
        int counter = 0;
        int x = 0;
        while(counter < 32) {
            if(counter % 8 == 0) {
                g.drawLine(x - 1, 0, x-1, this.getHeight());
                g.drawLine(x + 1, 0, x+1, this.getHeight());
            }
            g.drawLine(x, 0, x, this.getHeight());
            g.drawString("" + counter, x  + 5, 15);
            x += incr;
            counter++;
        }
        g.drawLine(x, 0, x, this.getHeight());
    }

    public int getCellIndexByX(int x) {
        int incr = this.getWidth() / 32;

        int cellIndex = x / incr;

        return cellIndex;
    }

    public int getCellXByIndex(int index) {
        int incr = this.getWidth() / 32;
        return incr * index;

    }

    public int getCellWidth() {
        return this.getWidth() / 32;
    }

    @Override
    public void setEnabled(boolean e) {
        super.setEnabled(e);
        this.setBackground(e ? origColor : disabledColor);
        for(int i = 0; i < this.getComponentCount(); i++) {
            this.getComponent(i).setEnabled(e);
        }

    }
}
