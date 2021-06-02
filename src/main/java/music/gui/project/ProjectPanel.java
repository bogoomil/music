package music.gui.project;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;

import music.App;
import music.event.AddMeasureToTrackEvent;
import music.event.DelMeasureFromTrackEvent;
import music.event.FileOpenEvent;
import music.event.FileSaveEvent;
import music.event.TrackSelectedEvent;
import music.gui.trackeditor.TrackEditorPanel;
import music.logic.MidiEngine;
import music.model.Project;
import music.model.Track;
import music.theory.Measure;

public class ProjectPanel extends JPanel {

    private TrackEditorPanel currentTrackEditor;

    private JPanel pnTracks;
    private List<Track> tracks = new ArrayList<>();
    private JSlider slTempo;
    private JComboBox cbTempoFactor;
    private JTextField txtTfprojcetname;

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
        txtTfprojcetname.setPreferredSize(new Dimension(190, 19));
        txtTfprojcetname.setText("noname");
        pnToolbar.add(txtTfprojcetname);
        txtTfprojcetname.setColumns(10);


        JButton btnPlay = new JButton("Play");
        btnPlay.setPreferredSize(new Dimension(95, 25));
        btnPlay.setBackground(App.GREEN);
        pnToolbar.add(btnPlay);

        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    play();
                } catch (InvalidMidiDataException | IOException | MidiUnavailableException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        });

        JButton btnStop = new JButton("Stop");
        btnStop.setPreferredSize(new Dimension(95, 25));
        btnStop.setBackground(App.RED);
        pnToolbar.add(btnStop);

        btnStop.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                MidiEngine.getSequencer().stop();

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

        JButton btnDel = new JButton("-");
        pnToolbar.add(btnDel);

        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delTrack();

            }
        });


        slTempo = new JSlider();

        final TitledBorder tbTempo = new TitledBorder(null, "Tempo", TitledBorder.LEADING, TitledBorder.TOP, null, null);


        slTempo = new JSlider();
        slTempo.setSnapToTicks(true);
        slTempo.setMinorTickSpacing(10);
        slTempo.setMajorTickSpacing(60);
        slTempo.setPaintLabels(true);
        slTempo.setPaintTicks(true);
        slTempo.setMinimum(60);
        slTempo.setBorder(tbTempo);
        slTempo.setMaximum(300);

        slTempo.setValue(200);

        slTempo.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tbTempo.setTitle("Tempo: " + slTempo.getValue());;

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

    }
    @Subscribe
    private void handleAddMeasureToTrackEvent(AddMeasureToTrackEvent e) {
        if(this.currentTrackEditor == null) {
            this.currentTrackEditor = this.createTrack();
        }

        this.currentTrackEditor.getTrack().addMeasure(e.getMeasure().clone());
        this.currentTrackEditor.refresh();
    }

    @Subscribe
    private void handleDelMeasureEvent(DelMeasureFromTrackEvent e) {
        Track t = this.getTrackById(e.getTrackId());
        if(t != null) {
            t.removeMeasure(e.getMeasure().getNum());
        }
        TrackEditorPanel trep = (TrackEditorPanel) this.pnTracks.getComponent(e.getTrackId());
        trep.removeMeasureButton(e.getMeasure().getNum());

        for(int i = 0; i < t.getMeasures().size(); i++) {
            t.getMeasures().get(i).setNum(i);
        }

        trep.validate();
        trep.repaint();
    }

    @Subscribe
    private void handleTrackSelectedEvent(TrackSelectedEvent e) {
        for(int i = 0; i < this.pnTracks.getComponentCount(); i++) {
            TrackEditorPanel tep = (TrackEditorPanel) this.pnTracks.getComponent(i);
            if(tep.getTrack().getId() == e.getTrackId()) {
                this.currentTrackEditor = tep;
            }
            tep.setSelected(tep.getTrack().getId() == e.getTrackId());
        }
    }

    private void play() throws InvalidMidiDataException, IOException, MidiUnavailableException {
        Sequence seq = new Sequence(Sequence.PPQ, MidiEngine.RESOLUTION);
        Sequencer sequencer = MidiEngine.getSequencer();
        float f = Float.parseFloat(cbTempoFactor.getSelectedItem() + "");
        sequencer.setTempoFactor(f);
        for(Track t :this.tracks) {
            javax.sound.midi.Track track = MidiEngine.getInstrumentTrack(seq, t.getChannel(), t.getInstrument());
            for(Measure m : t.getMeasures()) {
                MidiEngine.addNotesToTrack(track, t.getChannel(), m);
            }

        }
        sequencer.setSequence(seq);
        if(!sequencer.isOpen()) {
            sequencer.open();
        }
        //        sequencer.setLoopCount(3);
        sequencer.start();
        sequencer.setTempoInBPM(slTempo.getValue());
        File file = new File("piece.mid");
        MidiSystem.write(seq,1,file);

    }
    private TrackEditorPanel createTrack(Track track) {
        TrackEditorPanel tep = new TrackEditorPanel(track);
        tep.setSelected(true);
        tep.setTrack(track);
        tep.refresh();
        pnTracks.add(tep);
        pnTracks.validate();
        pnTracks.repaint();
        this.repaint();
        this.validate();
        return tep;
    }


    private TrackEditorPanel createTrack() {
        this.resetTrackSelection();
        Track track = new Track(this.tracks.size());
        tracks.add(track);
        TrackEditorPanel tep = new TrackEditorPanel(track);
        tep.setSelected(true);
        pnTracks.add(tep);
        pnTracks.validate();
        pnTracks.repaint();
        this.repaint();
        this.validate();
        return tep;
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
        this.currentTrackEditor = null;

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
        System.out.println(json);
        FileWriter writer = new FileWriter(e.getFile());
        writer.write(json);
        writer.flush();
        writer.close();
    }

    @Subscribe
    private void handleFileOpenEvent(FileOpenEvent e) throws JsonParseException, JsonMappingException, IOException {
        System.out.println("Opening: : " + e.getFile().getAbsolutePath());
        ObjectMapper om = new ObjectMapper();
        Project p = om.readValue(e.getFile(), Project.class);
        this.tracks = p.getTracks();

        pnTracks.removeAll();
        this.tracks.forEach(t -> pnTracks.add(createTrack(t)));

        this.repaint();
        this.validate();
    }

}
