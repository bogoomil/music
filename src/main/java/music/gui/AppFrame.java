package music.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import music.App;
import music.event.FileOpenEvent;
import music.event.FileSaveEvent;
import music.gui.chords.ChordsPanel;
import music.gui.project.ProjectPanel;
import music.gui.trackeditor.TrackEditor;
import music.logic.MidiEngine;

public class AppFrame extends JFrame{

    private JFileChooser fileChooser = new JFileChooser();

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


        JScrollPane spChords = new JScrollPane(pnChords, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnBottom.add(spChords, "Chords");

        TrackEditor te = new TrackEditor();
        JScrollPane spTrackEditor = new JScrollPane(te, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnBottom.add(spTrackEditor, "Track");



        setTitle("Music composer 1.0");

        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Window closing...");
                MidiEngine.getSynth().close();
                MidiEngine.getSequencer().close();
                System.out.println("synth, seq closed");
                System.exit(0);
            }

        });


    }


    private JMenuBar createMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu mFile = new JMenu("File");
        mb.add(mFile);

        JMenuItem mSave = new JMenuItem("Save");
        mFile.add(mSave);

        mSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showSaveDialog(AppFrame.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    App.eventBus.post(new FileSaveEvent(file));
                }
            }
        });

        JMenuItem mOpen = new JMenuItem("Open...");
        mFile.add(mOpen);

        mOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(AppFrame.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    App.eventBus.post(new FileOpenEvent(file));
                }
            }
        });



        JMenu mnEdit = new JMenu("Edit");
        mb.add(mnEdit);

        JMenuItem miPrefs = new JMenuItem("Preferences...");
        mnEdit.add(miPrefs);

        return mb;

    }


}
