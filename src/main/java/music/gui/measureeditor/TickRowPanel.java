package music.gui.measureeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import music.event.PianoKeyEvent;
import music.gui.MainFrame;
import music.gui.PianoKey;
import music.logic.MidiEngine;
import music.theory.Note;
import music.theory.NoteLength;
import music.theory.NoteName;
import music.theory.Pitch;

public class TickRowPanel extends JPanel {

    private static final Logger LOG = LoggerFactory.getLogger(TickRowPanel.class);
    private Pitch pitch;

    private JPanel buttons = new JPanel(new GridLayout(1, 0, 0, 0));

    public TickRowPanel() {
        this(new Pitch(NoteName.C.getMidiCode()).shift(3));
    }

    public TickRowPanel(Pitch pitch) {
        super();
        this.pitch = pitch;
        MainFrame.eventBus.register(this);

        setLayout(new BorderLayout());

        this.add(new PianoKey(pitch), BorderLayout.WEST);

        this.add(buttons, BorderLayout.CENTER);

        for(int i = 0; i < 128; i++) {
            JToggleButton btn = new JToggleButton();
            btn.setPreferredSize(new Dimension(10,10));
            buttons.add(btn);

            if((i) % MidiEngine.RESOLUTION == 0 ) {
                btn.setBackground(Color.CYAN);
            } else {
                btn.setBackground(Color.BLUE);
            }

            btn.addMouseListener(new MouseListener() {

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
                    //                    LOG.debug("mouse entered: {}", e.getButton());
                    if(e.isControlDown()) {
                        JToggleButton btn = (JToggleButton) e.getComponent();
                        if(btn.isEnabled()) {
                            btn.setSelected(!btn.isSelected());
                        }
                    }

                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    MainFrame.eventBus.post(new PianoKeyEvent(pitch));

                }
            });
        }

        //        this.addComponentListener(new ComponentListener() {
        //
        //            @Override
        //            public void componentShown(ComponentEvent e) {
        //                // TODO Auto-generated method stub
        //
        //            }
        //
        //            @Override
        //            public void componentResized(ComponentEvent e) {
        //                MainFrame.eventBus.post(new TickRowResizedEvent(e.getComponent().getWidth()));
        //
        //            }
        //
        //            @Override
        //            public void componentMoved(ComponentEvent e) {
        //                // TODO Auto-generated method stub
        //
        //            }
        //
        //            @Override
        //            public void componentHidden(ComponentEvent e) {
        //                // TODO Auto-generated method stub
        //
        //            }
        //        });
    }
    public List<Note> getNotes(int measureIndex){
        List<Note> retVal = new ArrayList<>();

        int startIndex = (measureIndex * MidiEngine.TICKS_IN_MEASURE);

        for(int i = startIndex; i < startIndex + MidiEngine.TICKS_IN_MEASURE; i++) {

            JToggleButton btn = (JToggleButton) this.buttons.getComponents()[i];
            if(!btn.isSelected() || (i % MidiEngine.TICKS_IN_MEASURE != 0 && (i > 0 && ((JToggleButton)this.buttons.getComponents()[i - 1]).isSelected()))) {
                continue;
            }
            int startTick = i;
            NoteLength length = getNoteLengthFromTick(startTick);
            Note note = new Note();
            note.setPitch(pitch);

            int relativeStartTick = startTick - startIndex;


            note.setRelativStartTick(relativeStartTick);
            note.setAbsoluteStartTick(startTick);
            note.setLength(length);
            retVal.add(note);
        }
        return retVal;
    }
    private NoteLength getNoteLengthFromTick(int startTick) {
        int harmincKettedCount = 0;
        for(int i = startTick; i < this.buttons.getComponents().length; i++) {

            JToggleButton btn = (JToggleButton) this.buttons.getComponent(i);

            if(btn.isSelected()) {
                harmincKettedCount++;
            }else {
                break;
            }
        }
        NoteLength length = null;
        if(harmincKettedCount > 32) {
            length = NoteLength.EGESZ;
        }else {
            length = NoteLength.ofErtek(harmincKettedCount);
        }

        if(length == null) {
            length = getNextLength(harmincKettedCount);
        }

        return length;
    }



    private NoteLength getNextLength(int harmincKettedCount) {
        while(harmincKettedCount < NoteLength.NEGYSZERES.getErtek()) {
            for(NoteLength l : NoteLength.values()) {
                NoteLength nl = NoteLength.ofErtek(harmincKettedCount);
                if(nl != null) {
                    return nl;
                }
                harmincKettedCount++;
            }
        }
        return NoteLength.NEGYSZERES;
    }

    public Pitch getPitch() {
        return pitch;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

    @Override
    public void setEnabled(boolean b) {
        for(Component c: buttons.getComponents()) {
            JToggleButton jtb = (JToggleButton) c;
            jtb.setEnabled(b);
            jtb.setBackground(Color.GRAY);
        }
    }

    public void setSelectedTick(int i) {
        JToggleButton btn = (JToggleButton) this.buttons.getComponent(i);
        btn.setSelected(true);

    }
}
