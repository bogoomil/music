package music.gui.trackeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.FillNotesEvent;
import music.event.tracks.AddMeasureToTrackEvent;
import music.event.tracks.AddNotesToTrackEvent;
import music.event.tracks.DeleteNotesFromTrackEvent;
import music.event.tracks.PlayTrackEvent;
import music.event.tracks.TrackNotesUpdatedEvent;
import music.event.tracks.TrackScrollEvent;
import music.event.tracks.TrackSelectedEvent;
import music.event.tracks.TrackVolumeChangedEvent;
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
        pnNorth.removeAll();
        if(track != null) {

            for(int i = 0; i < track.getMeasureNum(); i++) {
                JButton btnMeasure = new JButton("" + i);
                btnMeasure.setMargin(new Insets(2,2,2,2));
                btnMeasure.setFont(new Font("Dialog", Font.PLAIN, 9));
                pnNorth.add(btnMeasure);
                final int measureNum = i;
                btnMeasure.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        App.eventBus.post(new TrackScrollEvent(measureNum));
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
        trackPanel.setTrack(e.getTrack());
        this.updateButtons();

    }

    @Subscribe
    private void handleAddMeasureToTrackEvent(AddMeasureToTrackEvent e) {
        if(this.track == null) {
            this.track = new Track();

        }
        this.track.setMeasureNum(this.track.getMeasureNum() + 1);
        trackPanel.revalidate();
        trackPanel.repaint();
        this.updateButtons();

    }

    @Subscribe
    private void handleAddNotesToTrackEvent(AddNotesToTrackEvent e) {
        if(this.track == null) {
            this.track = new Track();
        }

        for(int i = 0; i < e.getNotes().length; i++) {
            e.getNotes()[i].setStartTick(e.getNotes()[i].getStartTick() + (trackPanel.getSelectedMeasureNum() * 32) );
        }

        this.track.getNotes().addAll(Arrays.asList(e.getNotes()));
        this.trackPanel.setSelectedMeasureNum(trackPanel.getSelectedMeasureNum() + 1);
        keyBoard.setMinOctave(track.getMinOctave());

        trackPanel.setTrack(track);
        this.updateButtons();

    }

    @Subscribe
    private void handleTrackNotesUpdatedEvent(TrackNotesUpdatedEvent e) {

        List<Note> notes = trackPanel.getNotes();
        this.track.setNotes(notes);
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
        trackPanel.setTrack(track);
    }

    @Subscribe
    private void handleFillNotesEvent(FillNotesEvent e) {
        System.out.println("filling notes: " + e.getLength() + " : " + e.getBeat());
        int counter = 0;

        while(counter < e.getMeasureNum() * 32) {
            Note n = new Note();
            n.setLength(e.getLength());
            n.setStartTick(counter + trackPanel.getSelectedMeasureNum());
            n.setPitch(e.getPitch());
            counter += e.getBeat().getErtek();
            track.getNotes().add(n);
        }
        trackPanel.setTrack(track);
    }
}
