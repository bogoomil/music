package music.gui.trackeditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.AddNotesToTrackEvent;
import music.event.DeleteNotesFromTrackEvent;
import music.event.FillNotesEvent;
import music.event.PlayTrackEvent;
import music.event.TrackScrollEvent;
import music.event.TrackSelectedEvent;
import music.event.TrackVolumeChangedEvent;
import music.gui.project.ProjectPanel;
import music.logic.MidiEngine;
import music.model.Track;
import music.theory.Note;

public class TrackEditor extends JPanel {

    private TrackPropertiesPanel tpp;
    private TrackPanel trackPanel;
    private JPanel pnZoom;
    JScrollPane spMain;
    KeyBoard keyBoard;

    private Track track;
    private JPanel pnNorth;

    private boolean scrollLocked = false;

    public TrackEditor() {
        App.eventBus.register(this);
        setLayout(new BorderLayout(0, 0));

        initGui();
    }

    private void initGui() {

        this.removeAll();

        tpp = new TrackPropertiesPanel();
        this.add(tpp, BorderLayout.WEST);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout());

        keyBoard = new KeyBoard();
        panel.add(keyBoard, BorderLayout.WEST);

        keyBoard.setPreferredSize(new Dimension(100, 800));

        trackPanel = new TrackPanel();
        JPanel pnAbs = new JPanel();
        pnAbs.setLayout(null);
        pnAbs.add(trackPanel);

        trackPanel.setBounds(0, 4, 1000, 16 * 48);
        trackPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        panel.add(pnAbs, BorderLayout.CENTER);

        pnNorth = new JPanel();
        pnNorth.setBorder(new EtchedBorder());
        this.add(pnNorth, BorderLayout.NORTH);

        this.revalidate();
        this.repaint();
    }

    private void updateButtons() {

        for(Component c : pnNorth.getComponents()) {
            if(c instanceof MeasureButton) {
                MeasureButton mb = (MeasureButton) c;
                App.eventBus.unregister(mb);
            }
        }

        pnNorth.removeAll();

        JCheckBox cbScrollLock = new JCheckBox("Scroll lock");
        pnNorth.add(cbScrollLock);
        cbScrollLock.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                scrollLocked = cbScrollLock.isSelected();

            }
        });
        cbScrollLock.setSelected(scrollLocked);

        if(track != null) {

            for(int i = 0; i < track.getMeasureNum(); i++) {
                MeasureButton btnMeasure = new MeasureButton(i);
                btnMeasure.setMargin(new Insets(2,2,2,2));
                btnMeasure.setFont(new Font("Dialog", Font.PLAIN, 9));
                pnNorth.add(btnMeasure);
                final int measureNum = i;
                btnMeasure.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if(!scrollLocked) {
                            App.eventBus.post(new TrackScrollEvent(measureNum));
                        }
                    }
                });
            }
        }
        pnNorth.revalidate();
        pnNorth.repaint();
    }


    @Subscribe
    private void handleTrackSelectionEvent(TrackSelectedEvent e) {
        this.track = e.getTrack();
        keyBoard.setMinOctave(track.getMinOctave());
        trackPanel.setSelectedMeasureNum(0);
        trackPanel.refreshNoteLabels(e.getTrack());
        this.updateButtons();

    }

    @Subscribe
    private void handleAddNotesToTrackEvent(AddNotesToTrackEvent e) {
        if(this.track == null) {
            this.track = ProjectPanel.getTracks().get(0);
        }
        Arrays.asList(e.getNotes()).forEach(n -> n.setStartTick(n.getStartTick() + (trackPanel.getSelectedMeasureNum() * MidiEngine.TICKS_IN_MEASURE) ));

        this.track.getNotes().addAll(Arrays.asList(e.getNotes()));
        this.trackPanel.setSelectedMeasureNum(trackPanel.getSelectedMeasureNum() + 1);
        keyBoard.setMinOctave(track.getMinOctave());

        trackPanel.refreshNoteLabels(track);
        this.updateButtons();

    }

    @Subscribe
    private void handlePlayTrackEvent(PlayTrackEvent e) {
        MidiEngine.getSynth().getChannels()[e.getChannel()].programChange(e.getInstrument());
        MidiEngine.playTrack(track, MidiEngine.getSynth().getChannels()[e.getChannel()], e.getTempo());
    }


    @Subscribe
    private void handleTrackVolumeChangedEvent(TrackVolumeChangedEvent e) {
        track.getNotes().forEach(n -> n.setVol(e.getValue()));
    }

    @Subscribe
    private void handleDeleteNotesFromTrackEvent(DeleteNotesFromTrackEvent e) {
        track.setNotes(new ArrayList<>());
        trackPanel.refreshNoteLabels(track);
        trackPanel.setSelectedMeasureNum(0);
    }

    @Subscribe
    private void handleFillNotesEvent(FillNotesEvent e) {
        int counter = 0;

        while(counter < e.getMeasureNum()  * MidiEngine.TICKS_IN_MEASURE) {
            Note n = new Note();
            n.setLength(e.getLength());
            n.setStartTick(counter + MidiEngine.TICKS_IN_MEASURE * e.getFromMeasure());
            n.setPitch(e.getPitch());
            counter += e.getBeat().getErtek();
            track.getNotes().add(n);
        }
        trackPanel.refreshNoteLabels(track);
    }
}
