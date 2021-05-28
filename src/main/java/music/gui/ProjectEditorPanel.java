package music.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import music.gui.measureeditor.MeasureEditorPanel;
import music.gui.trackeditor.TrackEditorPanel;
import music.model.Project;

public class ProjectEditorPanel extends JPanel {

    private Project project;
    private JPanel pnTracks;

    public ProjectEditorPanel() {
        setLayout(new BorderLayout(0, 0));

        JPanel pnButtons = new JPanel();
        add(pnButtons, BorderLayout.NORTH);

        JButton btnAddTrack = new JButton("+");
        pnButtons.add(btnAddTrack);

        JButton btnDel = new JButton("-");
        pnButtons.add(btnDel);

        JPanel mesEditor = new JPanel();


        MeasureEditorPanel measureEditor = new MeasureEditorPanel();
        mesEditor.add(measureEditor);

        pnTracks = new JPanel();
        pnTracks.setLayout(new GridLayout(1, 0, 0, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                pnTracks, mesEditor);

        //        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);
        this.add(splitPane, BorderLayout.CENTER);

    }

    public void setProject(Project project) {
        this.project = project;
        project.getTracks().keySet().forEach(k -> {
            pnTracks.add(new TrackEditorPanel(project.getTracks().get(k)));
        });
    }

}
