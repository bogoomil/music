package music.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import music.theory.Track;

public class TrackEditorPanel extends JPanel {

    private Track track;

    public TrackEditorPanel(Track track) {
        setLayout(new GridLayout(1, 0, 0, 0));
        setTrack(track);
    }

    public void setTrack(Track track) {
        this.track = track;

    }

}
