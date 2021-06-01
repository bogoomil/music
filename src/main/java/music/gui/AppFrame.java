package music.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import music.App;
import music.gui.chords.ChordsPanel;
import music.gui.measure.MeasureEditorPanel;
import music.gui.project.ProjectPanel;

public class AppFrame extends JFrame{
    public AppFrame() {

        App.eventBus.register(this);

        ProjectPanel pnTop = new ProjectPanel();

        JTabbedPane pnBottom  = new JTabbedPane();

        this.setJMenuBar(this.createMenu());

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                pnTop, pnBottom);

        getContentPane().add(splitPane, BorderLayout.CENTER);

        splitPane.setPreferredSize(new Dimension(1800,1000));

        ChordsPanel pnChords = new ChordsPanel();

        MeasureEditorPanel mep = new MeasureEditorPanel();

        JScrollPane spChords = new JScrollPane(pnChords, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnBottom.add(spChords, "Chords");

        JScrollPane spMeasure = new JScrollPane(mep, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnBottom.add(spMeasure, "Measure");


        this.pack();
        this.setVisible(true);

    }

    private JMenuBar createMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu mFile = new JMenu("File");
        mb.add(mFile);

        JMenuItem mntmNewMenuItem = new JMenuItem("Save");
        mFile.add(mntmNewMenuItem);


        JMenu mnEdit = new JMenu("Edit");
        mb.add(mnEdit);

        JMenuItem miPrefs = new JMenuItem("Preferences...");
        mnEdit.add(miPrefs);

        return mb;

    }


}
