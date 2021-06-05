package music.gui.trackeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.tracks.KeyBoardClearButtonEvent;
import music.event.tracks.KeyBoardSelectButtonEvent;
import music.event.tracks.TrackNotesUpdatedEvent;
import music.event.tracks.TrackScrollEvent;
import music.event.tracks.ZoomEvent;
import music.model.Track;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.Pitch;

public class TrackPanel extends JPanel {

    private Point selectedCell;

    private List<Pitch> pitches;

    private Track track;

    private int currentMeasure;

    private List<Note> copyNotes = new ArrayList<>();

    private List<Component> selectingComponents = new ArrayList<>();

    private boolean isSelectedAll;

    private boolean isRowsEnabled = true;

    public TrackPanel() {
        super();
        this.setLayout(null);
        App.eventBus.register(this);
        setBorder(new EtchedBorder());
        setFocusable(true);

        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(e.isControlDown() + " " + e.getKeyCode());
                if(e.getKeyCode() == 67 && e.isControlDown()) {
                    //ctrl c
                    copyNotes  = new ArrayList<>();
                    for(Component c : getComponents()) {
                        if(c instanceof NoteLabel) {
                            NoteLabel nl = (NoteLabel) c;
                            copyNotes.add(nl.getNote().clone());
                        }
                    }

                } else if(e.getKeyCode() == 86 && e.isControlDown()) {
                    //ctrl v
                    for(Note n : copyNotes) {
                        n.setStartTick(n.getStartTick() + (getSelectedMeasureNum() * 32));
                    }
                    track.getNotes().addAll(copyNotes);
                    setTrack(track);

                }else if(e.getKeyCode() == 65 && e.isControlDown()) {
                    // ctrl A
                    for(Component c : getComponents()) {
                        if(c instanceof NoteLabel) {
                            NoteLabel nl = (NoteLabel) c;
                            nl.setSelected(!isSelectedAll);
                        }
                    }
                    isSelectedAll = !isSelectedAll;
                }else if(e.getKeyCode() == 127) { // del
                    for(Component c : getComponents()) {
                        if(c instanceof NoteLabel) {
                            NoteLabel nl = (NoteLabel) c;
                            if(nl.getSelected()) {
                                track.getNotes().remove(nl.getNote());
                            }
                        }
                    }
                    setTrack(track);
                }else if(e.getKeyCode() == 38) {//up
                    int add = e.isControlDown() ? 12 : 1;
                    for(Component c : getComponents()) {
                        if(c instanceof NoteLabel) {
                            NoteLabel nl = (NoteLabel) c;
                            if(nl.getSelected()) {
                                nl.getNote().setPitch(new Pitch(nl.getNote().getMidiCode() + add));

                                nl.reCalculateSizeAndLocation();
                            }
                        }
                    }
                }else if(e.getKeyCode() == 40) {//down
                    // del
                    int add = e.isControlDown() ? 12 : 1;
                    for(Component c : getComponents()) {
                        if(c instanceof NoteLabel) {
                            NoteLabel nl = (NoteLabel) c;
                            if(nl.getSelected()) {
                                nl.getNote().setPitch(new Pitch(nl.getNote().getMidiCode() - add));
                                nl.reCalculateSizeAndLocation();
                            }
                        }
                    }
                }else if(e.getKeyCode() == 68 && e.isControlDown()) {//ctrl d enable / disable rows
                    isRowsEnabled = !isRowsEnabled;

                }
            }
        });

        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                requestFocusInWindow();

                for(Component c : selectingComponents) {
                    NoteLabel nl = (NoteLabel) c;
                    nl.setSelected(!nl.getSelected());
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {
                selectingComponents = new ArrayList<>();
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

                int row = getRowByY(e.getY());
                int col = getColByX(e.getX());


                if(e.isControlDown()) {
                    Note note = new Note();
                    note.setPitch(pitches.get(row));
                    note.setStartTick(col);
                    note.setLength(NoteLength.HARMICKETTED);
                    NoteLabel l = new NoteLabel(TrackPanel.this, note);

                    int x = getXByCol(col);
                    int y = getYByRow(row);
                    int w = getTickWidth();
                    int h = getRowHeight();
                    l.setBounds(x, y, w, h);
                    add(l);
                    App.eventBus.post(new TrackNotesUpdatedEvent());

                } else {
                    selectedCell = new Point(getColByX(e.getX()), getRowByY(e.getY() ));
                }

                System.out.println("row: " + getRowByY(e.getY() ) + ", col: " + getColByX(e.getX()));

                repaint();
                revalidate();
            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseMoved(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentPos = new Point(e.getX(), e.getY());
                Component c = TrackPanel.this.getComponentAt(currentPos);
                if(c instanceof NoteLabel) {
                    if(! selectingComponents.contains(c)) {
                        System.out.println("Adding comp: " + c);
                        selectingComponents.add(c);
                    }
                }
            }
        });
    }

    private double zoomFactor = 100;

    public Track getTrack() {
        return track;
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        this.setPreferredSize(new Dimension(100, 400));
        int incr = this.getTickWidth();
        if(this.track != null) {

            for(int i = 0; i < track.getMeasureNum() * 32; i++) {
                int x = i * incr;
                if(i % 32 == 0) {
                    g.drawString("" + i / 32, x  + 5, 15);

                    g.setColor(Color.BLUE);
                    g.drawLine(x-1, 0, x-1, this.getHeight());
                    g.drawLine(x+1, 0, x+1, this.getHeight());
                }else if(i % 16 == 0) {
                    g.setColor(Color.GREEN);
                }else if(i % 8 == 0) {
                    g.setColor(Color.RED);
                }else {
                    g.setColor(Color.BLACK);
                }
                g.drawLine(x, 0, x, this.getHeight());
            }
        }

        g.setColor(Color.BLACK);

        int rowHeight = this.getHeight() / 48;
        for(int i = 0; i < 48; i++) {
            int y = i * rowHeight;
            g.drawLine(0, y, this.getWidth(), y);

        }
        if(this.selectedCell != null) {
            g.setColor(App.SELECT_COLOR);
            g.fillRect(this.getXByCol(selectedCell.x), this.getYByRow(selectedCell.y), this.getTickWidth(), this.getRowHeight());
        }


        if(track != null) {
            int newWidth = incr * track.getMeasureNum() * 32;
            int x = this.currentMeasure * (getTickWidth() * 32);
            this.setBounds(-1 * x, this.getBounds().y, newWidth, this.getBounds().height);

        }
    }

    public int getTickWidth() {
        int tickWidth = (int) (50 * (zoomFactor / 100));
        return tickWidth;
    }

    public int getRowHeight() {
        return this.getHeight() / 48;
    }

    public void setZoomFactor(int zoomFactor) {
        this.zoomFactor = zoomFactor;
    }

    @Subscribe
    private void handleZoomEvent(ZoomEvent e) {

        setZoomFactor(e.getValue());

        for(int i= 0; i < this.getComponentCount(); i++) {
            NoteLabel nl = (NoteLabel) this.getComponent(i);
            nl.reCalculateSizeAndLocation();
        }

        validate();

        revalidate();
        repaint();

    }

    @Subscribe
    private void handleTrackScrollEvent(TrackScrollEvent e) {

        this.currentMeasure = e.getMeasureNum();

        int x = this.currentMeasure * (getTickWidth() * 32);

        this.setBounds(-1 * x, this.getY(), this.getWidth(), this.getHeight());

        for(int i= 0; i < this.getComponentCount(); i++) {
            NoteLabel nl = (NoteLabel) this.getComponent(i);
            nl.reCalculateSizeAndLocation();
        }


        revalidate();
        repaint();
    }

    public int getColByX(int x) {
        return x / this.getTickWidth();
    }

    public int getRowByY(int y) {
        return y / getRowHeight();
    }

    public int getXByCol(int col) {
        return this.getTickWidth() * col;
    }

    public int getYByRow(int row) {
        return this.getRowHeight() * row;
    }


    //    public List<Pitch> getPitches(){
    //        return this.pitches;
    //    }
    //
    public void setPitches(List<Pitch> pitches) {
        this.pitches = pitches;
    }

    public List<Note> getNotes(){
        List<Note> retVal = new ArrayList<>();
        for(int i= 0; i < this.getComponentCount(); i++) {
            NoteLabel nl = (NoteLabel) this.getComponent(i);
            retVal.add(nl.getNote());
        }

        return retVal;
    }

    public void setTrack(Track track) {
        this.track = track;
        removeAll();
        for(Note n : track.getNotes()) {
            NoteLabel l = new NoteLabel(TrackPanel.this, n);

            add(l);
            l.reCalculateSizeAndLocation();

            if(!Arrays.asList(this.getComponents()).contains(l)) {
            }
        }
        requestFocusInWindow();
        revalidate();
        repaint();
    }

    @Subscribe
    void handleClearEvent(KeyBoardClearButtonEvent e) {
        track.removePitches(e.getPitch());
        setTrack(track);
    }

    @Subscribe
    void handleSelectEvent(KeyBoardSelectButtonEvent e) {
        for(int i= 0; i < this.getComponentCount(); i++) {
            NoteLabel nl = (NoteLabel) this.getComponent(i);
            if(nl.getNote().getPitch().getMidiCode() == e.getPitch().getMidiCode()) {
                nl.setSelected(!nl.getSelected());
            }
        }
    }

    public int getRowByPitch(Pitch p) {
        Collections.sort(pitches, new Comparator<Pitch>() {

            @Override
            public int compare(Pitch o1, Pitch o2) {
                // TODO Auto-generated method stub
                return Integer.compare(o1.getMidiCode(), o2.getMidiCode());
            }
        });
        Collections.reverse(pitches);
        for(int i = 0; i < pitches.size(); i++) {
            if(p.getMidiCode() == pitches.get(i).getMidiCode()) {
                return i;
            }
        }
        return 0;
    }

    public int getSelectedMeasureNum() {
        if(this.selectedCell != null) {
            int col = selectedCell.x;
            return col / 32;
        }else {
            return 0;
        }
    }

}