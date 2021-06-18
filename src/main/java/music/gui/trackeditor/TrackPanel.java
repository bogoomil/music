package music.gui.trackeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.ArpeggioEvent;
import music.event.DeleteMeasureEvent;
import music.event.DuplicateMeasureEvent;
import music.event.KeyBoardClearButtonEvent;
import music.event.KeyBoardSelectButtonEvent;
import music.event.RandomizeEvent;
import music.event.ShiftNotesEvent;
import music.event.TickOffEvent;
import music.event.TickOnEvent;
import music.event.TrackScrollEvent;
import music.event.ZoomEvent;
import music.logic.MidiEngine;
import music.model.Track;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;

public class TrackPanel extends JPanel {

    private Point selectedCell;

    private double zoomFactor = 20;

    private Track track;

    private int currentMeasure;

    private List<Note> copyNotes = new ArrayList<>();

    private List<Component> selectingComponents = new ArrayList<>();

    private boolean isSelectedAll;

    private boolean isRowsEnabled = true;

    private Point startDrag, endDrag;

    private static int currentTick;

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
                if(e.getKeyCode() == 67 && e.isControlDown()) {

                    //ctrl c
                    copy();

                } else if(e.getKeyCode() == 86 && e.isControlDown()) {
                    //ctrl v
                    paste();


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
                    refreshNoteLabels(track);
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
                    repaint();

                }
            }


        });

        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                requestFocusInWindow();

                if(endDrag != null) {
                    List<NoteLabel> nls = findAllBetween(startDrag, endDrag);

                    nls.forEach(n -> n.setSelected(!n.getSelected()));
                }



                //                for(Component c : selectingComponents) {
                //                    NoteLabel nl = (NoteLabel) c;
                //                    nl.setSelected(!nl.getSelected());
                //                }

            }

            @Override
            public void mousePressed(MouseEvent e) {
                startDrag = e.getPoint();
                endDrag = null;

                //                selectingComponents = new ArrayList<>();
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

                selectedCell = new Point(getColByX(e.getX()), getRowByY(e.getY() ));

                currentTick = getColByX(e.getX());

                if(e.getButton() == MouseEvent.BUTTON1) {
                    if(track != null) {
                        int row = getRowByY(e.getY());
                        int col = getColByX(e.getX());


                        if(e.getClickCount() == 2) {
                            Note note = new Note();
                            note.setPitch(KeyBoard.getPitches().get(row));
                            note.setStartTick(col);
                            note.setLength(NoteLength.HARMICKETTED);
                            NoteLabel l = new NoteLabel(TrackPanel.this, note);

                            int x = getXByCol(col);
                            int y = getYByRow(row);
                            int w = getTickWidth();
                            int h = getRowHeight();
                            l.setBounds(x, y, w, h);
                            add(l);

                            track.getNotes().add(note);

                        }

                    }else {
                        JOptionPane.showMessageDialog(TrackPanel.this, "Nincs kiválasztott track!", "Hiba", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    showPopup(e.getPoint());
                }
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

                endDrag = e.getPoint();

                //                Point currentPos = new Point(e.getX(), e.getY());
                //                Component c = TrackPanel.this.getComponentAt(currentPos);
                //                if(c instanceof NoteLabel) {
                //                    if(! selectingComponents.contains(c)) {
                //                        selectingComponents.add(c);
                //                    }
                //                }
            }
        });
    }
    public Track getTrack() {
        return track;
    }

    @Override
    public void paintComponent(Graphics g) {
        List<NoteName> scale = Arrays.asList(TrackPropertiesPanel.getScale()).stream().map(p -> p.getName()).collect(Collectors.toList());

        super.paintComponent(g);
        //this.setPreferredSize(new Dimension(100, 400));
        int incr = this.getTickWidth();
        if(this.track != null) {

            for(int i = 0; i < 1 + (track.getMeasureNum() * MidiEngine.TICKS_IN_MEASURE); i++) {
                int x = i * incr;
                if(i % MidiEngine.TICKS_IN_MEASURE == 0) {

                    //                    g.setColor(App.SELECT_COLOR);
                    //                    g..fillRect(0, 0, 20, 21);

                    g.setColor(Color.BLACK);
                    g.drawString("" + i / MidiEngine.TICKS_IN_MEASURE, x  + 5, 15);

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
            final int rowNum = i;
            int y = i * rowHeight;
            g.drawLine(0, y, this.getWidth(), y);

            getPitchByRow(i).ifPresent(p -> {
                if(!isRowsEnabled && !scale.contains(p.getName())) {
                    g.setColor(App.DISABLED_COLOR);

                    g.fillRect(0, getYByRow(rowNum), this.getWidth(), getRowHeight());

                }
            });

        }

        if(this.selectedCell != null) {
            g.setColor(App.SELECT_COLOR);
            g.fillRect(this.getXByCol(selectedCell.x), this.getYByRow(selectedCell.y), this.getTickWidth(), this.getRowHeight());
        }

        if(track != null && track.getMeasureNum() !=  0) {
            int newWidth = incr * track.getMeasureNum() * MidiEngine.TICKS_IN_MEASURE;
            int x = this.currentMeasure * (getTickWidth() * MidiEngine.TICKS_IN_MEASURE);
            this.setBounds(-1 * x, this.getBounds().y, newWidth, this.getBounds().height);
        }

        if(this.currentTick != 0) {
            g.setColor(Color.RED);
            g.fillRect(getXByCol(currentTick), 0, 2, this.getBounds().height);
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
    private void handleRandomizeEvent(RandomizeEvent e) {
        this.track.randomize(e.getSeed(), e.getMaxNoteLengthInTicks());
        refreshNoteLabels(track);
        revalidate();
        repaint();
    }

    @Subscribe
    private void handleArpeggioEvent(ArpeggioEvent e) {
        this.track.generateArpeggio(e.getShift(), e.getHossz(), e.getSzunet());
        refreshNoteLabels(track);
        revalidate();
        repaint();
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

        int x = this.currentMeasure * (getTickWidth() * MidiEngine.TICKS_IN_MEASURE);

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


    public List<Note> getNotes(){
        List<Note> retVal = new ArrayList<>();
        for(int i= 0; i < this.getComponentCount(); i++) {
            NoteLabel nl = (NoteLabel) this.getComponent(i);
            retVal.add(nl.getNote());
        }

        return retVal;
    }

    public void refreshNoteLabels(Track track) {
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
        refreshNoteLabels(track);
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
        for(int i = 0; i < KeyBoard.getPitches().size(); i++) {
            if(p.getMidiCode() == KeyBoard.getPitches().get(i).getMidiCode()) {
                return i;
            }
        }
        return 0;
    }

    public Optional<Pitch> getPitchByRow(int row) {
        return KeyBoard.getPitches() != null ?  Optional.of(KeyBoard.getPitches().get(row)) : Optional.empty();
    }

    public int getSelectedMeasureNum() {
        if(this.selectedCell != null) {
            int col = selectedCell.x;
            return col / MidiEngine.TICKS_IN_MEASURE;
        }else {
            return 0;
        }
    }

    public void setSelectedMeasureNum(int mn) {
        this.selectedCell = new Point(mn * MidiEngine.TICKS_IN_MEASURE, 0);
    }

    private List<NoteLabel> findAllBetween(Point start, Point end){
        List<NoteLabel> retVal = new ArrayList<>();

        for(Component c : getComponents()) {
            if(c instanceof NoteLabel) {
                NoteLabel nl = (NoteLabel) c;
                int width = endDrag.x - startDrag.x;
                int height = endDrag.y - startDrag.y;
                if(nl.getBounds().intersects(new Rectangle(startDrag, new Dimension(width, height)))) {
                    retVal.add(nl);
                }
            }
        }
        return retVal;
    }

    private void showPopup(Point pos) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Copy");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                copy();
            }
        });
        menu.add(item);

        item = new JMenuItem("Paste");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                paste();
            }
        });
        menu.add(item);

        menu.add(new JSeparator());

        item = new JMenuItem("Duplicate measure");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new DuplicateMeasureEvent(getSelectedMeasureNum()));
                //                track.duplicateMeasure(getSelectedMeasureNum());
                refreshNoteLabels(track);
            }
        });
        menu.add(item);


        item = new JMenuItem("Insert left");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new ShiftNotesEvent(getSelectedMeasureNum(), 1));
                //                track.shiftNotesFromMeasureBy(getSelectedMeasureNum(), 1);
                refreshNoteLabels(track);
            }
        });
        menu.add(item);

        item = new JMenuItem("Insert right");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new ShiftNotesEvent((getSelectedMeasureNum() + 1), 1));
                //                track.shiftNotesFromMeasureBy(getSelectedMeasureNum() - 1, 1);
                refreshNoteLabels(track);
            }
        });
        menu.add(item);

        item = new JMenuItem("Invert measure notes selection");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                getNoteLabelsByMeasureNum(getSelectedMeasureNum()).forEach(nl -> nl.setSelected(!nl.getSelected()));
                revalidate();
                repaint();
            }
        });
        menu.add(item);


        item = new JMenuItem("Delete measure");
        menu.add(item);

        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new DeleteMeasureEvent(getSelectedMeasureNum()));
                //                track.deleteMeasure(getSelectedMeasureNum());
                refreshNoteLabels(track);
            }
        });

        menu.show(this, pos.x, pos.y);
    }

    private List<NoteLabel> getNoteLabelsByMeasureNum(int measureNum){
        List<NoteLabel> retVal = new ArrayList<>();
        for(Component c : getComponents()) {
            NoteLabel nl = (NoteLabel) c;
            if(nl.getNote().getStartTick() >= measureNum * MidiEngine.TICKS_IN_MEASURE && nl.getNote().getStartTick() < (measureNum + 1) * MidiEngine.TICKS_IN_MEASURE) {
                retVal.add(nl);
            }
        }
        return retVal;
    }

    private List<Note> getCopyNotes() {
        List<Note> retVal = new ArrayList<>();
        for(Component c : getComponents()) {
            if(c instanceof NoteLabel) {
                NoteLabel nl = (NoteLabel) c;
                if(nl.getSelected()) {
                    Note copy = nl.getNote().clone();
                    copy.setSelected(true);
                    retVal.add(copy);
                    nl.setSelected(false);
                }
            }
        }
        Collections.sort(retVal, new Comparator<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                // TODO Auto-generated method stub
                return Integer.compare(o1.getStartTick(), o2.getStartTick());
            }
        });
        int offset = retVal.get(0).getStartTick() / MidiEngine.TICKS_IN_MEASURE;
        retVal.forEach(n -> {
            int modStartTick = n.getStartTick() - (offset * MidiEngine.TICKS_IN_MEASURE);
            n.setStartTick(modStartTick);
        });

        return retVal;
    }
    private void copy() {
        copyNotes  = getCopyNotes();

    }
    private void paste() {
        for(Component c : getComponents()) {
            if(c instanceof NoteLabel) {
                NoteLabel nl = (NoteLabel) c;
                nl.setSelected(false);
            }
        }

        if(copyNotes != null && copyNotes.size() > 0) {
            computeStartTickBySelectedNotes(copyNotes);
            track.getNotes().addAll(copyNotes);
            refreshNoteLabels(track);

            copyNotes = copyNotes.stream().map(n -> n.clone()).collect(Collectors.toList());
        } else {
            JOptionPane.showMessageDialog(this, "Nincs adat", "Hiba", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void computeStartTickBySelectedNotes(List<Note> notes) {
        //        notes.forEach(n -> {
        //
        //        });

        List<Integer> measures = new ArrayList<>();
        notes.forEach(n -> {
            if(!measures.contains(n.getStartTick() / MidiEngine.TICKS_IN_MEASURE)) {
                measures.add(n.getStartTick() / MidiEngine.TICKS_IN_MEASURE);
            }
        });
        Collections.sort(measures);

        notes.forEach(n -> {
            int selectedMeasureOffset = this.getSelectedMeasureNum() * MidiEngine.TICKS_IN_MEASURE;

            int noteMeasureOffset = measures.indexOf(n.getStartTick() / MidiEngine.TICKS_IN_MEASURE) * MidiEngine.TICKS_IN_MEASURE;

            n.setStartTick(n.getStartTickRelativeToMeasure() + selectedMeasureOffset + noteMeasureOffset);
        });
    }


    @Subscribe
    void handleTickOnEvent(TickOnEvent e) {
        currentTick = e.getTick();
        this.repaint();


    }

    @Subscribe
    void handleTickOffEvent(TickOffEvent e) {
        currentTick = 0;
        this.repaint();
    }

    public static int getCurrentTick() {
        return currentTick;
    }

}
