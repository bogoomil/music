package music.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import music.gui.measureeditor.MeasureEditorPanel;
import music.model.Project;

public class ProjectEditorPanel extends JPanel {

    private Project project;
    private JPanel pnTracks;

    public ProjectEditorPanel() {
        setLayout(new BorderLayout(0, 0));

        pnTracks = new JPanel();
        add(pnTracks, BorderLayout.CENTER);
        pnTracks.setLayout(new GridLayout(1, 0, 0, 0));

        JPanel pnButtons = new JPanel();
        add(pnButtons, BorderLayout.NORTH);

        JButton btnAddTrack = new JButton("+");
        pnButtons.add(btnAddTrack);

        JButton btnDel = new JButton("-");
        pnButtons.add(btnDel);

        JPanel mesEditor = new JPanel();
        this.add(mesEditor, BorderLayout.SOUTH);

        MeasureEditorPanel measureEditor = new MeasureEditorPanel();
        mesEditor.add(measureEditor);
    }

    public void setProject(Project project) {
        this.project = project;
        project.getTracks().keySet().forEach(k -> {
            pnTracks.add(new TrackEditorPanel(project.getTracks().get(k)));
        });
    }

}
