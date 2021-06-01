package music.gui.trackeditor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import music.App;
import music.event.DelMeasureFromTrackEvent;
import music.event.MeasureSelectedEvent;
import music.event.PlayMeasureEvent;
import music.event.TrackSelectedEvent;
import music.theory.Measure;

public class MeasureButton extends JPanel {
    private Measure measure;
    private int trackId;
    private JLabel lbTitle;

    public MeasureButton(Measure measure, int trackId) {
        super();
        this.measure = measure;
        this.trackId = trackId;

        this.setLayout(new BorderLayout());

        JPanel pnCenter = new JPanel();
        this.add(pnCenter, BorderLayout.CENTER);


        lbTitle = new JLabel(trackId + "/" + measure.getNum());
        pnCenter.add(lbTitle);
        JButton btnEdit = new JButton("Edit");
        btnEdit.setMargin(new Insets(1, 1, 1, 1));
        btnEdit.setFont(new Font("Dialog", Font.BOLD, 9));

        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new MeasureSelectedEvent(measure));
                App.eventBus.post(new TrackSelectedEvent(trackId));

            }
        });

        this.add(btnEdit, BorderLayout.NORTH);


        JButton btnDel = new JButton("x");
        btnDel.setBackground(App.RED);
        btnDel.setMargin(new Insets(1, 1, 1, 1));
        btnDel.setFont(new Font("Dialog", Font.BOLD, 9));
        pnCenter.add(btnDel);

        JButton btnPlay = new JButton(">");
        btnPlay.setFont(new Font("Dialog", Font.BOLD, 9));
        pnCenter.add(btnPlay);
        btnPlay.setBackground(App.GREEN);
        btnPlay.setMargin(new Insets(1,1,1,1));
        btnPlay.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new PlayMeasureEvent(measure));

            }
        });

        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                App.eventBus.post(new DelMeasureFromTrackEvent(measure, trackId));

            }
        });

    }

    public Measure getMeasure() {
        return measure;
    }

    public void setMeasure(Measure measure) {
        this.measure = measure;
    }

    public void setTitle(String title) {
        this.lbTitle.setText(title);
    }


}
