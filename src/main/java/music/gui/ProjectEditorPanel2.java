package music.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.eventbus.Subscribe;

import music.event.AddMeasureToTrackEvent;
import music.event.PlayEvent;
import music.event.TrackSelectedEvent;
import music.gui.measure.MeasureEditorPanel;
import music.gui.trackeditor.TrackEditorPanel;
import music.logic.MidiEngine;
import music.model.Project;
import music.theory.Measure;
import music.theory.Track;

public class ProjectEditorPanel2 extends JPanel {

    private Project project;
    private JPanel pnTracks;

    private List<Track> tracks = new ArrayList<>();

    JScrollPane spTop;
    JScrollPane spBottom;

    TrackEditorPanel currentTrackEditor;
    private JSlider slVolume;
    private JSlider slTempo;

    public ProjectEditorPanel2() {
        super();
        MainFrame.eventBus.register(this);
        setLayout(new BorderLayout(0, 0));

        JPanel pnButtons = new JPanel();
        add(pnButtons, BorderLayout.NORTH);

        JButton btnAddTrack = new JButton("+");
        pnButtons.add(btnAddTrack);

        btnAddTrack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                currentTrackEditor = createTrack();

            }
        });

        JButton btnDel = new JButton("-");
        pnButtons.add(btnDel);

        MeasureEditorPanel measureEditor = new MeasureEditorPanel();

        pnTracks = new JPanel();
        pnTracks.setLayout(new BoxLayout(pnTracks, BoxLayout.Y_AXIS));


        spTop = new JScrollPane(pnTracks, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spBottom = new JScrollPane(measureEditor, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                spTop, spBottom);

        this.add(splitPane, BorderLayout.CENTER);

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
        pnButtons.add(slTempo);

        slVolume = new JSlider();
        slVolume.setMinorTickSpacing(5);
        slVolume.setPaintLabels(true);
        slVolume.setPaintTicks(true);
        slVolume.setSnapToTicks(true);
        slVolume.setMajorTickSpacing(20);
        TitledBorder tbVolume = new TitledBorder(null, "Volume", TitledBorder.LEADING, TitledBorder.TOP, null, null);
        slVolume.setBorder(tbVolume);
        slVolume.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                tbVolume.setTitle("Volume: " + slVolume.getValue());

            }
        });
        pnButtons.add(slVolume);


    }

    public void setProject(Project project) {
        this.project = project;
        project.getTracks().keySet().forEach(k -> {
            pnTracks.add(new TrackEditorPanel(project.getTracks().get(k)));
        });
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
    private void handleTrackSelectedEvent(TrackSelectedEvent e) {
        for(int i = 0; i < this.pnTracks.getComponentCount(); i++) {
            TrackEditorPanel tep = (TrackEditorPanel) this.pnTracks.getComponent(i);
            if(tep.getTrack().getId() == e.getTrackId()) {
                this.currentTrackEditor = tep;
            }
            tep.setSelected(tep.getTrack().getId() == e.getTrackId());
        }
    }

    @Subscribe
    private void handlePlayEvent(PlayEvent e) throws InvalidMidiDataException, IOException, MidiUnavailableException {
        Sequence seq = new Sequence(Sequence.PPQ, MidiEngine.RESOLUTION);

        Sequencer sequencer = MidiEngine.getSequencer();

        sequencer.setTempoInBPM(slTempo.getValue());
        sequencer.setTempoFactor(4);

        for(Track t :this.tracks) {
            javax.sound.midi.Track track = MidiEngine.getInstrumentTrack(seq, t.getChannel(), t.getInstrument());
            for(Measure m : t.getMeasures()) {
                MidiEngine.addNotesToTrack(track, t.getChannel(), m);
            }

        }
        System.out.println("Seq tempo: " + sequencer.getTempoInBPM() + ", factor: " + sequencer.getTempoFactor());
        sequencer.setSequence(seq);

        if(!sequencer.isOpen()) {
            sequencer.open();
        }

        sequencer.start();

        System.out.println("seq running: " + sequencer.isRunning());

        File f = new File("piece.mid");

        System.out.println("creating midi file: " + f.getAbsolutePath());

        MidiSystem.write(seq,1,f);

        //sequencer.close();

    }

    private TrackEditorPanel createTrack() {

        this.resetTrackSelection();

        Track track = new Track(this.tracks.size());
        tracks.add(track);
        TrackEditorPanel tep = new TrackEditorPanel(track);
        tep.setSelected(true);
        pnTracks.add(tep);
        pnTracks.validate();
        return tep;

    }

    private void resetTrackSelection() {
        for(int i = 0; i < this.pnTracks.getComponentCount(); i++) {
            TrackEditorPanel tep = (TrackEditorPanel) this.pnTracks.getComponent(i);
            tep.setSelected(false);
        }
        this.currentTrackEditor = null;

    }

}
