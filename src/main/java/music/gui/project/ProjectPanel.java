package music.gui.project;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.DeleteMeasureEvent;
import music.event.DuplicateMeasureEvent;
import music.event.FileOpenEvent;
import music.event.FileSaveEvent;
import music.event.NoteDeletedEvent;
import music.event.ShiftNotesEvent;
import music.event.TrackSelectedEvent;
import music.gui.TempoSlider;
import music.gui.chords.ChordPanel;
import music.gui.trackeditor.TrackEditorPanel;
import music.gui.trackeditor.TrackPanel;
import music.logic.MidiEngine;
import music.model.Project;
import music.model.Track;

public class ProjectPanel extends JPanel {

    private TrackEditorPanel currentTrackEditor;

    private JPanel pnTracks;
    private static List<Track> tracks = new ArrayList<>();
    private JSlider slTempo = new TempoSlider();
    private JComboBox cbTempoFactor;
    private JTextField txtTfprojcetname;

    private int currentTick;

    public ProjectPanel() {
        super();
        App.eventBus.register(this);
        setLayout(new BorderLayout(0, 0));

        JPanel pnToolbar = new JPanel();
        FlowLayout flowLayout = (FlowLayout) pnToolbar.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        pnToolbar.setPreferredSize(new Dimension(210, 10));
        add(pnToolbar, BorderLayout.WEST);

        pnTracks = new JPanel();
        pnTracks.setLayout(new BoxLayout(pnTracks, BoxLayout.Y_AXIS));

        JScrollPane spTracks = new JScrollPane(pnTracks, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(spTracks, BorderLayout.CENTER);

        txtTfprojcetname = new JTextField();
        txtTfprojcetname.setPreferredSize(new Dimension(210, 19));
        txtTfprojcetname.setText("noname");
        pnToolbar.add(txtTfprojcetname);
        txtTfprojcetname.setColumns(10);


        JButton btnPlay = new JButton("Play");
        btnPlay.setPreferredSize(new Dimension(80, 25));
        btnPlay.setBackground(App.GREEN);
        pnToolbar.add(btnPlay);

        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MidiEngine.play(tracks, slTempo.getValue(), Float.parseFloat("" + cbTempoFactor.getItemAt(cbTempoFactor.getSelectedIndex())), TrackPanel.getCurrentTick());
                } catch (InvalidMidiDataException | IOException | MidiUnavailableException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        });

        JButton btnStop = new JButton("Stop");
        btnStop.setPreferredSize(new Dimension(80, 25));
        btnStop.setBackground(App.RED);
        pnToolbar.add(btnStop);

        btnStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MidiEngine.getSequencer().stop();
                MidiEngine.getSynth().close();

            }
        });

        JToggleButton btnRec = new JToggleButton("Rec");
        btnRec.setPreferredSize(new Dimension(80, 25));
        pnToolbar.add(btnRec);

        btnRec.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ChordPanel.setRecording(btnRec.isSelected());
            }
        });

        JButton btnAddTrack = new JButton("+");
        pnToolbar.add(btnAddTrack);

        btnAddTrack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                currentTrackEditor = createTrack();

            }
        });

        JButton btnDuplicateTrack = new JButton("x2");
        pnToolbar.add(btnDuplicateTrack);

        btnDuplicateTrack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                currentTrackEditor = duplicateTrack();

            }
        });

        JButton btnDel = new JButton("-");
        pnToolbar.add(btnDel);

        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delTrack();

            }
        });


        pnToolbar.add(slTempo);

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(180, 50));
        panel.setBorder(new TitledBorder(null, "Tempo factor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        pnToolbar.add(panel);

        cbTempoFactor = new JComboBox();
        panel.add(cbTempoFactor);
        cbTempoFactor.setModel(new DefaultComboBoxModel(new String[] {"0.1", "0.25", "0.5", "0.75", "1.0", "2.0", "3.0", "4.0"}));
        cbTempoFactor.setSelectedIndex(4);

        currentTrackEditor = this.createTrack();

    }

    private TrackEditorPanel duplicateTrack() {
        Track newTrack = this.currentTrackEditor.getTrack().clone();
        newTrack.setId(tracks.size());

        return createTrack(newTrack);
    }


    private TrackEditorPanel createTrack(Track track) {
        this.resetTrackSelection();

        tracks.add(track);
        TrackEditorPanel tep = new TrackEditorPanel(track);
        tep.setSelected(true);
        tep.setTrack(track);
        pnTracks.add(tep);
        pnTracks.validate();
        pnTracks.repaint();
        this.repaint();
        this.validate();
        return tep;
    }


    private TrackEditorPanel createTrack() {
        Track track = new Track(this.tracks.size());
        return createTrack(track);
    }

    private void resetTrackSelection() {
        for(int i = 0; i < this.pnTracks.getComponentCount(); i++) {
            TrackEditorPanel tep = (TrackEditorPanel) this.pnTracks.getComponent(i);
            tep.setSelected(false);
        }
        this.currentTrackEditor = null;

    }

    private void delTrack() {
        for(int i = 0; i < this.pnTracks.getComponentCount(); i++) {
            TrackEditorPanel tep = (TrackEditorPanel) this.pnTracks.getComponent(i);
            if(tep.isSelected()) {
                pnTracks.remove(i);
                tracks.remove(i);
            }
        }
        pnTracks.repaint();
        pnTracks.validate();
        if(pnTracks.getComponentCount() > 0) {
            this.currentTrackEditor = (TrackEditorPanel) pnTracks.getComponent(0) ;
        }

    }

    private Track getTrackById(int id) {
        for(int i = 0; i < this.tracks.size(); i++) {
            if(this.tracks.get(i).getId() == id) {
                return this.tracks.get(i);
            }
        }
        return null;
    }

    @Subscribe
    private void handleFileSaveEvent(FileSaveEvent e) throws IOException {

        Project project = new Project();
        project.setName(txtTfprojcetname.getText());
        project.setTracks(tracks);

        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(project);
        FileWriter writer = new FileWriter(e.getFile());
        writer.write(json);
        writer.flush();
        writer.close();
    }

    @Subscribe
    private void handleFileOpenEvent(FileOpenEvent e) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper om = new ObjectMapper();
        Project p = om.readValue(e.getFile(), Project.class);
        ProjectPanel.tracks = new ArrayList<>();
        pnTracks.removeAll();

        p.getTracks().forEach(t -> createTrack(t));

        txtTfprojcetname.setText(p.getName());

        this.repaint();
        this.validate();
    }


    public static List<Track> getTracks(){
        return tracks;
    }

    @Subscribe
    private void handleNoteDeletedEvent(NoteDeletedEvent e) {
        tracks.forEach(t -> {
            t.getNotes().remove(e.getNote());
        });
    }

    @Subscribe
    private void handleTrackSelectedEvent(TrackSelectedEvent e) {
        for(Component c : pnTracks.getComponents()) {
            TrackEditorPanel tep = (TrackEditorPanel) c;
            if(tep.getTrack().equals(e.getTrack())) {
                this.currentTrackEditor = tep;
            }
        }
    }

    @Subscribe
    private void handleDuplicateMeasureEvent(DuplicateMeasureEvent e) {
        tracks.forEach(t -> t.duplicateMeasure(e.getMeasureNum()));
    }

    @Subscribe
    private void handleShiftNotesEvent(ShiftNotesEvent e) {
        tracks.forEach(t -> t.shiftNotesFromMeasureBy(e.getMeasureNum(), e.getBy()));
    }

    @Subscribe
    private void handleDeleteMeasureEvent(DeleteMeasureEvent e) {
        tracks.forEach(t -> t.deleteMeasure(e.getMeasureNum()));
    }
}
